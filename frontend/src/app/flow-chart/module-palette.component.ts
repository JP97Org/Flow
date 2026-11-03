import { Component, input, output, ViewChild, ElementRef, viewChild } from '@angular/core';
import { CdkDrag, CdkDragEnd, CdkDragMove, CdkDragStart } from '@angular/cdk/drag-drop';
import { ModuleClass } from '../api';

export interface PaletteDragRequest {
  className: string;
  pointer: { x: number; y: number };
}

@Component({
  selector: 'app-module-palette',
  imports: [CdkDrag],
  template: `
    <h3>Module types</h3>
    <ul>
      @for (moduleClass of moduleClasses(); track moduleClass.className) {
        <li>
          <div
            cdkDrag
            [cdkDragData]="moduleClass"
            (cdkDragStarted)="onDragStarted($event)"
            (cdkDragMoved)="onDragMoved($event)"
            (cdkDragEnded)="onDragEnded($event)"
            (cdkDragReleased)="onDragReleased()"
          >
            <strong>{{ moduleClass.displayName }}</strong>
            <span>{{ moduleClass.className }}</span>
          </div>
        </li>
      } @empty {
        <li>No module types loaded.</li>
      }
    </ul>
  `,
  styles: [`
    :host {
      display: block;
      padding: 1rem;
    }
    h3 {
      margin: 0 0 0.75rem;
      font-size: 0.95rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
      color: #475569;
    }
    ul {
      list-style: none;
      margin: 0;
      padding: 0;
    }
    li {
      margin-bottom: 0.5rem;
    }
    [cdkDrag] {
      cursor: grab;
      padding: 0.6rem;
      border-radius: 0.6rem;
      background: #e2e8f0;
      transition: background 0.2s, box-shadow 0.2s;
    }
    [cdkDrag]:hover {
      background: #cbd5e1;
    }
    [cdkDrag]:active {
      cursor: grabbing;
    }
    [cdkDrag].cdk-drag-animating {
      opacity: 0.8;
      transition: opacity 0.2s;
    }
    strong {
      display: block;
      font-size: 0.85rem;
      color: #1e293b;
    }
    span {
      font-size: 0.7rem;
      color: #64748b;
      font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
    }
  `],
})
export class ModulePaletteComponent {
  readonly moduleClasses = input.required<ModuleClass[]>();

  readonly dragStarted = output<CdkDragStart>();
  readonly dragMoved = output<PaletteDragRequest>();
  readonly dragEnded = output<PaletteDragRequest | null>();
  readonly dragReleased = output<void>();

  private draggingClassName: string | null = null;

  onDragStarted(event: CdkDragStart): void {
    this.draggingClassName = event.source.data?.className ?? null;
    this.dragStarted.emit(event);
  }

  onDragMoved(event: CdkDragMove): void {
    if (this.draggingClassName) {
      this.dragMoved.emit({
        className: this.draggingClassName,
        pointer: event.pointerPosition,
      });
    }
  }

  onDragEnded(event: CdkDragEnd): void {
    if (this.draggingClassName) {
      event.source.reset();
      this.dragEnded.emit({
        className: this.draggingClassName,
        pointer: event.dropPoint,
      });
      this.draggingClassName = null;
    } else {
      this.dragEnded.emit(null);
    }
  }

  onDragReleased(): void {
    if (this.draggingClassName) {
      // Do not clear draggingClassName here: CDK emits `released` before `ended`,
      // and onDragEnded still needs the class name and drop point to report the drop.
      this.dragReleased.emit();
    }
  }
}
