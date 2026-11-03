import { Component, OnInit, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import {
  FlowChart,
  FlowChartService,
  Health,
  ModuleClass,
  ValidationResult
} from './api';

@Component({
  imports: [],
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
      },
      error: (error: unknown) => {
        this.error.set(this.describeError(error));
        this.loading.set(false);
      }
    });
  }

  protected addMockModule(): void {
    const moduleClass = this.moduleClasses().find(moduleClass => moduleClass.builtIn);
    if (!moduleClass?.className) {
      return;
    }
    const moduleCount = this.flowChart()?.modules?.length ?? 0;
    this.api.addModule({
      addModuleRequest: {
        className: moduleClass.className,
        position: { x: 80 + moduleCount * 140, y: 80 }
      }
    }).subscribe({
      next: () => this.refresh(),
      error: (error: unknown) => this.error.set(this.describeError(error))
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
