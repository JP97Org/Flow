import { TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { Connection, FlowChart } from '../api';
import { FlowChartGridComponent } from './flow-chart-grid.component';

function chartWithPins(): FlowChart {
  return {
    id: 1,
    modules: [
      {
        id: 2,
        className: 'org.jojo.flow.model.util.DynamicObjectLoader$MockModule',
        name: 'Mock Module 2',
        position: { x: 2, y: 3 },
        width: 4,
        height: 4,
        inputs: [
          {
            index: 0,
            direction: 'input',
            implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
            position: { x: 0, y: 1 },
            linePoint: { x: 0, y: 1 },
            width: 1,
            height: 1,
            iconText: 'in',
            orientation: 'LEFT',
          },
          {
            index: 1,
            direction: 'input',
            implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
            position: { x: 2, y: 2 },
            linePoint: { x: 2, y: 2 },
            width: 1,
            height: 1,
            orientation: 'DOWN',
          },
        ],
        outputs: [
          {
            index: 0,
            direction: 'output',
            implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
            position: { x: 4, y: 1 },
            linePoint: { x: 4, y: 1 },
            width: 1,
            height: 1,
            iconText: 'out',
            orientation: 'RIGHT',
          },
          {
            index: 1,
            direction: 'output',
            implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
            position: { x: 2, y: 2 },
            linePoint: { x: 2, y: 2 },
            width: 1,
            height: 1,
            orientation: 'DOWN',
            connectionIds: [9],
          },
        ],
      },
    ],
  };
}

describe('FlowChartGridComponent pins', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlowChartGridComponent],
    }).compileComponents();
  });

  it('renders each pin position once and marks the merged rigid pin', () => {
    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chartWithPins());
    fixture.detectChanges();

    const pins = Array.from(fixture.nativeElement.querySelectorAll('.module-pin')) as HTMLElement[];

    expect(pins).toHaveLength(3);
    expect(pins.filter((pin) => pin.classList.contains('pin-input'))).toHaveLength(1);
    expect(pins.filter((pin) => pin.classList.contains('pin-output'))).toHaveLength(1);
    expect(pins.filter((pin) => pin.classList.contains('pin-bidirectional'))).toHaveLength(1);
    expect(pins.filter((pin) => pin.classList.contains('pin-connected'))).toHaveLength(1);
    expect(pins.some((pin) => pin.textContent?.trim() === 'in')).toBe(true);
    expect(pins.some((pin) => pin.textContent?.trim() === 'out')).toBe(true);

    const rigid = pins.find((pin) => pin.classList.contains('pin-bidirectional'));
    expect(rigid?.style.left).toBe('40px');
    expect(rigid?.style.top).toBe('80px');
  });

  it('renders existing default and rigid connections', () => {
    const chart = chartWithPins();
    const connections: Connection[] = [
      {
        id: 8,
        className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
        from: { moduleId: 2, index: 0, direction: 'output' },
        to: [{ moduleId: 2, index: 0, direction: 'input' }],
      },
      {
        id: 9,
        className: 'org.jojo.flow.model.flowChart.connections.RigidConnection',
        from: { moduleId: 2, index: 1, direction: 'output' },
        to: [{ moduleId: 2, index: 1, direction: 'input' }],
      },
    ];
    chart.connections = connections;

    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chart);
    fixture.detectChanges();

    const paths = Array.from(
      fixture.nativeElement.querySelectorAll('.connection-layer path.connection-path'),
    ) as SVGPathElement[];

    expect(paths).toHaveLength(2);
    expect(paths.filter((path) => path.classList.contains('connection-rigid'))).toHaveLength(1);
    expect(paths.every((path) => (path.getAttribute('d') ?? '').startsWith('M '))).toBe(true);

    const defaultPath = paths.find((path) => !path.classList.contains('connection-rigid'));
    const rigidPath = paths.find((path) => path.classList.contains('connection-rigid'));
    expect(defaultPath?.getAttribute('marker-end')).toBe('url(#connection-arrow)');
    expect(rigidPath?.getAttribute('marker-end')).toBeNull();
  });

  it('connects compatible pins through click selection', () => {
    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chartWithPins());
    fixture.detectChanges();

    const requests: unknown[] = [];
    fixture.componentInstance.connectionRequested.subscribe((request) => requests.push(request));

    const output = fixture.nativeElement.querySelector('.pin-output') as HTMLElement;
    const input = fixture.nativeElement.querySelector('.pin-input') as HTMLElement;

    output.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();
    expect(output.classList.contains('pin-selected')).toBe(true);
    expect(requests).toHaveLength(0);

    input.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(requests).toEqual([
      { fromModuleId: 2, fromOutputIndex: 0, toModuleId: 2, toInputIndex: 0 },
    ]);
    expect(output.classList.contains('pin-selected')).toBe(false);
  });

  it('connects pins across two instances of the same module type', () => {
    const chart = chartWithPins();
    const second = {
      ...chart.modules![0],
      id: 4,
      position: { x: 20, y: 3 },
    };
    chart.modules!.push(second);

    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chart);
    fixture.detectChanges();

    const requests: unknown[] = [];
    fixture.componentInstance.connectionRequested.subscribe((request) => requests.push(request));

    const outputs = Array.from(
      fixture.nativeElement.querySelectorAll('.pin-output'),
    ) as HTMLElement[];
    const inputs = Array.from(
      fixture.nativeElement.querySelectorAll('.pin-input'),
    ) as HTMLElement[];

    outputs[0].dispatchEvent(new MouseEvent('click', { bubbles: true }));
    inputs[1].dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(requests).toEqual([
      { fromModuleId: 2, fromOutputIndex: 0, toModuleId: 4, toInputIndex: 0 },
    ]);
  });

  it('deletes a selected module when Delete is pressed', () => {
    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chartWithPins());
    fixture.detectChanges();

    const deleted: number[] = [];
    fixture.componentInstance.moduleDeleteRequested.subscribe((id) => deleted.push(id));

    const moduleCard = fixture.nativeElement.querySelector('.module-card') as HTMLElement;
    moduleCard.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(moduleCard.classList.contains('module-selected')).toBe(true);

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Delete', bubbles: true }));
    fixture.detectChanges();

    expect(deleted).toEqual([2]);
    expect(moduleCard.classList.contains('module-selected')).toBe(false);
  });

  it('emits a path change after dragging a selected connection segment', () => {
    const chart = chartWithPins();
    chart.connections = [
      {
        id: 8,
        className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
        from: { moduleId: 2, index: 0, direction: 'output' },
        to: [{ moduleId: 2, index: 0, direction: 'input' }],
      },
    ];

    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chart);
    fixture.detectChanges();

    const changes: unknown[] = [];
    fixture.componentInstance.connectionPathChangeRequested.subscribe((change) =>
      changes.push(change),
    );

    const hit = fixture.nativeElement.querySelector('.connection-hit') as SVGPathElement;
    hit.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    const segment = fixture.nativeElement.querySelector(
      '.connection-segment-hit',
    ) as SVGLineElement;
    expect(segment).not.toBeNull();

    const horizontal = segment.getAttribute('y1') === segment.getAttribute('y2');
    const deltaX = horizontal ? 0 : 10;
    const deltaY = horizontal ? 10 : 0;

    segment.dispatchEvent(new MouseEvent('pointerdown', { bubbles: true, clientX: 0, clientY: 0 }));
    expect(fixture.componentInstance.segmentDrag()?.to).toBeDefined();
    document.dispatchEvent(
      new MouseEvent('pointermove', { bubbles: true, clientX: deltaX, clientY: deltaY }),
    );
    expect(fixture.componentInstance.segmentDrag()?.moved).toBe(true);
    document.dispatchEvent(
      new MouseEvent('pointerup', { bubbles: true, clientX: deltaX, clientY: deltaY }),
    );
    fixture.detectChanges();

    expect(changes).toHaveLength(1);
    const change = changes[0] as {
      connectionId: number;
      to: unknown;
      diversionPoints: unknown[];
    };
    expect(change.connectionId).toBe(8);
    expect(change.to).toEqual({ moduleId: 2, index: 0, direction: 'input' });
    expect(change.diversionPoints.length).toBeGreaterThanOrEqual(2);
  });

  it('deletes a selected connection when Delete is pressed', () => {
    const chart = chartWithPins();
    chart.connections = [
      {
        id: 8,
        className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
        from: { moduleId: 2, index: 0, direction: 'output' },
        to: [{ moduleId: 2, index: 0, direction: 'input' }],
      },
    ];

    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chart);
    fixture.detectChanges();

    const deleted: number[] = [];
    fixture.componentInstance.connectionDeleteRequested.subscribe((id) => deleted.push(id));

    const hit = fixture.nativeElement.querySelector('.connection-hit') as SVGPathElement;
    hit.dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelectorAll('.connection-path.connection-selected').length,
    ).toBe(1);

    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Delete', bubbles: true }));
    fixture.detectChanges();

    expect(deleted).toEqual([8]);
    expect(
      fixture.nativeElement.querySelectorAll('.connection-path.connection-selected').length,
    ).toBe(0);
  });

  it('rejects click selection when both pins are inputs', () => {
    const chart = chartWithPins();
    const source = chart.modules![0];
    chart.modules!.push({
      ...source,
      id: 3,
      outputs: [],
      inputs: [{ ...(source.inputs?.[0] ?? {}) }],
    });

    const fixture = TestBed.createComponent(FlowChartGridComponent);
    fixture.componentRef.setInput('flowChart', chart);
    fixture.detectChanges();

    const requests: unknown[] = [];
    const rejections: string[] = [];
    fixture.componentInstance.connectionRequested.subscribe((request) => requests.push(request));
    fixture.componentInstance.connectionRejected.subscribe((rejection) =>
      rejections.push(rejection.message),
    );

    const inputs = Array.from(
      fixture.nativeElement.querySelectorAll('.pin-input'),
    ) as HTMLElement[];

    inputs[0].dispatchEvent(new MouseEvent('click', { bubbles: true }));
    inputs[1].dispatchEvent(new MouseEvent('click', { bubbles: true }));
    fixture.detectChanges();

    expect(requests).toHaveLength(0);
    expect(rejections).toHaveLength(1);
    expect(rejections[0]).toContain('input pin and one output pin');
  });
});
