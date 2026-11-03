import { Module } from '../api';

/**
 * Grid cell size in pixels. One logical flow-chart unit corresponds to one grid cell.
 */
export const CELL_SIZE = 20;

/**
 * Minimum grid dimensions to ensure usability even with small flow charts.
 */
export const MIN_COLUMNS = 40;
export const MIN_ROWS = 24;

/**
 * Padding around the grid content in cells.
 */
export const GRID_PADDING = 4;

/**
 * A point in grid cell coordinates.
 */
export interface GridPoint {
  x: number;
  y: number;
}

/**
 * A rectangle in grid cell coordinates.
 */
export interface GridRect {
  x: number;
  y: number;
  width: number;
  height: number;
}

/**
 * Computed grid metrics based on module extents.
 */
export interface GridMetrics {
  columns: number;
  rows: number;
  widthPx: number;
  heightPx: number;
}

/**
 * Converts a module's position and dimensions to a grid footprint.
 * Uses width/height from the module DTO (defaults to 1 if missing).
 */
export function moduleFootprint(module: Module): GridRect {
  const x = module.position?.x ?? 0;
  const y = module.position?.y ?? 0;
  const width = Math.max(1, Math.round(module.width ?? 1));
  const height = Math.max(1, Math.round(module.height ?? 1));
  return { x, y, width, height };
}

/**
 * Checks if two rectangles overlap (excluding touching edges).
 */
export function rectsOverlap(a: GridRect, b: GridRect): boolean {
  return a.x < b.x + b.width &&
         a.x + a.width > b.x &&
         a.y < b.y + b.height &&
         a.y + a.height > b.y;
}

/**
 * Checks if a target cell is occupied by any module footprint.
 * For palette additions, only the single cell is checked (footprint 1x1).
 */
export function isCellOccupied(modules: readonly Module[], cell: GridPoint): boolean {
  return modules.some(m => {
    const fp = moduleFootprint(m);
    return cell.x >= fp.x && cell.x < fp.x + fp.width &&
           cell.y >= fp.y && cell.y < fp.y + fp.height;
  });
}

/**
 * Checks if a full footprint at target cell is free (no overlap with other modules).
 * Optionally excludes a module by ID (for moving itself).
 */
export function isFootprintFree(modules: readonly Module[], target: GridRect, excludeId?: number): boolean {
  for (const m of modules) {
    if (excludeId !== undefined && m.id === excludeId) {
      continue;
    }
    const fp = moduleFootprint(m);
    if (rectsOverlap(target, fp)) {
      return false;
    }
  }
  return true;
}

/**
 * Computes grid metrics based on module extents with minimum constraints.
 */
export function gridMetrics(modules: readonly Module[], cellSize = CELL_SIZE): GridMetrics {
  if (modules.length === 0) {
    return {
      columns: MIN_COLUMNS,
      rows: MIN_ROWS,
      widthPx: MIN_COLUMNS * cellSize,
      heightPx: MIN_ROWS * cellSize,
    };
  }

  let maxRight = 0;
  let maxBottom = 0;

  for (const m of modules) {
    const fp = moduleFootprint(m);
    const right = fp.x + fp.width;
    const bottom = fp.y + fp.height;
    if (right > maxRight) maxRight = right;
    if (bottom > maxBottom) maxBottom = bottom;
  }

  const columns = Math.max(MIN_COLUMNS, maxRight + GRID_PADDING);
  const rows = Math.max(MIN_ROWS, maxBottom + GRID_PADDING);

  return {
    columns,
    rows,
    widthPx: columns * cellSize,
    heightPx: rows * cellSize,
  };
}

/**
 * Calculates the grid cell at the given client coordinates relative to a canvas element.
 * Accounts for scrolling via getBoundingClientRect.
 */
export function cellFromClientPoint(clientX: number, clientY: number, canvas: HTMLElement, cellSize = CELL_SIZE): GridPoint {
  const rect = canvas.getBoundingClientRect();
  const x = Math.floor((clientX - rect.left) / cellSize);
  const y = Math.floor((clientY - rect.top) / cellSize);
  return { x, y };
}

/**
 * Snaps a rectangle's top-left corner to the nearest grid cell.
 */
export function snapToCell(left: number, top: number, cellSize = CELL_SIZE): GridPoint {
  return {
    x: Math.round(left / cellSize),
    y: Math.round(top / cellSize),
  };
}

/**
 * Converts a grid cell to pixel coordinates (top-left of the cell).
 */
export function cellToPixels(cell: GridPoint, cellSize = CELL_SIZE): GridPoint {
  return {
    x: cell.x * cellSize,
    y: cell.y * cellSize,
  };
}

/**
 * Returns true if the given cell is inside the grid bounds.
 */
export function isCellInBounds(cell: GridPoint, columns: number, rows: number): boolean {
  return cell.x >= 0 && cell.y >= 0 &&
         cell.x < columns && cell.y < rows;
}
