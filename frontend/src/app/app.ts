import { Component, OnInit, inject, signal, viewChild } from '@angular/core';
import { forkJoin } from 'rxjs';
import {
  FlowChart,
  FlowChartService,
  Health,
  Module,
  ModuleClass,
  ValidationResult
} from './api';
import { ModulePaletteComponent, PaletteDragRequest } from './flow-chart/module-palette.component';
import { FlowChartGridComponent } from './flow-chart/flow-chart-grid.component';

@Component({
  imports: [ModulePaletteComponent, FlowChartGridComponent],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {
  private readonly api = inject(FlowChartService);

  protected readonly health = signal<Health | null>(null);
  protected readonly moduleClasses = signal<ModuleClass[]>([]);
  protected readonly flowChart = signal<FlowChart | null>(null);
  protected readonly validation = signal<ValidationResult | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);

  private readonly grid = viewChild<FlowChartGridComponent>('grid');

  ngOnInit(): void {
    this.refresh();
  }

  protected refresh(): void {
    this.loading.set(true);
    this.error.set(null);
    forkJoin({
      health: this.api.getHealth(),
      moduleClasses: this.api.getModuleClasses(),
      flowChart: this.api.getMainFlowChart()
    }).subscribe({
      next: ({ health, moduleClasses, flowChart }) => {
        this.health.set(health);
        this.moduleClasses.set(moduleClasses);
        this.flowChart.set(flowChart);
        this.loading.set(false);
        this.saving.set(false);
      },
      error: (error: unknown) => {
        this.error.set(this.describeError(error));
        this.loading.set(false);
        this.saving.set(false);
      }
    });
  }

  protected addModule(className: string, position: { x: number; y: number }): void {
    const chart = this.flowChart();
    if (!chart || !chart.id) {
      this.error.set('No flow chart available');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    this.api.addModuleToFlowChart({
      id: chart.id,
      addModuleRequest: { className, position }
    }).subscribe({
      next: module => {
        this.addModuleToChart(chart, module);
      },
      error: (error: unknown) => {
        this.error.set(this.describeError(error));
        this.saving.set(false);
      }
    });
  }

  private addModuleToChart(chart: FlowChart, module: Module): void {
    const modules = [...(chart.modules ?? [])];
    const others = modules.filter(m => m.id !== module.id);
    const overlap = others.find(m => this.modulesOverlap(module, m));
    this.saving.set(false);

    if (overlap) {
      // Do not show the module locally and remove it from the backend again.
      this.api.removeModuleFromFlowChart({
        id: chart.id!,
        moduleId: module.id!
      }).subscribe({
        error: () => {
          this.error.set('Module added but overlaps with another; rollback failed');
          this.refresh();
        }
      });
      this.error.set('That position is occupied by another module');
      return;
    }

    // Use the latest chart state: other requests may have completed in the meantime.
    const current = this.flowChart() ?? chart;
    this.flowChart.set({ ...current, modules: [...(current.modules ?? []), module] });
    this.validation.set(null);
  }

  private modulesOverlap(a: any, b: any): boolean {
    const ax = a.position?.x ?? 0;
    const ay = a.position?.y ?? 0;
    const aw = Math.max(1, a.width ?? 1);
    const ah = Math.max(1, a.height ?? 1);
    const bx = b.position?.x ?? 0;
    const by = b.position?.y ?? 0;
    const bw = Math.max(1, b.width ?? 1);
    const bh = Math.max(1, b.height ?? 1);
    return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
  }

  protected moveModule(moduleId: number, position: { x: number; y: number }): void {
    const chart = this.flowChart();
    if (!chart?.id) {
      this.error.set('No flow chart available');
      return;
    }
    const oldModules = [...(chart.modules ?? [])];
    // Optimistic update so the module immediately shows up at its new cell.
    const newModules = oldModules.map(m => m.id === moduleId ? { ...m, position } : m);
    this.flowChart.set({ ...chart, modules: newModules });
    this.saving.set(true);
    this.error.set(null);
    this.api.updateModule({
      id: chart.id,
      moduleId,
      updateModuleRequest: { position }
    }).subscribe({
      next: updated => {
        const current = this.flowChart();
        if (current) {
          this.flowChart.set({
            ...current,
            modules: (current.modules ?? []).map(m => m.id === moduleId ? updated : m)
          });
        }
        this.saving.set(false);
      },
      error: (error: unknown) => {
        this.flowChart.set({ ...chart, modules: oldModules });
        this.error.set(this.describeError(error));
        this.saving.set(false);
      }
    });
  }

  protected createMainFlowChart(): void {
    this.api.createMainFlowChart().subscribe({
      next: () => {
        this.validation.set(null);
        this.refresh();
      },
      error: (error: unknown) => this.error.set(this.describeError(error))
    });
  }

  protected validate(): void {
    this.api.validateMainFlowChart().subscribe({
      next: validation => this.validation.set(validation),
      error: (error: unknown) => this.error.set(this.describeError(error))
    });
  }

  protected onPaletteDragMoved(request: PaletteDragRequest): void {
    const grid = this.grid();
    if (grid) {
      grid.onExternalDragMoved(request.className, request.pointer);
    }
  }

  protected onPaletteDragEnded(request: PaletteDragRequest | null): void {
    // The grid validates the drop cell and emits (moduleDropped), which calls addModule().
    // Do not add the module here as well, otherwise it is created twice.
    this.grid()?.onExternalDragEnded(request?.className ?? '', request?.pointer ?? { x: 0, y: 0 });
  }

  private describeError(error: unknown): string {
    if (typeof error === 'object' && error !== null && 'error' in error) {
      const body = (error as { error?: { message?: string } }).error;
      if (body?.message) {
        return body.message;
      }
    }
    return error instanceof Error ? error.message : String(error);
  }
}
