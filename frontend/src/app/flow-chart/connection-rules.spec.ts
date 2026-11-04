import { describe, expect, it } from 'vitest';
import { Module, Pin } from '../api';
import { DisplayPin } from './pin-geometry';
import {
  PinSelection,
  canConnect,
  connectionRejectionReason,
  normalizeConnection,
} from './connection-rules';

const RIGID = 'org.jojo.flow.model.flowChart.modules.RigidPin';
const DEFAULT = 'org.jojo.flow.model.flowChart.modules.DefaultPin';

function pin(
  direction: 'input' | 'output' | 'bidirectional',
  index: number,
  implementationClass: string,
  connected = false,
): DisplayPin {
  const rigid = direction === 'bidirectional';
  return {
    key: `${direction}:${index}`,
    pin: { index, direction, implementationClass } as Pin,
    inputPin: direction !== 'output' ? ({ index } as Pin) : undefined,
    outputPin: direction !== 'input' ? ({ index: rigid ? index + 10 : index } as Pin) : undefined,
    inputIndex: direction !== 'output' ? index : undefined,
    outputIndex: direction !== 'input' ? (rigid ? index + 10 : index) : undefined,
    direction,
    orientation: null,
    left: 0,
    top: 0,
    size: 8,
    label: '',
    tooltip: '',
    connected,
  };
}

function selection(moduleId: number, displayPin: DisplayPin): PinSelection {
  return { module: { id: moduleId } as Module, pin: displayPin };
}

describe('connection-rules', () => {
  it('always connects two rigid pins', () => {
    const first = selection(1, pin('bidirectional', 0, RIGID));
    const second = selection(2, pin('bidirectional', 1, RIGID));

    expect(canConnect(first, second)).toBe(true);
    expect(canConnect(second, first)).toBe(true);
  });

  it('connects default pins only input-to-output or output-to-input', () => {
    const input = selection(1, pin('input', 0, DEFAULT));
    const output = selection(2, pin('output', 0, DEFAULT));
    const otherInput = selection(2, pin('input', 1, DEFAULT));
    const otherOutput = selection(2, pin('output', 1, DEFAULT));

    expect(canConnect(output, input)).toBe(true);
    expect(canConnect(input, output)).toBe(true);
    expect(canConnect(input, otherInput)).toBe(false);
    expect(canConnect(output, otherOutput)).toBe(false);
  });

  it('does not mix rigid and default pins', () => {
    const rigid = selection(1, pin('bidirectional', 0, RIGID));
    const input = selection(2, pin('input', 0, DEFAULT));
    const output = selection(2, pin('output', 0, DEFAULT));

    expect(canConnect(rigid, input)).toBe(false);
    expect(canConnect(rigid, output)).toBe(false);
    expect(canConnect(input, rigid)).toBe(false);
    expect(canConnect(output, rigid)).toBe(false);
  });

  it('normalizes reversed default selections to output-to-input', () => {
    const input = selection(1, pin('input', 4, DEFAULT));
    const output = selection(2, pin('output', 7, DEFAULT));

    expect(normalizeConnection(input, output)).toEqual({
      fromModuleId: 2,
      fromOutputIndex: 7,
      toModuleId: 1,
      toInputIndex: 4,
    });
  });

  it('normalizes rigid selections to output-to-input', () => {
    const first = selection(1, pin('bidirectional', 2, RIGID));
    const second = selection(2, pin('bidirectional', 3, RIGID));

    expect(normalizeConnection(first, second)).toEqual({
      fromModuleId: 1,
      fromOutputIndex: 12,
      toModuleId: 2,
      toInputIndex: 3,
    });
    expect(normalizeConnection(second, first)).toEqual({
      fromModuleId: 2,
      fromOutputIndex: 13,
      toModuleId: 1,
      toInputIndex: 2,
    });
  });

  it('returns no request and a reason for invalid selections', () => {
    const firstInput = selection(1, pin('input', 0, DEFAULT));
    const secondInput = selection(2, pin('input', 1, DEFAULT));

    expect(normalizeConnection(firstInput, secondInput)).toBeNull();
    expect(connectionRejectionReason(firstInput, secondInput)).toContain(
      'input pin and one output',
    );
  });
});
