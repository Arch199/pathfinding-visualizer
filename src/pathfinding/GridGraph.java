package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A grid of Cells viewed as a Graph.
 */
public class GridGraph implements Graph<Cell> {	
	private Cell[][] cells;
	private Cell start, end;
	private final int width, height;
	private int cellSize;
	
	public GridGraph(int width, int height, int cellSize) {
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		cells = new Cell[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j, this);
			}
		}
	}
	
	@Override
	public List<Cell> getConnected(Cell cell) {
		int i = cell.getI(), j = cell.getJ();
		List<Cell> adjacents = new ArrayList<Cell>();
		int[] xOffsets = {1, 0, -1, 0}, yOffsets = {0, 1, 0, -1};
		for (int counter = 0; counter < xOffsets.length; counter++) {
			int x = xOffsets[counter], y = yOffsets[counter];
			if (i+x >= 0 && i+x <= cells.length && j+y >= 0 && j+y <= cells[0].length && cells[i+x][j+y].canVisit()) {
				adjacents.add(cells[i+x][j+y]);
			}
		}
		return Collections.unmodifiableList(adjacents);
	}
	
	@Override
	public void setPath(Cell start, Cell end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Both start and end must be valid cells");
		}
		this.start = start;
		this.end = end;
	}
	
	@Override
	public void clearPathData() {
		start = end = null;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j].clearPathData();
			}
		}
	}
	
	@Override public int getNodeSize() { return cellSize; }
	@Override public void setNodeSize(int nodeSize) { cellSize = nodeSize; }
	@Override public Cell getStart() { return start; }
	@Override public Cell getEnd() { return end; }
	
	public Cell get(int x, int y) { return cells[x][y];	}
	public int getWidth() { return cells.length; }
	public int getHeight() { return cells[0].length; }
	
}
