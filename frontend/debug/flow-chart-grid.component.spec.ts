import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { FlowChartGridComponent } from './flow-chart-grid.component';
import { FlowChart } from '../api';

function chartWith(modules: any[]): FlowChart {
  return { id: 1, modules } as FlowChart;
}

describe('FlowChartGridComponent dragging', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [FlowChartGridComponent] });
  });

  it('traces drag lifecycle', () => {
    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput(
      'flowChart',
      chartWith([{ id: 2, position: { x: 5, y: 5 }, width: 2, height: 2 }])
    );
    fixture.detectChanges();

    const component = fixture.componentInstance;
    const started = vi.spyOn(component, 'onModuleDragStarted');
    const moved = vi.spyOn(component, 'onModuleDragMoved');
    const ended = vi.spyOn(component, 'onModuleDragEnded');
    const emitted = vi.spyOn(component.moduleMoved, 'emit');

    const card = fixture.nativeElement.querySelector('.module-card') as HTMLElement;
    expect(card).toBeTruthy();

    const down = new MouseEvent('mousedown', { bubbles: true, cancelable: true, clientX: 100, clientY: 100, button: 0, buttons: 1 });
    card.dispatchEvent(down);
    const move1 = new MouseEvent('mousemove', { bubbles: true, cancelable: true, clientX: 130, clientY: 120, button: 0, buttons: 1 });
    document.dispatchEvent(move1);
    const move2 = new MouseEvent('mousemove', { bubbles: true, cancelable: true, clientX: 160, clientY: 140, button: 0, buttons: 1 });
    document.dispatchEvent(move2);
    const up = new MouseEvent('mouseup', { bubbles: true, cancelable: true, clientX: 160, clientY: 140, button: 0, buttons: 0 });
    document.dispatchEvent(up);

    console.log('started calls', started.mock.calls.length);
    console.log('moved calls', moved.mock.calls.length);
    console.log('ended calls', ended.mock.calls.length);
    console.log('emitted calls', emitted.mock.calls.length, emitted.mock.calls);
    expect(started.mock.calls.length).toBeGreaterThan(0);
  });
});
