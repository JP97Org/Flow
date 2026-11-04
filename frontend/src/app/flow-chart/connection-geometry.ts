import { Connection, ConnectionRoute, Module, Pin, PinReference } from '../api';
import { CELL_SIZE } from './grid-geometry';
import { pinDisplayOffset, pinDisplaySize } from './pin-geometry';

export interface DiagramPoint {
  x: number;
  y: number;
}

export interface ResolvedPin {
  module: Module;
  pin: Pin;
}

export interface ConnectionRouteView {
  to?: PinReference;
  points: DiagramPoint[];
  customized: boolean;
  endTrim: number;
}

export interface ConnectionPathView {
  connection: Connection;
  paths: string[];
  routes: ConnectionRouteView[];
}

interface DiagramRect {
  x: number;
  y: number;
  width: number;
  height: number;
}

interface PathSpec {
  connection: Connection;
  toReference?: PinReference;
  from: DiagramPoint;
  to: DiagramPoint;
  endTrim: number;
  key: string;
  customPoints: DiagramPoint[] | null;
}

const LANE_SPACING = CELL_SIZE * 0.75;
const ROUTE_MARGIN = CELL_SIZE * 0.5;
const MAX_LANE_STEPS = 8;

export function pinCenterPoint(pin: Pin, module: Module, cellSize = CELL_SIZE): DiagramPoint {
  const offset = pinDisplayOffset(pin, module, cellSize);
  const size = pinDisplaySize(pin, cellSize);
  return {
    x: (module.position?.x ?? 0) * cellSize + offset.left + size / 2,
    y: (module.position?.y ?? 0) * cellSize + offset.top + size / 2,
  };
}

export function resolvePinReference(
  modules: readonly Module[],
  reference: PinReference | undefined,
): ResolvedPin | null {
  if (!reference || reference.moduleId === undefined || reference.index === undefined) {
    return null;
  }
  const module = modules.find((candidate) => candidate.id === reference.moduleId);
  if (!module) {
    return null;
  }
  const pins = reference.direction === 'input' ? module.inputs : module.outputs;
  const pin = pins?.[reference.index];
  return pin ? { module, pin } : null;
}

export function orthogonalConnectionPath(from: DiagramPoint, to: DiagramPoint): string {
  const points: DiagramPoint[] = [from];
  if (from.x !== to.x && from.y !== to.y) {
    const middleX = from.x + (to.x - from.x) / 2;
    points.push({ x: middleX, y: from.y }, { x: middleX, y: to.y });
  }
  points.push(to);
  return pathToString(points);
}

export function connectionPaths(
  connection: Connection,
  modules: readonly Module[],
  cellSize = CELL_SIZE,
): string[] {
  const from = resolvePinReference(modules, connection.from);
  if (!from) {
    return [];
  }
  const fromPoint = pinCenterPoint(from.pin, from.module, cellSize);
  const obstacles = moduleRects(modules, cellSize);
  return (connection.to ?? [])
    .map((reference) => resolvePinReference(modules, reference))
    .filter((resolved): resolved is ResolvedPin => resolved !== null)
    .map((to, index) => {
      const toPoint = pinCenterPoint(to.pin, to.module, cellSize);
      const endTrim = pinDisplaySize(to.pin, cellSize) / 2;
      const reference = connection.to?.[index];
      const customPoints = customRoutePoints(connection, reference, cellSize);
      const points = customPoints
        ? orthogonalizeThrough(fromPoint, toPoint, customPoints)
        : routePath(fromPoint, toPoint, obstacles, 0);
      return pathToString(trimPathEnd(points, endTrim));
    });
}

export function connectionPathViews(
  connections: readonly Connection[],
  modules: readonly Module[],
  cellSize = CELL_SIZE,
): ConnectionPathView[] {
  const obstacles = moduleRects(modules, cellSize);
  const specs: PathSpec[] = [];

  for (const connection of connections) {
    const from = resolvePinReference(modules, connection.from);
    if (!from) {
      continue;
    }
    const fromPoint = pinCenterPoint(from.pin, from.module, cellSize);
    for (const reference of connection.to ?? []) {
      const to = resolvePinReference(modules, reference);
      if (!to) {
        continue;
      }
      const toPoint = pinCenterPoint(to.pin, to.module, cellSize);
      specs.push({
        connection,
        toReference: reference,
        from: fromPoint,
        to: toPoint,
        endTrim: pinDisplaySize(to.pin, cellSize) / 2,
        key: endpointKey(fromPoint, toPoint),
        customPoints: customRoutePoints(connection, reference, cellSize),
      });
    }
  }

  const occurrences = new Map<string, number>();
  const pathsByConnection = new Map<Connection, string[]>();
  const routesByConnection = new Map<Connection, ConnectionRouteView[]>();
  const usedPaths = new Set<string>();
  for (const spec of specs) {
    const occurrence = occurrences.get(spec.key) ?? 0;
    occurrences.set(spec.key, occurrence + 1);
    let points: DiagramPoint[];
    let customized = false;
    if (spec.customPoints) {
      points = orthogonalizeThrough(spec.from, spec.to, spec.customPoints);
      customized = true;
      usedPaths.add(pathToString(trimPathEnd(points, spec.endTrim)));
    } else {
      const lane = laneOffset(occurrence);
      points = routePath(spec.from, spec.to, obstacles, lane);
      let path = pathToString(trimPathEnd(points, spec.endTrim));
      for (let attempt = 1; attempt <= MAX_LANE_STEPS && usedPaths.has(path); attempt++) {
        const extra = attempt % 2 === 1 ? attempt : -attempt;
        points = routePath(spec.from, spec.to, obstacles, lane + extra * LANE_SPACING);
        path = pathToString(trimPathEnd(points, spec.endTrim));
      }
      usedPaths.add(path);
    }
    const path = pathToString(trimPathEnd(points, spec.endTrim));
    const paths = pathsByConnection.get(spec.connection) ?? [];
    paths.push(path);
    pathsByConnection.set(spec.connection, paths);
    const routes = routesByConnection.get(spec.connection) ?? [];
    routes.push({
      to: spec.toReference,
      points,
      customized,
      endTrim: spec.endTrim,
    });
    routesByConnection.set(spec.connection, routes);
  }

  return connections.map((connection) => ({
    connection,
    paths: pathsByConnection.get(connection) ?? [],
    routes: routesByConnection.get(connection) ?? [],
  }));
}

export function isRigidConnection(connection: Connection): boolean {
  return (connection.className ?? '').split('.').pop() === 'RigidConnection';
}

export function pathFromPoints(points: readonly DiagramPoint[], endTrim = 0): string {
  return pathToString(trimPathEnd(points, endTrim));
}

export function moveConnectionSegment(
  points: readonly DiagramPoint[],
  segmentIndex: number,
  delta: number,
  modules: readonly Module[],
  cellSize = CELL_SIZE,
): DiagramPoint[] {
  if (segmentIndex < 0 || segmentIndex >= points.length - 1) {
    return points.map((point) => ({ ...point }));
  }
  const source = points.map((point) => ({ ...point }));
  const start = source[segmentIndex];
  const end = source[segmentIndex + 1];
  const horizontal = start.y === end.y;
  if (!horizontal && start.x !== end.x) {
    return source;
  }

  const originalCoordinate = horizontal ? start.y : start.x;
  const obstacles = moduleRects(modules, cellSize);
  const desired = originalCoordinate + delta;
  const candidates = [
    desired,
    ...obstacles.flatMap((rect) =>
      horizontal
        ? [rect.y - ROUTE_MARGIN, rect.y + rect.height + ROUTE_MARGIN]
        : [rect.x - ROUTE_MARGIN, rect.x + rect.width + ROUTE_MARGIN],
    ),
  ].sort((first, second) => Math.abs(first - desired) - Math.abs(second - desired));

  let fallback: DiagramPoint[] | null = null;
  for (const candidate of candidates) {
    const moved = moveSegmentRaw(source, segmentIndex, candidate - originalCoordinate);
    if (!fallback) {
      fallback = moved;
    }
    if (obstacleIntersections(moved, obstacles) === 0) {
      return moved;
    }
  }
  return fallback ?? source;
}

function customRoutePoints(
  connection: Connection,
  reference: PinReference | undefined,
  cellSize: number,
): DiagramPoint[] | null {
  const key = referenceKey(reference);
  if (key === null) {
    return null;
  }
  const route = (connection.routes ?? []).find(
    (candidate: ConnectionRoute) =>
      candidate.customized === true && referenceKey(candidate.to) === key,
  );
  if (!route) {
    return null;
  }
  return (route.diversionPoints ?? []).map((point) => ({
    x: (point.x ?? 0) * cellSize,
    y: (point.y ?? 0) * cellSize,
  }));
}

function referenceKey(reference: PinReference | undefined): string | null {
  if (!reference || reference.moduleId === undefined || reference.index === undefined) {
    return null;
  }
  return `${reference.moduleId}:${reference.index}:${reference.direction ?? ''}`;
}

function orthogonalizeThrough(
  from: DiagramPoint,
  to: DiagramPoint,
  via: readonly DiagramPoint[],
): DiagramPoint[] {
  const points: DiagramPoint[] = [{ ...from }];
  let current = from;
  for (const next of via) {
    if (current.x === next.x && current.y === next.y) {
      continue;
    }
    if (current.x !== next.x && current.y !== next.y) {
      points.push({ x: next.x, y: current.y });
    }
    points.push({ ...next });
    current = next;
  }
  if (current.x !== to.x && current.y !== to.y) {
    points.push({ x: to.x, y: current.y });
  }
  points.push({ ...to });
  return simplifyPath(points);
}

function moveSegmentRaw(
  points: readonly DiagramPoint[],
  segmentIndex: number,
  delta: number,
): DiagramPoint[] {
  const result = points.map((point) => ({ ...point }));
  const start = result[segmentIndex];
  const end = result[segmentIndex + 1];
  const horizontal = start.y === end.y;
  if (result.length === 2) {
    const newY = horizontal ? start.y + delta : start.y;
    const newX = horizontal ? start.x : start.x + delta;
    return simplifyPath([
      start,
      { x: newX, y: newY },
      { x: horizontal ? end.x : newX, y: horizontal ? newY : end.y },
      end,
    ]);
  }
  if (horizontal) {
    const newY = start.y + delta;
    if (segmentIndex === 0) {
      return simplifyPath([
        start,
        { x: start.x, y: newY },
        { x: end.x, y: newY },
        ...result.slice(2),
      ]);
    }
    if (segmentIndex === result.length - 2) {
      return simplifyPath([
        ...result.slice(0, result.length - 2),
        { x: start.x, y: newY },
        { x: end.x, y: newY },
        end,
      ]);
    }
    start.y = newY;
    end.y = newY;
  } else {
    const newX = start.x + delta;
    if (segmentIndex === 0) {
      return simplifyPath([
        start,
        { x: newX, y: start.y },
        { x: newX, y: end.y },
        ...result.slice(2),
      ]);
    }
    if (segmentIndex === result.length - 2) {
      return simplifyPath([
        ...result.slice(0, result.length - 2),
        { x: newX, y: start.y },
        { x: newX, y: end.y },
        end,
      ]);
    }
    start.x = newX;
    end.x = newX;
  }
  return simplifyPath(result);
}

function simplifyPath(points: readonly DiagramPoint[]): DiagramPoint[] {
  const result: DiagramPoint[] = [];
  for (const point of points) {
    const last = result[result.length - 1];
    if (last && last.x === point.x && last.y === point.y) {
      continue;
    }
    while (result.length >= 2) {
      const before = result[result.length - 2];
      const current = result[result.length - 1];
      const collinearX = before.x === current.x && current.x === point.x;
      const collinearY = before.y === current.y && current.y === point.y;
      if (collinearX || collinearY) {
        result.pop();
      } else {
        break;
      }
    }
    result.push({ ...point });
  }
  return result;
}

function moduleRects(modules: readonly Module[], cellSize: number): DiagramRect[] {
  return modules.map((module) => ({
    x: (module.position?.x ?? 0) * cellSize,
    y: (module.position?.y ?? 0) * cellSize,
    width: Math.max(1, module.width ?? 1) * cellSize,
    height: Math.max(1, module.height ?? 1) * cellSize,
  }));
}

function endpointKey(from: DiagramPoint, to: DiagramPoint): string {
  const first = `${from.x.toFixed(2)},${from.y.toFixed(2)}`;
  const second = `${to.x.toFixed(2)},${to.y.toFixed(2)}`;
  return first < second ? `${first}|${second}` : `${second}|${first}`;
}

function laneOffset(occurrence: number): number {
  if (occurrence === 0) {
    return 0;
  }
  const step = Math.ceil(occurrence / 2);
  return occurrence % 2 === 1 ? step * LANE_SPACING : -step * LANE_SPACING;
}

function routePath(
  from: DiagramPoint,
  to: DiagramPoint,
  obstacles: readonly DiagramRect[],
  lane: number,
): DiagramPoint[] {
  const candidates: DiagramPoint[][] = [];
  const verticalXs = candidateCoordinates((from.x + to.x) / 2, lane, obstacles, true);
  const horizontalYs = candidateCoordinates((from.y + to.y) / 2, lane, obstacles, false);

  for (const x of verticalXs) {
    const path = verticalPath(from, to, x);
    if (path) {
      candidates.push(path);
    }
  }
  for (const y of horizontalYs) {
    const path = horizontalPath(from, to, y);
    if (path) {
      candidates.push(path);
    }
  }

  if (candidates.length === 0) {
    return simplifyPath([from, to]);
  }

  let best = candidates[0];
  let bestIntersections = Number.POSITIVE_INFINITY;
  for (const candidate of candidates) {
    const intersections = obstacleIntersections(candidate, obstacles);
    if (intersections === 0) {
      return simplifyPath(candidate);
    }
    if (intersections < bestIntersections) {
      best = candidate;
      bestIntersections = intersections;
    }
  }
  return simplifyPath(best);
}

function candidateCoordinates(
  base: number,
  lane: number,
  obstacles: readonly DiagramRect[],
  vertical: boolean,
): number[] {
  const target = base + lane;
  const ordered: number[] = [target];
  for (let step = 1; step <= MAX_LANE_STEPS; step++) {
    ordered.push(target + step * LANE_SPACING, target - step * LANE_SPACING);
  }

  const edges = obstacles
    .flatMap((rect) =>
      vertical
        ? [rect.x - ROUTE_MARGIN, rect.x + rect.width + ROUTE_MARGIN]
        : [rect.y - ROUTE_MARGIN, rect.y + rect.height + ROUTE_MARGIN],
    )
    .sort((first, second) => Math.abs(first - target) - Math.abs(second - target));

  return [...new Set([...ordered, ...edges])];
}

function verticalPath(from: DiagramPoint, to: DiagramPoint, x: number): DiagramPoint[] | null {
  if (from.y === to.y) {
    return null;
  }
  return [from, { x, y: from.y }, { x, y: to.y }, to];
}

function horizontalPath(from: DiagramPoint, to: DiagramPoint, y: number): DiagramPoint[] | null {
  if (from.x === to.x) {
    return null;
  }
  return [from, { x: from.x, y }, { x: to.x, y }, to];
}

function trimPathEnd(points: readonly DiagramPoint[], distance: number): DiagramPoint[] {
  if (distance <= 0 || points.length < 2) {
    return [...points];
  }
  const trimmed = points.map((point) => ({ ...point }));
  let remaining = distance;
  while (trimmed.length > 2 && remaining > 0) {
    const previous = trimmed[trimmed.length - 2];
    const end = trimmed[trimmed.length - 1];
    const length = Math.hypot(end.x - previous.x, end.y - previous.y);
    if (length === 0) {
      trimmed.pop();
    } else if (length > remaining) {
      const ratio = (length - remaining) / length;
      trimmed[trimmed.length - 1] = {
        x: previous.x + (end.x - previous.x) * ratio,
        y: previous.y + (end.y - previous.y) * ratio,
      };
      remaining = 0;
    } else {
      remaining -= length;
      trimmed.pop();
    }
  }
  if (trimmed.length < 2) {
    return [...points];
  }
  if (remaining > 0) {
    const previous = trimmed[trimmed.length - 2];
    const end = trimmed[trimmed.length - 1];
    const length = Math.hypot(end.x - previous.x, end.y - previous.y);
    if (length > remaining) {
      const ratio = (length - remaining) / length;
      trimmed[trimmed.length - 1] = {
        x: previous.x + (end.x - previous.x) * ratio,
        y: previous.y + (end.y - previous.y) * ratio,
      };
    }
  }
  return trimmed;
}

function obstacleIntersections(
  points: readonly DiagramPoint[],
  obstacles: readonly DiagramRect[],
): number {
  let intersections = 0;
  for (let index = 1; index < points.length; index++) {
    const start = points[index - 1];
    const end = points[index];
    for (const rect of obstacles) {
      if (segmentIntersectsRect(start, end, rect)) {
        intersections++;
      }
    }
  }
  return intersections;
}

function segmentIntersectsRect(start: DiagramPoint, end: DiagramPoint, rect: DiagramRect): boolean {
  if (start.y === end.y) {
    const y = start.y;
    return (
      y > rect.y &&
      y < rect.y + rect.height &&
      Math.max(start.x, end.x) > rect.x &&
      Math.min(start.x, end.x) < rect.x + rect.width
    );
  }
  if (start.x === end.x) {
    const x = start.x;
    return (
      x > rect.x &&
      x < rect.x + rect.width &&
      Math.max(start.y, end.y) > rect.y &&
      Math.min(start.y, end.y) < rect.y + rect.height
    );
  }
  return false;
}

function pathToString(points: readonly DiagramPoint[]): string {
  return points.map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`).join(' ');
}
