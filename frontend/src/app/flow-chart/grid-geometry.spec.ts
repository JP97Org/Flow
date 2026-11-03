import { describe, it, expect } from 'vitest';
import {
  CELL_SIZE,
  GRID_PADDING,
  MIN_COLUMNS,
  MIN_ROWS,
  GridPoint,
  GridRect,
  moduleFootprint,
  rectsOverlap,
  isCellOccupied,
  isFootprintFree,
  gridMetrics,
  cellFromClientPoint,
  snapToCell,
  cellToPixels,
  isCellInBounds,
} from './grid-geometry';

describe('grid-geometry', () => {
  it('computes module footprint', () => {
    const mod = { id: 1, position: { x: 120, y: 80 }, width: 10, height: 10 } as any;
    const fp = moduleFootprint(mod);
    expect(fp.x).toBe(120);
    expect(fp.y).toBe(80);
    expect(fp.width).toBe(10);
    expect(fp.height).toBe(10);
  });

  it('defaults module dimensions', () => {
    const mod = { id: 1, position: { x: 0, y: 0 } } as any;
    const fp = moduleFootprint(mod);
    expect(fp.width).toBe(1);
    expect(fp.height).toBe(1);
  });

  it('detects overlapping rectangles', () => {
    const a: GridRect = { x: 0, y: 0, width: 10, height: 10 };
    const b: GridRect = { x: 5, y: 5, width: 10, height: 10 };
    const c: GridRect = { x: 10, y: 10, width: 10, height: 10 };

    expect(rectsOverlap(a, b)).toBe(true);
    expect(rectsOverlap(a, c)).toBe(false);
    expect(rectsOverlap(b, c)).toBe(true);
  });

  it('checks cell occupancy', () => {
    const modules = [
      { id: 1, position: { x: 0, y: 0 }, width: 10, height: 10 } as any,
      { id: 2, position: { x: 20, y: 0 }, width: 10, height: 10 } as any,
    ];

    expect(isCellOccupied(modules, { x: 5, y: 5 })).toBe(true);
    expect(isCellOccupied(modules, { x: 15, y: 5 })).toBe(false);
    expect(isCellOccupied(modules, { x: 25, y: 5 })).toBe(true);
  });

  it('checks footprint availability', () => {
    const modules = [{ id: 1, position: { x: 0, y: 0 }, width: 10, height: 10 } as any];
    const target: GridRect = { x: 0, y: 0, width: 10, height: 10 };

    expect(isFootprintFree(modules, target)).toBe(false);
    expect(isFootprintFree(modules, target, 1)).toBe(true);
    expect(isFootprintFree(modules, { x: 10, y: 0, width: 10, height: 10 })).toBe(true);
  });

  it('computes grid metrics for empty chart', () => {
    const metrics = gridMetrics([]);
    expect(metrics.columns).toBe(MIN_COLUMNS);
    expect(metrics.rows).toBe(MIN_ROWS);
    expect(metrics.widthPx).toBe(MIN_COLUMNS * CELL_SIZE);
    expect(metrics.heightPx).toBe(MIN_ROWS * CELL_SIZE);
  });

  it('computes grid metrics from modules', () => {
    const modules = [
      { id: 1, position: { x: 0, y: 0 }, width: 10, height: 10 } as any,
      { id: 2, position: { x: 50, y: 30 }, width: 10, height: 10 } as any,
    ];
    const metrics = gridMetrics(modules);
    expect(metrics.columns).toBe(64);
    expect(metrics.rows).toBe(44);
  });

  it('calculates cell from client point', () => {
    const clientX = 100 + 5 * CELL_SIZE + 10;
    const clientY = 50 + 3 * CELL_SIZE + 5;
    const canvas = { getBoundingClientRect: () => ({ left: 100, top: 50 }) } as any;
    const cell = cellFromClientPoint(clientX, clientY, canvas);
    expect(cell.x).toBe(5);
    expect(cell.y).toBe(3);
  });

  it('snaps to cell', () => {
    expect(snapToCell(5 * CELL_SIZE + 8, 3 * CELL_SIZE + 12)).toEqual({ x: 5, y: 4 });
    expect(snapToCell(5 * CELL_SIZE - 5, 3 * CELL_SIZE - 6)).toEqual({ x: 5, y: 3 });
  });

  it('converts cell to pixels', () => {
    expect(cellToPixels({ x: 5, y: 3 })).toEqual({ x: 5 * CELL_SIZE, y: 3 * CELL_SIZE });
  });

  it('checks cell bounds', () => {
    expect(isCellInBounds({ x: 0, y: 0 }, 40, 24)).toBe(true);
    expect(isCellInBounds({ x: 39, y: 23 }, 40, 24)).toBe(true);
    expect(isCellInBounds({ x: -1, y: 0 }, 40, 24)).toBe(false);
    expect(isCellInBounds({ x: 40, y: 0 }, 40, 24)).toBe(false);
  });
});
