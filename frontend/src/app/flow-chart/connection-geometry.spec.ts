import { describe, expect, it } from 'vitest';
import { Connection, Module, Pin } from '../api';
import {
  connectionPathViews,
  connectionPaths,
  isRigidConnection,
  moveConnectionSegment,
  orthogonalConnectionPath,
  pinCenterPoint,
  resolvePinReference,
} from './connection-geometry';
import { pinDisplaySize } from './pin-geometry';

const DEFAULT = 'org.jojo.flow.model.flowChart.modules.DefaultPin';

function moduleWithPins(id: number, x: number, y: number): Module {
  const input: Pin = {
    index: 0,
    direction: 'input',
    implementationClass: DEFAULT,
    position: { x: 0, y: 1 },
    linePoint: { x: 0, y: 1 },
    width: 1,
    height: 1,
    orientation: 'LEFT',
  };
  const output: Pin = {
    index: 0,
    direction: 'output',
    implementationClass: DEFAULT,
    position: { x: 4, y: 1 },
    linePoint: { x: 4, y: 1 },
    width: 1,
    height: 1,
    orientation: 'RIGHT',
  };
  return { id, position: { x, y }, width: 4, height: 4, inputs: [input], outputs: [output] };
}

function pathPoints(path: string): { x: number; y: number }[] {
  const tokens = path.split(' ');
  const points: { x: number; y: number }[] = [];
  for (let index = 0; index < tokens.length; index += 3) {
    points.push({ x: Number(tokens[index + 1]), y: Number(tokens[index + 2]) });
  }
  return points;
}

function pathIntersectsRect(
  path: string,
  rect: { x: number; y: number; width: number; height: number },
): boolean {
  const points = pathPoints(path);
  for (let index = 1; index < points.length; index++) {
    const start = points[index - 1];
    const end = points[index];
    if (
      start.y === end.y &&
      start.y > rect.y &&
      start.y < rect.y + rect.height &&
      Math.max(start.x, end.x) > rect.x &&
      Math.min(start.x, end.x) < rect.x + rect.width
    ) {
      return true;
    }
    if (
      start.x === end.x &&
      start.x > rect.x &&
      start.x < rect.x + rect.width &&
      Math.max(start.y, end.y) > rect.y &&
      Math.min(start.y, end.y) < rect.y + rect.height
    ) {
      return true;
    }
  }
  return false;
}

describe('connection-geometry', () => {
  it('places a pin center relative to its module and cell size', () => {
    const module = moduleWithPins(1, 2, 3);
    const pin = module.outputs?.[0];
    const size = pinDisplaySize(pin!);
    const center = pinCenterPoint(pin!, module, 20);

    expect(size).toBe(15);
    expect(center).toEqual({ x: 40 + 4 * 20 + size / 2, y: 60 + 1 * 20 + size / 2 });
  });

  it('aligns connection endpoints with rendered left input pins', () => {
    const module = moduleWithPins(1, 10, 0);
    const pin = module.inputs?.[0];
    const size = pinDisplaySize(pin!, 20);

    expect(pinCenterPoint(pin!, module, 20)).toEqual({ x: 200 - size / 2, y: 20 + size / 2 });
  });

  it('creates a direct path when endpoints share a coordinate', () => {
    expect(orthogonalConnectionPath({ x: 0, y: 5 }, { x: 10, y: 5 })).toBe('M 0 5 L 10 5');
  });

  it('creates an orthogonal path with a middle vertical segment', () => {
    const path = orthogonalConnectionPath({ x: 0, y: 0 }, { x: 20, y: 10 });

    expect(path).toBe('M 0 0 L 10 0 L 10 10 L 20 10');
  });

  it('resolves pin references by direction and index', () => {
    const modules = [moduleWithPins(1, 0, 0)];

    expect(resolvePinReference(modules, { moduleId: 1, index: 0, direction: 'output' })?.pin).toBe(
      modules[0].outputs?.[0],
    );
    expect(resolvePinReference(modules, { moduleId: 1, index: 0, direction: 'input' })?.pin).toBe(
      modules[0].inputs?.[0],
    );
    expect(resolvePinReference(modules, { moduleId: 2, index: 0, direction: 'input' })).toBeNull();
  });

  it('separates connections that share the same endpoints', () => {
    const modules = [moduleWithPins(1, 0, 0), moduleWithPins(2, 10, 5)];
    const first: Connection = {
      id: 9,
      className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
      from: { moduleId: 1, index: 0, direction: 'output' },
      to: [{ moduleId: 2, index: 0, direction: 'input' }],
    };
    const second: Connection = { ...first, id: 10 };

    const views = connectionPathViews([first, second], modules, 20);

    expect(views).toHaveLength(2);
    expect(views[0].paths).toHaveLength(1);
    expect(views[1].paths).toHaveLength(1);
    expect(views[0].paths[0]).not.toBe(views[1].paths[0]);
  });

  it('routes a path around an intervening module', () => {
    const modules = [moduleWithPins(1, 0, 0), moduleWithPins(2, 5, 0), moduleWithPins(3, 10, 0)];
    const connection: Connection = {
      id: 9,
      className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
      from: { moduleId: 1, index: 0, direction: 'output' },
      to: [{ moduleId: 3, index: 0, direction: 'input' }],
    };
    const blocker = { x: 100, y: 0, width: 80, height: 80 };

    const path = connectionPaths(connection, modules, 20)[0];

    expect(pathIntersectsRect(path, blocker)).toBe(false);
  });

  it('uses a customized route instead of automatic routing', () => {
    const modules = [moduleWithPins(1, 0, 0), moduleWithPins(2, 10, 5)];
    const connection: Connection = {
      id: 9,
      className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
      from: { moduleId: 1, index: 0, direction: 'output' },
      to: [{ moduleId: 2, index: 0, direction: 'input' }],
      routes: [
        {
          to: { moduleId: 2, index: 0, direction: 'input' },
          diversionPoints: [{ x: 8, y: 2 }],
          customized: true,
        },
      ],
    };

    const view = connectionPathViews([connection], modules, 20)[0];

    expect(view.routes[0].customized).toBe(true);
    expect(view.routes[0].points).toContainEqual({ x: 160, y: 40 });
    expect(view.paths[0]).toContain('160 40');
  });

  it('moves an internal segment perpendicularly', () => {
    const points = [
      { x: 0, y: 0 },
      { x: 10, y: 0 },
      { x: 10, y: 10 },
      { x: 20, y: 10 },
    ];

    expect(moveConnectionSegment(points, 1, 5, [], 20)).toEqual([
      { x: 0, y: 0 },
      { x: 15, y: 0 },
      { x: 15, y: 10 },
      { x: 20, y: 10 },
    ]);
  });

  it('adds a bend when dragging the only segment of a direct path', () => {
    const points = [
      { x: 0, y: 0 },
      { x: 20, y: 0 },
    ];

    expect(moveConnectionSegment(points, 0, 5, [], 20)).toEqual([
      { x: 0, y: 0 },
      { x: 0, y: 5 },
      { x: 20, y: 5 },
      { x: 20, y: 0 },
    ]);
  });

  it('creates one path per connection target', () => {
    const modules = [moduleWithPins(1, 0, 0), moduleWithPins(2, 10, 5)];
    const connection: Connection = {
      id: 9,
      className: 'org.jojo.flow.model.flowChart.connections.DefaultArrow',
      from: { moduleId: 1, index: 0, direction: 'output' },
      to: [{ moduleId: 2, index: 0, direction: 'input' }],
    };

    const paths = connectionPaths(connection, modules, 20);
    const target = modules[1].inputs?.[0];
    const targetCenter = pinCenterPoint(target!, modules[1], 20);
    const lastPoint = pathPoints(paths[0]).at(-1);

    expect(paths).toHaveLength(1);
    expect(paths[0].startsWith('M ')).toBe(true);
    expect(paths[0]).toContain(' L ');
    expect(lastPoint?.x).toBeCloseTo(targetCenter.x - pinDisplaySize(target!, 20) / 2);
    expect(lastPoint?.y).toBeCloseTo(targetCenter.y);
    expect(isRigidConnection(connection)).toBe(false);
    expect(
      isRigidConnection({
        className: 'org.jojo.flow.model.flowChart.connections.RigidConnection',
      }),
    ).toBe(true);
  });
});
