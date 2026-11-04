import { Module } from '../api';
import { DisplayPin } from './pin-geometry';

export interface PinSelection {
  module: Module;
  pin: DisplayPin;
}

export interface PinConnectionRequest {
  fromModuleId: number;
  fromOutputIndex: number;
  toModuleId: number;
  toInputIndex: number;
}

function isRigid(selection: PinSelection): boolean {
  return selection.pin.direction === 'bidirectional';
}

function outputIndexOf(selection: PinSelection): number | undefined {
  if (selection.pin.outputIndex !== undefined) {
    return selection.pin.outputIndex;
  }
  return selection.pin.direction === 'output' ? selection.pin.pin.index : undefined;
}

function inputIndexOf(selection: PinSelection): number | undefined {
  if (selection.pin.inputIndex !== undefined) {
    return selection.pin.inputIndex;
  }
  return selection.pin.direction === 'input' ? selection.pin.pin.index : undefined;
}

function isSamePin(first: PinSelection, second: PinSelection): boolean {
  return first.module.id === second.module.id && first.pin.key === second.pin.key;
}

export function canConnect(first: PinSelection, second: PinSelection): boolean {
  if (isSamePin(first, second)) {
    return false;
  }

  const firstIsRigid = isRigid(first);
  const secondIsRigid = isRigid(second);

  if (firstIsRigid || secondIsRigid) {
    if (!firstIsRigid || !secondIsRigid) {
      return false;
    }
    return (
      (outputIndexOf(first) !== undefined && inputIndexOf(second) !== undefined) ||
      (outputIndexOf(second) !== undefined && inputIndexOf(first) !== undefined)
    );
  }

  return (
    (first.pin.direction === 'output' && second.pin.direction === 'input') ||
    (first.pin.direction === 'input' && second.pin.direction === 'output')
  );
}

export function normalizeConnection(
  first: PinSelection,
  second: PinSelection,
): PinConnectionRequest | null {
  if (!canConnect(first, second)) {
    return null;
  }

  const fromTo =
    outputIndexOf(first) !== undefined && inputIndexOf(second) !== undefined
      ? { from: first, to: second }
      : { from: second, to: first };
  const fromOutputIndex = outputIndexOf(fromTo.from);
  const toInputIndex = inputIndexOf(fromTo.to);
  const fromModuleId = fromTo.from.module.id;
  const toModuleId = fromTo.to.module.id;

  if (
    fromOutputIndex === undefined ||
    toInputIndex === undefined ||
    fromModuleId === undefined ||
    toModuleId === undefined
  ) {
    return null;
  }

  return { fromModuleId, fromOutputIndex, toModuleId, toInputIndex };
}

export function connectionRejectionReason(first: PinSelection, second: PinSelection): string {
  if (isSamePin(first, second)) {
    return 'A pin cannot be connected to itself.';
  }
  const firstIsRigid = isRigid(first);
  const secondIsRigid = isRigid(second);
  if (firstIsRigid !== secondIsRigid) {
    return 'Rigid pins can only be connected to other rigid pins.';
  }
  if (!firstIsRigid && first.pin.direction === second.pin.direction) {
    return 'Default connections require one input pin and one output pin.';
  }
  return 'The selected pins cannot be connected.';
}
