import {
  Component,
  HostListener,
  input,
  output,
  signal,
  computed,
  ElementRef,
  viewChild,
} from '@angular/core';
import { CdkDrag, CdkDragEnd, CdkDragMove, CdkDragStart } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Connection, FlowChart, Module, PinReference } from '../api';
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
import { DisplayPin, modulePinViews } from './pin-geometry';
import {
  PinConnectionRequest,
  PinSelection,
  canConnect,
  connectionRejectionReason,
  normalizeConnection,
} from './connection-rules';
import {
  ConnectionPathView,
  DiagramPoint,
  connectionPathViews,
  isRigidConnection,
  moveConnectionSegment,
  pathFromPoints,
} from './connection-geometry';

export interface ModuleDropRequest {
  className: string;
  position: { x: number; y: number };
}

export interface ModuleMoveRequest {
  moduleId: number;
  position: { x: number; y: number };
}

export interface ModuleDragEndInfo {
  moduleId: number;
  pointer: { x: number; y: number };
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

export interface ConnectionRejection {
  message: string;
}

export type GridSelection =
  { type: 'module'; moduleId: number } | { type: 'connection'; connectionId: number };

export interface ConnectionPathChangeRequest {
  connectionId: number;
  to: PinReference;
  diversionPoints: { x: number; y: number }[];
}

interface SegmentDrag {
  connectionId: number;
  to?: PinReference;
  routeIndex: number;
  segmentIndex: number;
  start: { x: number; y: number };
  originalPoints: DiagramPoint[];
  previewPoints: DiagramPoint[];
  endTrim: number;
  moved: boolean;
}

interface EditableSegment {
  key: string;
  connection: Connection;
  routeIndex: number;
  segmentIndex: number;
  start: DiagramPoint;
  end: DiagramPoint;
  horizontal: boolean;
}

@Component({
  selector: 'app-flow-chart-grid',
  imports: [CommonModule, CdkDrag],
  template: `
    <div
      #canvas
      class="grid-canvas"
      [class.is-dragging]="draggingModuleId() !== null"
      [style.width.px]="gridMetrics().widthPx"
      [style.height.px]="gridMetrics().heightPx"
      [style.grid-template-columns]="'repeat(' + gridMetrics().columns + ', ' + cellSize() + 'px)'"
      [style.grid-template-rows]="'repeat(' + gridMetrics().rows + ', ' + cellSize() + 'px)'"
      (click)="onCanvasClick($event)"
    >
      <div class="grid-background"></div>

      <svg
        class="connection-layer"
        [attr.width]="gridMetrics().widthPx"
        [attr.height]="gridMetrics().heightPx"
        [attr.viewBox]="'0 0 ' + gridMetrics().widthPx + ' ' + gridMetrics().heightPx"
      >
        <defs>
          <marker
            id="connection-arrow"
            viewBox="0 0 10 10"
            refX="10"
            refY="5"
            markerWidth="14"
            markerHeight="14"
            orient="auto"
            markerUnits="userSpaceOnUse"
          >
            <path d="M 0 0 L 10 5 L 0 10 z" fill="#0284c7"></path>
          </marker>
          <marker
            id="connection-arrow-selected"
            viewBox="0 0 10 10"
            refX="10"
            refY="5"
            markerWidth="14"
            markerHeight="14"
            orient="auto"
            markerUnits="userSpaceOnUse"
          >
            <path d="M 0 0 L 10 5 L 0 10 z" fill="#dc2626"></path>
          </marker>
        </defs>
        @for (view of connectionViews(); track view.connection.id) {
          @for (path of view.paths; track $index) {
            <path
              class="connection-hit"
              [attr.d]="path"
              (click)="onConnectionClick($event, view.connection)"
            ></path>
            <path
              class="connection-path"
              [class.connection-rigid]="isRigidConnection(view.connection)"
              [class.connection-selected]="isConnectionSelected(view.connection)"
              [attr.d]="path"
              [attr.marker-end]="connectionMarkerEnd(view.connection)"
            ></path>
          }
        }
        @for (segment of editableSegments(); track segment.key) {
          <line
            class="connection-segment-hit"
            [attr.x1]="segment.start.x"
            [attr.y1]="segment.start.y"
            [attr.x2]="segment.end.x"
            [attr.y2]="segment.end.y"
            [style.cursor]="segment.horizontal ? 'ns-resize' : 'ew-resize'"
            (pointerdown)="onSegmentPointerDown($event, segment)"
          ></line>
        }
      </svg>

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
            (click)="onModuleClick($event, module)"
            class="module-card"
            [class.module-occupied]="module.id === draggingModuleId()"
            [class.module-selected]="isModuleSelected(module)"
            [style.left.px]="(module.position?.x ?? 0) * cellSize()"
            [style.top.px]="(module.position?.y ?? 0) * cellSize()"
            [style.width.px]="(module.width || 1) * cellSize()"
            [style.height.px]="(module.height || 1) * cellSize()"
          >
            <div class="module-header">
              <strong>{{ module.name || module.className || '' }}</strong>
              <span>{{ module.className ? module.className.split('$').pop() : '' }}</span>
            </div>

            @for (pin of pinsByModule().get(module) ?? []; track pin.key) {
              <span
                class="module-pin"
                [class.pin-input]="pin.direction === 'input'"
                [class.pin-output]="pin.direction === 'output'"
                [class.pin-bidirectional]="pin.direction === 'bidirectional'"
                [class.pin-connected]="pin.connected"
                [class.pin-up]="pin.orientation === 'up'"
                [class.pin-left]="pin.orientation === 'left'"
                [class.pin-down]="pin.orientation === 'down'"
                [class.pin-right]="pin.orientation === 'right'"
                [class.pin-connectable]="!busy()"
                [class.pin-selected]="isPinSelected(module, pin)"
                [class.pin-target-valid]="isPinTargetValid(module, pin)"
                [style.left.px]="pin.left"
                [style.top.px]="pin.top"
                [style.width.px]="pin.size"
                [style.height.px]="pin.size"
                [attr.data-pin-key]="pin.key"
                [attr.data-module-id]="module.id"
                [attr.title]="pin.tooltip"
                [attr.aria-label]="pin.tooltip"
                (pointerdown)="onPinPointerDown($event)"
                (mousedown)="$event.stopPropagation()"
                (touchstart)="$event.stopPropagation()"
                (click)="onPinClick($event, module, pin)"
              >
                @if (pin.label) {
                  <span class="pin-label">{{ pin.label }}</span>
                }
              </span>
            }
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
  styles: [
    `
      .grid-canvas {
        position: relative;
        display: grid;
        gap: 0;
        overflow: auto;
        border-radius: 0.75rem;
        border: 1px solid rgba(148, 163, 184, 0.34);
        background-color: #f8fafc;
        box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08);
        cursor: default;
        background:
          linear-gradient(transparent 95%, rgba(148, 163, 184, 0.2) 95%),
          linear-gradient(90deg, transparent 95%, rgba(148, 163, 184, 0.2) 95%);
        background-size:
          100% 100%,
          100% 100%;
      }

      .grid-canvas.is-dragging {
        overflow: visible;
        z-index: 20;
      }

      .connection-layer {
        position: absolute;
        inset: 0;
        z-index: 1;
        overflow: visible;
        pointer-events: none;
      }

      .connection-hit {
        fill: none;
        stroke: transparent;
        stroke-width: 14;
        pointer-events: stroke;
        cursor: pointer;
      }

      .connection-path {
        fill: none;
        stroke: #0284c7;
        stroke-width: 2;
        stroke-linecap: round;
        stroke-linejoin: round;
      }

      .connection-path.connection-rigid {
        stroke: #a855f7;
        stroke-dasharray: 6 4;
      }

      .connection-path.connection-selected {
        stroke: #dc2626;
        stroke-width: 3;
        stroke-dasharray: none;
      }

      .connection-segment-hit {
        stroke: transparent;
        stroke-width: 14;
        pointer-events: stroke;
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
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        box-sizing: border-box;
        cursor: grab;
        touch-action: none;
        transition: box-shadow 0.2s;
        z-index: 10;
      }

      .module-card:hover {
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
      }

      .module-card.module-occupied {
        opacity: 0.6;
      }

      .module-card.module-selected {
        outline: 3px solid #dc2626;
        outline-offset: 2px;
      }

      .module-pin {
        position: absolute;
        z-index: 2;
        display: flex;
        align-items: center;
        justify-content: center;
        box-sizing: border-box;
        overflow: hidden;
        min-width: 6px;
        min-height: 6px;
        border: 1px solid rgba(15, 23, 42, 0.4);
        border-radius: 0.25rem;
        background: #38bdf8;
        color: #082f49;
        cursor: default;
      }

      .module-pin.pin-connectable {
        cursor: crosshair;
      }

      .module-pin.pin-selected {
        box-shadow:
          0 0 0 2px #fff,
          0 0 0 4px #0f172a;
      }

      .module-pin.pin-target-valid {
        box-shadow:
          0 0 0 2px #fff,
          0 0 0 4px #22c55e;
      }

      .module-pin.pin-output {
        background: #fbbf24;
        color: #451a03;
      }

      .module-pin.pin-bidirectional {
        background: #c084fc;
        color: #2e1065;
        border-radius: 50%;
      }

      .module-pin.pin-connected {
        border-color: #0f172a;
        box-shadow:
          0 0 0 1px #fff,
          0 0 0 2px #0f172a;
      }

      .module-pin.pin-up {
        border-top-left-radius: 0.45rem;
        border-top-right-radius: 0.45rem;
      }

      .module-pin.pin-left {
        border-top-left-radius: 0.45rem;
        border-bottom-left-radius: 0.45rem;
      }

      .module-pin.pin-down {
        border-bottom-left-radius: 0.45rem;
        border-bottom-right-radius: 0.45rem;
      }

      .module-pin.pin-right {
        border-top-right-radius: 0.45rem;
        border-bottom-right-radius: 0.45rem;
      }

      .pin-label {
        max-width: 100%;
        overflow: hidden;
        font-size: 0.45rem;
        font-weight: 700;
        line-height: 1;
        text-overflow: clip;
        white-space: nowrap;
      }

      .module-header {
        position: relative;
        z-index: 3;
        pointer-events: none;
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
        background: rgba(16, 185, 129, 0.15);
        border: 2px dashed currentColor;
        border-radius: 0.5rem;
        pointer-events: none;
        z-index: 5;
      }

      .module-preview.invalid {
        background: rgba(239, 68, 68, 0.15);
        border-color: rgba(239, 68, 68, 0.5);
      }

      .cell-highlight {
        position: absolute;
        background: rgba(56, 189, 248, 0.2);
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
        background: rgba(255, 255, 255, 0.8);
        border-radius: 0.5rem;
      }
    `,
  ],
})
export class FlowChartGridComponent {
  readonly cellSize = input(CELL_SIZE);
  readonly busy = input(false);

  readonly flowChart = input.required<FlowChart>();

  readonly moduleDropped = output<ModuleDropRequest>();
  readonly moduleMoved = output<ModuleMoveRequest>();
  readonly moduleDragStarted = output<number>();
  readonly moduleDragEnded = output<ModuleDragEndInfo>();
  readonly connectionRequested = output<PinConnectionRequest>();
  readonly connectionRejected = output<ConnectionRejection>();
  readonly moduleDeleteRequested = output<number>();
  readonly connectionDeleteRequested = output<number>();
  readonly connectionPathChangeRequested = output<ConnectionPathChangeRequest>();

  protected readonly isRigidConnection = isRigidConnection;

  private readonly _hoverCell = signal<GridPoint | null>(null);
  readonly hoverCell = this._hoverCell.asReadonly();

  private readonly _draggingModuleId = signal<number | null>(null);
  readonly draggingModuleId = this._draggingModuleId.asReadonly();

  private readonly _externalPreview = signal<ExternalPreview | null>(null);
  readonly externalPreview = this._externalPreview.asReadonly();

  private readonly _moveTarget = signal<MoveTarget | null>(null);
  readonly moveTarget = this._moveTarget.asReadonly();

  private readonly _selectedPin = signal<PinSelection | null>(null);
  readonly selectedPin = this._selectedPin.asReadonly();

  private readonly _selectedElement = signal<GridSelection | null>(null);
  readonly selectedElement = this._selectedElement.asReadonly();

  private readonly _segmentDrag = signal<SegmentDrag | null>(null);
  readonly segmentDrag = this._segmentDrag.asReadonly();

  readonly chartModules = computed(() => this.flowChart()?.modules ?? []);
  readonly gridMetrics = computed(() => gridMetrics(this.chartModules(), this.cellSize()));
  readonly pinsByModule = computed(() => {
    const cellSize = this.cellSize();
    const result = new Map<Module, DisplayPin[]>();
    for (const module of this.chartModules()) {
      result.set(module, modulePinViews(module, cellSize));
    }
    return result;
  });
  private readonly baseConnectionViews = computed<ConnectionPathView[]>(() =>
    connectionPathViews(
      this.flowChart()?.connections ?? [],
      this.chartModules(),
      this.cellSize(),
    ).filter((view) => view.paths.length > 0),
  );
  readonly connectionViews = computed<ConnectionPathView[]>(() => {
    const drag = this._segmentDrag();
    if (!drag) {
      return this.baseConnectionViews();
    }
    return this.baseConnectionViews().map((view) => {
      if (view.connection.id !== drag.connectionId) {
        return view;
      }
      return {
        ...view,
        paths: view.paths.map((path, index) =>
          index === drag.routeIndex ? pathFromPoints(drag.previewPoints, drag.endTrim) : path,
        ),
        routes: view.routes.map((route, index) =>
          index === drag.routeIndex ? { ...route, points: drag.previewPoints } : route,
        ),
      };
    });
  });
  readonly editableSegments = computed<EditableSegment[]>(() => {
    const selected = this._selectedElement();
    if (selected?.type !== 'connection') {
      return [];
    }
    const view = this.connectionViews().find(
      (candidate) => candidate.connection.id === selected.connectionId,
    );
    if (!view) {
      return [];
    }
    return view.routes.flatMap((route, routeIndex) =>
      route.points.slice(0, -1).map((start, segmentIndex) => ({
        key: `${routeIndex}:${segmentIndex}`,
        connection: view.connection,
        routeIndex,
        segmentIndex,
        start,
        end: route.points[segmentIndex + 1],
        horizontal: start.y === route.points[segmentIndex + 1].y,
      })),
    );
  });
  connectionMarkerEnd(connection: Connection): string | null {
    if (isRigidConnection(connection)) {
      return null;
    }
    return this.isConnectionSelected(connection)
      ? 'url(#connection-arrow-selected)'
      : 'url(#connection-arrow)';
  }

  isModuleSelected(module: Module): boolean {
    const selected = this._selectedElement();
    return selected?.type === 'module' && selected.moduleId === module.id;
  }

  isConnectionSelected(connection: { id?: number }): boolean {
    const selected = this._selectedElement();
    return selected?.type === 'connection' && selected.connectionId === connection.id;
  }

  onModuleClick(event: MouseEvent, module: Module): void {
    event.stopPropagation();
    if (this.busy() || module.id === undefined) {
      return;
    }
    this._selectedPin.set(null);
    this._selectedElement.set({ type: 'module', moduleId: module.id });
  }

  onConnectionClick(event: MouseEvent, connection: { id?: number }): void {
    event.stopPropagation();
    if (this.busy() || connection.id === undefined) {
      return;
    }
    this._selectedPin.set(null);
    this._selectedElement.set({ type: 'connection', connectionId: connection.id });
  }

  @HostListener('document:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent): void {
    if (event.key !== 'Delete' || this.busy()) {
      return;
    }
    const target = event.target as HTMLElement | null;
    if (
      target &&
      (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)
    ) {
      return;
    }
    const selected = this._selectedElement();
    if (!selected) {
      return;
    }
    event.preventDefault();
    this._selectedElement.set(null);
    this._selectedPin.set(null);
    if (selected.type === 'module') {
      this.moduleDeleteRequested.emit(selected.moduleId);
    } else {
      this.connectionDeleteRequested.emit(selected.connectionId);
    }
  }

  isPinSelected(module: Module, pin: DisplayPin): boolean {
    const selected = this._selectedPin();
    return selected !== null && selected.module.id === module.id && selected.pin.key === pin.key;
  }

  isPinTargetValid(module: Module, pin: DisplayPin): boolean {
    const selected = this._selectedPin();
    return selected !== null && canConnect(selected, { module, pin });
  }

  onPinPointerDown(event: PointerEvent): void {
    event.stopPropagation();
  }

  onPinClick(event: MouseEvent, module: Module, pin: DisplayPin): void {
    event.stopPropagation();
    if (this.busy()) {
      return;
    }

    this._selectedElement.set(null);
    const selection: PinSelection = { module, pin };
    const selected = this._selectedPin();
    if (!selected) {
      this._selectedPin.set(selection);
      return;
    }
    if (selected.module.id === module.id && selected.pin.key === pin.key) {
      this._selectedPin.set(null);
      return;
    }
    this.tryConnect(selected, selection);
    this._selectedPin.set(null);
  }

  onCanvasClick(event: MouseEvent): void {
    const target = event.target as Element | null;
    if (!target?.closest('.module-pin')) {
      this._selectedPin.set(null);
    }
    if (!target?.closest('.module-card, .connection-hit, .connection-segment-hit')) {
      this._selectedElement.set(null);
    }
    const canvas = this.canvas().nativeElement;
    this._hoverCell.set(cellFromClientPoint(event.clientX, event.clientY, canvas, this.cellSize()));
  }

  private tryConnect(first: PinSelection, second: PinSelection): void {
    if (!canConnect(first, second)) {
      this.connectionRejected.emit({ message: connectionRejectionReason(first, second) });
      return;
    }
    const unavailable = this.unavailableReason(first) ?? this.unavailableReason(second);
    if (unavailable) {
      this.connectionRejected.emit({ message: unavailable });
      return;
    }
    const request = normalizeConnection(first, second);
    if (!request) {
      this.connectionRejected.emit({ message: 'The selected pins cannot be connected.' });
      return;
    }
    this.connectionRequested.emit(request);
  }

  private unavailableReason(selection: PinSelection): string | null {
    const actsAsInput =
      selection.pin.direction === 'input' || selection.pin.direction === 'bidirectional';
    return actsAsInput && selection.pin.connected
      ? 'The selected input pin is already connected.'
      : null;
  }

  onSegmentPointerDown(event: PointerEvent, segment: EditableSegment): void {
    event.stopPropagation();
    event.preventDefault();
    if (this.busy() || segment.connection.id === undefined) {
      return;
    }
    const view = this.connectionViews().find(
      (candidate) => candidate.connection.id === segment.connection.id,
    );
    const route = view?.routes[segment.routeIndex];
    if (!route || route.points.length < 2) {
      return;
    }
    this._selectedPin.set(null);
    this._selectedElement.set({
      type: 'connection',
      connectionId: segment.connection.id,
    });
    this._segmentDrag.set({
      connectionId: segment.connection.id,
      to: route.to,
      routeIndex: segment.routeIndex,
      segmentIndex: segment.segmentIndex,
      start: { x: event.clientX, y: event.clientY },
      originalPoints: route.points.map((point) => ({ ...point })),
      previewPoints: route.points.map((point) => ({ ...point })),
      endTrim: route.endTrim,
      moved: false,
    });
  }

  @HostListener('document:pointermove', ['$event'])
  onDocumentPointerMove(event: PointerEvent): void {
    const drag = this._segmentDrag();
    if (!drag) {
      return;
    }
    const start = drag.originalPoints[drag.segmentIndex];
    const end = drag.originalPoints[drag.segmentIndex + 1];
    if (!start || !end) {
      return;
    }
    const horizontal = start.y === end.y;
    const delta = horizontal ? event.clientY - drag.start.y : event.clientX - drag.start.x;
    const previewPoints = moveConnectionSegment(
      drag.originalPoints,
      drag.segmentIndex,
      delta,
      this.chartModules(),
      this.cellSize(),
    );
    this._segmentDrag.set({
      ...drag,
      previewPoints,
      moved: drag.moved || !this.samePoints(drag.originalPoints, previewPoints),
    });
    event.preventDefault();
  }

  @HostListener('document:pointerup')
  onDocumentPointerUp(): void {
    const drag = this._segmentDrag();
    if (!drag) {
      return;
    }
    this._segmentDrag.set(null);
    if (!drag.moved || drag.to === undefined) {
      return;
    }
    const cellSize = this.cellSize();
    const diversionPoints = drag.previewPoints.slice(1, -1).map((point) => ({
      x: Math.round(point.x / cellSize),
      y: Math.round(point.y / cellSize),
    }));
    this.connectionPathChangeRequested.emit({
      connectionId: drag.connectionId,
      to: drag.to,
      diversionPoints,
    });
  }

  private samePoints(first: readonly DiagramPoint[], second: readonly DiagramPoint[]): boolean {
    return (
      first.length === second.length &&
      first.every((point, index) => point.x === second[index].x && point.y === second[index].y)
    );
  }

  onModuleDragStarted(event: CdkDragStart): void {
    const module = event.source.data as Module;
    this._draggingModuleId.set(module.id ?? null);
    this._hoverCell.set(null);
    this._selectedPin.set(null);
    this._selectedElement.set(null);
    if (module.id !== undefined) {
      this.moduleDragStarted.emit(module.id);
    }
  }

  onModuleDragMoved(event: CdkDragMove): void {
    const module = event.source.data as Module;
    this._moveTarget.set(this.computeMoveTarget(module, event.distance));
  }

  onModuleDragEnded(event: CdkDragEnd): void {
    const module = event.source.data as Module;
    const pointer = { x: event.dropPoint.x, y: event.dropPoint.y };
    const rect = this.canvas().nativeElement.getBoundingClientRect();
    const insideGrid =
      pointer.x >= rect.left &&
      pointer.x <= rect.right &&
      pointer.y >= rect.top &&
      pointer.y <= rect.bottom;

    if (insideGrid) {
      const target = this.computeMoveTarget(module, event.distance);
      if (module.id !== undefined && target.valid) {
        const from = moduleFootprint(module);
        if (target.cell.x !== from.x || target.cell.y !== from.y) {
          // Emitting first lets the parent update the module position synchronously,
          // so it renders at the new cell as soon as the CDK transform is cleared below.
          this.moduleMoved.emit({ moduleId: module.id, position: target.cell });
        }
      }
    }

    if (module.id !== undefined) {
      // Report the release point so the parent can detect drops outside the grid
      // (e.g. onto a trash can). Drops elsewhere outside the grid are ignored.
      this.moduleDragEnded.emit({ moduleId: module.id, pointer });
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
    const inBounds =
      cell.x >= 0 && cell.y >= 0 && cell.x + fp.width <= columns && cell.y + fp.height <= rows;
    const free = isFootprintFree(
      this.chartModules(),
      { x: cell.x, y: cell.y, width: fp.width, height: fp.height },
      module.id,
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
    if (
      isFootprintFree(chart.modules ?? [], target) &&
      isCellInBounds(cell, this.gridMetrics().columns, this.gridMetrics().rows)
    ) {
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
    if (
      isFootprintFree(chart.modules ?? [], target) &&
      isCellInBounds(cell, this.gridMetrics().columns, this.gridMetrics().rows)
    ) {
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
