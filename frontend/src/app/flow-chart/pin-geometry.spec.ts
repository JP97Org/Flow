import { describe, expect, it } from 'vitest';
import { Module, Pin } from '../api';
import { modulePinViews, pinDisplaySize, pinPixelOffset } from './pin-geometry';

describe('pin-geometry', () => {
  it('converts module-relative pin positions to pixels', () => {
    const pin = { position: { x: 2, y: 3 } } as Pin;

    expect(pinPixelOffset(pin, 20)).toEqual({ left: 40, top: 60 });
  });

  it('uses the line point for rigid pins', () => {
    const pin = {
      position: { x: 20, y: 5 },
      linePoint: { x: 5, y: 10 },
      implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
    } as Pin;

    expect(pinPixelOffset(pin, 20)).toEqual({ left: 100, top: 200 });
  });

  it('wraps vertical rigid pins that start at or beyond the module width', () => {
    const first = {
      linePoint: { x: 10, y: 10 },
      implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
      orientation: 'DOWN',
    } as Pin;
    const second = {
      linePoint: { x: 20, y: 10 },
      implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
      orientation: 'DOWN',
    } as Pin;
    const module = { width: 10, height: 10 } as Module;

    expect(pinPixelOffset(first, 20, module)).toEqual({ left: 0, top: 200 });
    expect(pinPixelOffset(second, 20, module)).toEqual({ left: 200, top: 200 });
  });

  it('snaps default pins to the module edge indicated by their orientation', () => {
    const input = {
      position: { x: 50, y: 50 },
      linePoint: { x: 0, y: 0 },
      implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
      orientation: 'LEFT',
    } as Pin;
    const output = {
      linePoint: { x: 0, y: 0 },
      implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
      orientation: 'RIGHT',
    } as Pin;
    const module = { width: 10, height: 10 } as Module;

    expect(pinPixelOffset(input, 20, module)).toEqual({ left: 0, top: 0 });
    expect(pinPixelOffset(output, 20, module)).toEqual({ left: 200, top: 0 });
  });

  it('falls back to the line point when no position is available', () => {
    const pin = { linePoint: { x: -1, y: 2 } } as Pin;

    expect(pinPixelOffset(pin, 20)).toEqual({ left: -20, top: 40 });
  });

  it('clamps pin marker sizes to a readable range', () => {
    expect(pinDisplaySize({ width: 10, height: 10 } as Pin, 20)).toBe(15);
    expect(pinDisplaySize({ width: 0.1, height: 0.1 } as Pin, 20)).toBe(6);
    expect(pinDisplaySize({} as Pin, 20)).toBe(8);
  });

  it('merges rigid input and output pins at the same anchor', () => {
    const module = {
      inputs: [
        {
          index: 0,
          direction: 'input',
          pinClass: 'org.jojo.flow.model.flowChart.modules.InputPin',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 0, y: 0 },
          linePoint: { x: 0, y: 0 },
          width: 1,
          height: 1,
          iconText: 'A',
          orientation: 'LEFT',
        },
        {
          index: 1,
          direction: 'input',
          pinClass: 'org.jojo.flow.model.flowChart.modules.InputPin',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
          position: { x: 20, y: 5 },
          linePoint: { x: 10, y: 10 },
          width: 1,
          height: 1,
          orientation: 'DOWN',
          connectionIds: [7],
        },
      ],
      outputs: [
        {
          index: 0,
          direction: 'output',
          pinClass: 'org.jojo.flow.model.flowChart.modules.OutputPin',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 20, y: 0 },
          linePoint: { x: 20, y: 0 },
          width: 1,
          height: 1,
          orientation: 'RIGHT',
        },
        {
          index: 1,
          direction: 'output',
          pinClass: 'org.jojo.flow.model.flowChart.modules.OutputPin',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
          position: { x: 20, y: 5 },
          linePoint: { x: 10, y: 10 },
          width: 1,
          height: 1,
          orientation: 'DOWN',
          connectionIds: [7],
        },
      ],
    } as Module;

    const views = modulePinViews(module, 20);

    expect(views).toHaveLength(3);

    const input = views.find((view) => view.direction === 'input');
    expect(input?.direction).toBe('input');
    expect(input?.label).toBe('A');

    const output = views.find((view) => view.direction === 'output');
    expect(output?.direction).toBe('output');

    const rigid = views.find((view) => view.direction === 'bidirectional');
    expect(rigid?.direction).toBe('bidirectional');
    expect(rigid?.connected).toBe(true);
    expect(rigid?.label).toBe('1');
    expect(rigid?.tooltip).toContain('Bidirectional pin');
    expect(rigid?.left).toBe(200);
    expect(rigid?.top).toBe(200);
  });

  it('places pins outside the module and keeps them within the edge span', () => {
    const rigidInput = (x: number) =>
      ({
        direction: 'input',
        implementationClass: 'org.jojo.flow.model.flowChart.modules.RigidPin',
        position: { x, y: 10 },
        linePoint: { x, y: 10 },
        width: 1,
        height: 1,
        orientation: 'DOWN',
      }) as Pin;
    const rigidOutput = (x: number) =>
      ({
        ...rigidInput(x),
        direction: 'output',
      }) as Pin;
    const module = {
      width: 10,
      height: 10,
      inputs: [
        {
          index: 0,
          direction: 'input',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 50, y: 50 },
          linePoint: { x: 0, y: 0 },
          width: 1,
          height: 1,
          orientation: 'LEFT',
        },
        { ...rigidInput(10), index: 1 },
        { ...rigidInput(20), index: 2 },
      ],
      outputs: [
        {
          index: 0,
          direction: 'output',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 0, y: 0 },
          linePoint: { x: 0, y: 0 },
          width: 1,
          height: 1,
          orientation: 'RIGHT',
        },
        { ...rigidOutput(10), index: 1 },
        { ...rigidOutput(20), index: 2 },
      ],
    } as Module;

    const views = modulePinViews(module, 20);
    const input = views.find((view) => view.direction === 'input');
    const output = views.find((view) => view.direction === 'output');
    const rigid = views.filter((view) => view.direction === 'bidirectional');

    expect(input?.left).toBe(-15);
    expect(input?.top).toBe(0);
    expect(output?.left).toBe(200);
    expect(output?.top).toBe(0);
    expect(rigid.map((view) => [view.left, view.top])).toEqual([
      [0, 200],
      [185, 200],
    ]);
  });

  it('does not merge distinct default pins that share a line point', () => {
    const module = {
      inputs: [
        {
          index: 0,
          direction: 'input',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 5, y: 5 },
          linePoint: { x: 0, y: 0 },
        },
      ],
      outputs: [
        {
          index: 0,
          direction: 'output',
          implementationClass: 'org.jojo.flow.model.flowChart.modules.DefaultPin',
          position: { x: 0, y: 0 },
          linePoint: { x: 0, y: 0 },
        },
      ],
    } as Module;

    expect(modulePinViews(module, 20)).toHaveLength(2);
  });
});
