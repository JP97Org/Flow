import { Component, input, output, signal, computed, ElementRef, viewChild } from '@angular/core';
import { CdkDrag, CdkDragEnd, CdkDragMove, CdkDragStart } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { FlowChart, Module } from '../api';
import {
  CELL_SIZE,
  GridPoint,
  GridRect,
  moduleFootprint,
  rectsOverlap,
  isCellOccupied,
  isFootprintFree,
  gridMetrics,
  cellFromClientPoint,
  snapToCell,
  cellToPixels,
  isCellInBounds,
} from './grid-geometry';

export interface ModuleDropRequest {
  className: string;
  position: { x: number; y: number };
}

export interface ModuleMoveRequest {
  moduleId: number;
  position: { x: number; y: number };
}

export interface MoveTarget {
  cell: GridPoint;
  width: number;
  height: number;
  valid: boolean;
}

export interface ExternalPreview {
  className: string;
  cell: GridPoint;
  valid: boolean;
}

@Component({
  selector: 'app-flow-chart-grid',
  imports: [CommonModule, CdkDrag],
  template: `
    <div
      #canvas
      class="grid-canvas"
      [style.width.px]="gridMetrics().widthPx"
      [style.height.px]="gridMetrics().heightPx"
      [style.grid-template-columns]="'repeat(' + gridMetrics().columns + ', ' + cellSize() + 'px)'"
      [style.grid-template-rows]="'repeat(' + gridMetrics().rows + ', ' + cellSize() + 'px)'"
      (click)="onCanvasClick($event)"
    >
      <div class="grid-background"></div>

      <!-- Existing modules -->
      @for (module of chartModules(); track module.id) {
        @if (module.id !== undefined) {
          <div
            cdkDrag
            [cdkDragData]="module"
            [cdkDragDisabled]="busy()"
            (cdkDragStarted)="onModuleDragStarted($event)"
            (cdkDragMoved)="onModuleDragMoved($event)"
            (cdkDragEnded)="onModuleDragEnded($event)"
            class="module-card"
            [class.module-occupied]="module.id === draggingModuleId()"
            [style.left.px]="(module.position?.x ?? 0) * cellSize()"
            [style.top.px]="(module.position?.y ?? 0) * cellSize()"
            [style.width.px]="(module.width || 1) * cellSize()"
            [style.height.px]="(module.height || 1) * cellSize()"
          >
            <div class="module-header">
              <strong>{{ module.name || module.className || '' }}</strong>
              <span>{{ module.className ? module.className.split('$').pop() : '' }}</span>
            </div>
          </div>
        }
      }

      <!-- Target footprint while moving an existing module -->
      @if (moveTarget(); as target) {
        <div
          class="module-preview"
          [class.invalid]="!target.valid"
          [style.left.px]="target.cell.x * cellSize()"
          [style.top.px]="target.cell.y * cellSize()"
          [style.width.px]="target.width * cellSize()"
          [style.height.px]="target.height * cellSize()"
        ></div>
      }

      <!-- Drag preview overlay for palette modules -->
      @if (externalPreview(); as preview) {
        <div
          class="module-preview"
          [class.invalid]="!preview.valid"
          [style.left.px]="preview.cell.x * cellSize()"
          [style.top.px]="preview.cell.y * cellSize()"
          [style.width.px]="cellSize()"
          [style.height.px]="cellSize()"
        ></div>
      }

      <!-- Hover highlight -->
      @if (hoverCell(); as cell) {
        <div
          class="cell-highlight"
          [style.left.px]="cell.x * cellSize()"
          [style.top.px]="cell.y * cellSize()"
          [style.width.px]="cellSize()"
          [style.height.px]="cellSize()"
        ></div>
      }

      <!-- Empty state -->
      @if (chartModules().length === 0) {
        <div class="empty-state">
          <p>Drag a module type from the palette onto the grid</p>
        </div>
      }
    </div>
  `,
  styles: [`
    .grid-canvas {
      position: relative;
      display: grid;
      gap: 0;
      overflow: auto;
      border-radius: 0.75rem;
      border: 1px solid rgba(148,163,184,0.34);
      background-color: #f8fafc;
      box-shadow: 0 12px 40px rgba(15,23,42,0.08);
      cursor: default;
      background:
        linear-gradient(transparent 95%, rgba(148,163,184,0.2) 95%),
        linear-gradient(90deg, transparent 95%, rgba(148,163,184,0.2) 95%);
      background-size: 100% 100%, 100% 100%;
    }

    .module-card {
      position: absolute;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 0.4rem;
      background: #1e293b;
      color: #e2e8f0;
      border-radius: 0.5rem;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      box-sizing: border-box;
      cursor: grab;
      touch-action: none;
      transition: box-shadow 0.2s;
      z-index: 10;
    }

    .module-card:hover {
      box-shadow: 0 6px 16px rgba(0,0,0,0.2);
    }

    .module-card.module-occupied {
      opacity: 0.6;
    }

    .module-header {
      text-align: center;
      width: 100%;
    }

    .module-header strong {
      display: block;
      font-size: 0.75rem;
      font-weight: 700;
    }

    .module-header span {
      font-size: 0.6rem;
      color: #94a3b8;
    }

    .module-preview {
      position: absolute;
      background: rgba(16,185,129,0.15);
      border: 2px dashed currentColor;
      border-radius: 0.5rem;
      pointer-events: none;
      z-index: 5;
    }

    .module-preview.invalid {
      background: rgba(239,68,68,0.15);
      border-color: rgba(239,68,68,0.5);
    }

    .cell-highlight {
      position: absolute;
      background: rgba(56,189,248,0.2);
      border-radius: 0.25rem;
      pointer-events: none;
      z-index: 5;
    }

    .empty-state {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #64748b;
      pointer-events: none;
    }

    .empty-state p {
      margin: 0;
      padding: 0.75rem 1rem;
      background: rgba(255,255,255,0.8);
      border-radius: 0.5rem;
    }
  `],
})
export class FlowChartGridComponent {
  readonly cellSize = input(CELL_SIZE);
  readonly busy = input(false);

  readonly flowChart = input.required<FlowChart>();

  readonly moduleDropped = output<ModuleDropRequest>();
  readonly moduleMoved = output<ModuleMoveRequest>();

  private readonly _hoverCell = signal<GridPoint | null>(null);
  readonly hoverCell = this._hoverCell.asReadonly();

  private readonly _draggingModuleId = signal<number | null>(null);
  readonly draggingModuleId = this._draggingModuleId.asReadonly();

  private readonly _externalPreview = signal<ExternalPreview | null>(null);
  readonly externalPreview = this._externalPreview.asReadonly();

  private readonly _moveTarget = signal<MoveTarget | null>(null);
  readonly moveTarget = this._moveTarget.asReadonly();

  readonly chartModules = computed(() => this.flowChart()?.modules ?? []);
  readonly gridMetrics = computed(() =>
    gridMetrics(this.chartModules(), this.cellSize())
  );

  onCanvasClick(event: MouseEvent): void {
    const canvas = this.canvas().nativeElement;
    this._hoverCell.set(cellFromClientPoint(event.clientX, event.clientY, canvas, this.cellSize()));
  }

  onModuleDragStarted(event: CdkDragStart): void {
    const module = event.source.data as Module;
    this._draggingModuleId.set(module.id ?? null);
    this._hoverCell.set(null);
  }

  onModuleDragMoved(event: CdkDragMove): void {
    const module = event.source.data as Module;
    this._moveTarget.set(this.computeMoveTarget(module, event.distance));
  }

  onModuleDragEnded(event: CdkDragEnd): void {
    const module = event.source.data as Module;
    const target = this.computeMoveTarget(module, event.distance);

    if (module.id !== undefined && target.valid) {
      const from = moduleFootprint(module);
      if (target.cell.x !== from.x || target.cell.y !== from.y) {
        // Emitting first lets the parent update the module position synchronously,
        // so it renders at the new cell as soon as the CDK transform is cleared below.
        this.moduleMoved.emit({ moduleId: module.id, position: target.cell });
      }
    }

    // The card is positioned via left/top; drop CDK's temporary transform.
    event.source.reset();
    this.resetDrag();
  }

  /**
   * Computes the cell a module would land in when dragged by the given pixel distance.
   * The origin is the module's own cell, so scrolling and canvas offsets do not matter.
   */
  private computeMoveTarget(module: Module, distance: { x: number; y: number }): MoveTarget {
    const fp = moduleFootprint(module);
    const cell = {
      x: fp.x + Math.round(distance.x / this.cellSize()),
      y: fp.y + Math.round(distance.y / this.cellSize()),
    };
    const { columns, rows } = this.gridMetrics();
    const inBounds = cell.x >= 0 && cell.y >= 0 &&
      cell.x + fp.width <= columns && cell.y + fp.height <= rows;
    const free = isFootprintFree(
      this.chartModules(),
      { x: cell.x, y: cell.y, width: fp.width, height: fp.height },
      module.id
    );
    return { cell, width: fp.width, height: fp.height, valid: inBounds && free };
  }

  onExternalDragMoved(className: string, pointer: { x: number; y: number }): void {
    const canvas = this.canvas().nativeElement;
    if (!canvas) return;
    const cell = cellFromClientPoint(pointer.x, pointer.y, canvas, this.cellSize());
    const chart = this.flowChart();
    if (!chart) {
      this._externalPreview.set({ className, cell, valid: false });
      return;
    }
    const target: GridRect = { x: cell.x, y: cell.y, width: 1, height: 1 };
    if (isFootprintFree(chart.modules ?? [], target) && isCellInBounds(cell, this.gridMetrics().columns, this.gridMetrics().rows)) {
      this._hoverCell.set(cell);
      this._externalPreview.set({ className, cell, valid: true });
    } else {
      this._hoverCell.set(null);
      this._externalPreview.set({ className, cell, valid: false });
    }
  }

  onExternalDragEnded(className: string, pointer: { x: number; y: number }): void {
    const canvas = this.canvas().nativeElement;
    if (!canvas) return;
    const cell = cellFromClientPoint(pointer.x, pointer.y, canvas, this.cellSize());
    const chart = this.flowChart();
    if (!chart) {
      this.resetExternal();
      return;
    }
    const target: GridRect = { x: cell.x, y: cell.y, width: 1, height: 1 };
    if (isFootprintFree(chart.modules ?? [], target) && isCellInBounds(cell, this.gridMetrics().columns, this.gridMetrics().rows)) {
      this.moduleDropped.emit({ className, position: cell });
    }
    this.resetExternal();
  }

  private resetDrag(): void {
    this._draggingModuleId.set(null);
    this._moveTarget.set(null);
    this._hoverCell.set(null);
    this._externalPreview.set(null);
  }

  private resetExternal(): void {
    this._hoverCell.set(null);
    this._externalPreview.set(null);
  }

  private readonly canvas = viewChild.required<ElementRef<HTMLElement>>('canvas');
}
