import { Module, Pin } from '../api';
import { CELL_SIZE } from './grid-geometry';

export type PinDirection = 'input' | 'output' | 'bidirectional';
export type PinOrientation = 'up' | 'left' | 'down' | 'right' | null;

export interface PinOffset {
  left: number;
  top: number;
}

export interface DisplayPin {
  key: string;
  pin: Pin;
  inputPin?: Pin;
  outputPin?: Pin;
  inputIndex?: number;
  outputIndex?: number;
  direction: PinDirection;
  orientation: PinOrientation;
  left: number;
  top: number;
  size: number;
  label: string;
  tooltip: string;
  connected: boolean;
}

const FALLBACK_SIZE_FACTOR = 0.4;
const MIN_SIZE_PX = 6;
const MAX_SIZE_FACTOR = 0.75;

function pointOf(pin: Pin, module?: Module): { x: number; y: number } {
  const point = pin.linePoint ?? pin.position;
  let x = point?.x ?? 0;
  let y = point?.y ?? 0;
  if (!module) {
    return { x, y };
  }

  const orientation = orientationOf(pin);
  const width = module.width ?? 0;
  const height = module.height ?? 0;
  if (orientation === 'down' && height > 0 && y >= height) {
    x = normalizeEdgeOffset(x, width);
    y = height;
  } else if (orientation === 'up' && y <= 0) {
    x = normalizeEdgeOffset(x, width);
    y = 0;
  } else if (orientation === 'left' && x <= 0) {
    y = normalizeEdgeOffset(y, height);
    x = 0;
  } else if (orientation === 'right' && (x <= 0 || (width > 0 && x >= width))) {
    y = normalizeEdgeOffset(y, height);
    x = width;
  }
  return { x, y };
}

function normalizeEdgeOffset(value: number, size: number): number {
  if (size <= 0) {
    return value;
  }
  return value >= size ? value - size : value;
}

function anchorKey(pin: Pin): string {
  const position = pin.position ?? pin.linePoint;
  const linePoint = pin.linePoint ?? pin.position;
  return `${position?.x ?? 0},${position?.y ?? 0}:${linePoint?.x ?? 0},${linePoint?.y ?? 0}`;
}

export function isRigidPin(pin: Pin): boolean {
  return (pin.implementationClass ?? '').split('.').pop() === 'RigidPin';
}

function orientationOf(pin: Pin): PinOrientation {
  switch ((pin.orientation ?? '').toUpperCase()) {
    case 'UP':
      return 'up';
    case 'LEFT':
      return 'left';
    case 'DOWN':
      return 'down';
    case 'RIGHT':
      return 'right';
    default:
      return null;
  }
}

function shortClassName(pin: Pin): string {
  const className = pin.implementationClass ?? pin.pinClass ?? '';
  return className.split('.').pop() || 'pin';
}

function describePin(pin: Pin): string {
  const direction =
    pin.direction === 'output' ? 'Output' : pin.direction === 'input' ? 'Input' : 'Pin';
  const index = pin.index === undefined ? '' : ` #${pin.index}`;
  const connectionCount = pin.connectionIds?.length ?? 0;
  const parts = [`${direction}${index}`, shortClassName(pin)];
  if (pin.dataSignature) {
    parts.push(`data ${pin.dataSignature}`);
  }
  parts.push(`${connectionCount} connection${connectionCount === 1 ? '' : 's'}`);
  return parts.join(' · ');
}

function labelOf(pins: Pin[]): string {
  const iconText = pins.map((pin) => pin.iconText?.trim()).find((text) => !!text);
  if (iconText) {
    return iconText;
  }
  const firstIndex = pins[0]?.index;
  return firstIndex !== undefined && pins.every((pin) => pin.index === firstIndex)
    ? String(firstIndex)
    : '';
}

export function pinPixelOffset(pin: Pin, cellSize = CELL_SIZE, module?: Module): PinOffset {
  const point = pointOf(pin, module);
  return {
    left: point.x * cellSize,
    top: point.y * cellSize,
  };
}

export function pinDisplayOffset(pin: Pin, module: Module, cellSize = CELL_SIZE): PinOffset {
  const offset = pinPixelOffset(pin, cellSize, module);
  return pinEdgeOffset(offset, pinDisplaySize(pin, cellSize), orientationOf(pin), module, cellSize);
}

function pinEdgeOffset(
  offset: PinOffset,
  size: number,
  orientation: PinOrientation,
  module: Module,
  cellSize: number,
): PinOffset {
  if (!orientation || module.width === undefined || module.height === undefined) {
    return offset;
  }
  const widthPx = module.width * cellSize;
  const heightPx = module.height * cellSize;
  const clamp = (value: number, maximum: number) => Math.min(Math.max(value, 0), maximum);
  const maxLeft = Math.max(0, widthPx - size);
  const maxTop = Math.max(0, heightPx - size);

  switch (orientation) {
    case 'left':
      return { left: -size, top: clamp(offset.top, maxTop) };
    case 'right':
      return { left: widthPx, top: clamp(offset.top, maxTop) };
    case 'up':
      return { left: clamp(offset.left, maxLeft), top: -size };
    case 'down':
      return { left: clamp(offset.left, maxLeft), top: heightPx };
  }
}

export function pinDisplaySize(pin: Pin, cellSize = CELL_SIZE): number {
  const modelSize = Math.max(Math.abs(pin.width ?? 0), Math.abs(pin.height ?? 0)) * cellSize;
  const size = modelSize > 0 ? modelSize : cellSize * FALLBACK_SIZE_FACTOR;
  const maximum = Math.max(MIN_SIZE_PX, cellSize * MAX_SIZE_FACTOR);
  return Math.min(Math.max(size, MIN_SIZE_PX), maximum);
}

export function modulePinViews(module: Module, cellSize = CELL_SIZE): DisplayPin[] {
  const allPins = [...(module.inputs ?? []), ...(module.outputs ?? [])];
  const groups = new Map<string, Pin[]>();

  allPins.forEach((pin, index) => {
    const key = isRigidPin(pin)
      ? `rigid:${anchorKey(pin)}`
      : `pin:${index}:${pin.direction ?? 'pin'}:${anchorKey(pin)}`;
    const group = groups.get(key);
    if (group) {
      group.push(pin);
    } else {
      groups.set(key, [pin]);
    }
  });

  const views: DisplayPin[] = [];
  for (const [key, pins] of groups.entries()) {
    const first = pins[0];
    const inputPin = pins.find((pin) => pin.direction === 'input');
    const outputPin = pins.find((pin) => pin.direction === 'output');
    const hasInput = inputPin !== undefined;
    const hasOutput = outputPin !== undefined;
    const direction: PinDirection =
      hasInput && hasOutput ? 'bidirectional' : hasOutput ? 'output' : 'input';
    const rawOffset = pinPixelOffset(first, cellSize, module);
    const size = Math.max(...pins.map((pin) => pinDisplaySize(pin, cellSize)));
    const offset = pinEdgeOffset(
      rawOffset,
      size,
      pins.map(orientationOf).find((orientation) => orientation !== null) ?? null,
      module,
      cellSize,
    );
    const tooltip =
      direction === 'bidirectional'
        ? `Bidirectional pin · ${pins.map(describePin).join(' + ')}`
        : describePin(first);

    views.push({
      key,
      pin: first,
      inputPin,
      outputPin,
      inputIndex: inputPin?.index,
      outputIndex: outputPin?.index,
      direction,
      orientation: pins.map(orientationOf).find((orientation) => orientation !== null) ?? null,
      left: offset.left,
      top: offset.top,
      size,
      label: labelOf(pins),
      tooltip,
      connected: pins.some((pin) => (pin.connectionIds?.length ?? 0) > 0),
    });
  }

  return views;
}
