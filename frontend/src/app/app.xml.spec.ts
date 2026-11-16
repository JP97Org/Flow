import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { FlowChart, FlowChartService, Module } from './api';
import { App } from './app';

interface TestableApp {
  saveXml(): void;
  onXmlSelected(event: Event): void;
  addModule(className: string, position: { x: number; y: number }): void;
  flowChart(): FlowChart | null;
}

function selectFile(contents: string): { event: Event; input: HTMLInputElement } {
  const file = { text: () => Promise.resolve(contents) } as File;
  const input = { files: [file], value: 'C:\\fakepath\\chart.xml' } as unknown as HTMLInputElement;
  return { event: { target: input } as unknown as Event, input };
}

describe('App XML persistence', () => {
  const api = {
    getHealth: () => of({ status: 'UP', application: 'flow', version: '1.0.0' }),
    getModuleClasses: () => of([]),
    getMainFlowChart: () => of({ id: 1, modules: [], connections: [] } as FlowChart),
    exportMainFlowChartXml: vi.fn(() => of(new Blob(['<flow/>'], { type: 'application/xml' }))),
    importMainFlowChartXml: vi.fn(() => of({ id: 2, modules: [], connections: [] } as FlowChart)),
    addModuleToFlowChart: vi.fn(() => of({ id: 10, className: 'Mock' } as Module)),
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), { provide: FlowChartService, useValue: api }],
    }).compileComponents();
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.unstubAllGlobals();
  });

  it('exports the main flow chart when saving', async () => {
    const createObjectURL = vi.fn((_blob: Blob): string => 'blob:test');
    const revokeObjectURL = vi.fn((_url: string): void => {});
    vi.stubGlobal('URL', { createObjectURL, revokeObjectURL } as unknown as typeof URL);
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();

    (fixture.componentInstance as unknown as TestableApp).saveXml();

    expect(api.exportMainFlowChartXml).toHaveBeenCalledOnce();
    expect(createObjectURL).toHaveBeenCalledOnce();
    expect(createObjectURL.mock.calls[0][0]).toBeInstanceOf(Blob);
  });

  it('imports the selected XML file and replaces the chart', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const { event, input } = selectFile('<flow/>');

    (fixture.componentInstance as unknown as TestableApp).onXmlSelected(event);
    await fixture.whenStable();

    expect(api.importMainFlowChartXml).toHaveBeenCalledWith({ body: '<flow/>' });
    expect(input.value).toBe('');
    expect((fixture.componentInstance as unknown as TestableApp).flowChart()?.id).toBe(2);
  });

  it('allows editing a loaded chart whose id is zero', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    api.importMainFlowChartXml.mockReturnValueOnce(
      of({ id: 0, modules: [], connections: [] } as FlowChart),
    );
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const { event } = selectFile('<flow/>');
    const app = fixture.componentInstance as unknown as TestableApp;

    app.onXmlSelected(event);
    await fixture.whenStable();
    app.addModule('Mock', { x: 0, y: 0 });

    expect(app.flowChart()?.id).toBe(0);
    expect(api.addModuleToFlowChart).toHaveBeenCalledWith({
      id: 0,
      addModuleRequest: { className: 'Mock', position: { x: 0, y: 0 } },
    });
  });

  it('does not import when the load is cancelled', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const { event } = selectFile('<flow/>');

    (fixture.componentInstance as unknown as TestableApp).onXmlSelected(event);
    await fixture.whenStable();

    expect(api.importMainFlowChartXml).not.toHaveBeenCalled();
  });

  it('rejects an empty XML file', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await fixture.whenStable();
    const { event } = selectFile('   ');

    (fixture.componentInstance as unknown as TestableApp).onXmlSelected(event);
    await fixture.whenStable();

    expect(api.importMainFlowChartXml).not.toHaveBeenCalled();
  });
});
