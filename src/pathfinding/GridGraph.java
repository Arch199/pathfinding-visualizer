package pathfinding;

import java.util.ArrayList;
import java.util.List;

import pathfinding.Node;

public class GridGraph implements Graph {
	private Node[][] cells;
	private Node start, end;
	private int width, height, cellSize;
	
	public GridGraph(int width, int height, int cellSize) {
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		cells = new Node[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Node(i, j, this);
			}
		}
	}
	
	@Override
	public Node[] getConnected(Node node) {
		int i = node.getI(), j = node.getJ();
		List<Node> adjacents = new ArrayList<Node>();
		int[] xOffsets = {1, 0, -1, 0}, yOffsets = {0, 1, 0, -1};
		for (int counter = 0; counter < xOffsets.length; counter++) {
			int x = xOffsets[counter], y = yOffsets[counter];
			if (i+x >= 0 && i+x <= cells.length && j+y >= 0 && j+y <= cells[0].length && cells[i][j].canVisit()) {
				adjacents.add(cells[i+x][j+y]);
			}
		}
		Node[] adjacentArray = new Node[adjacents.size()];
		return adjacents.toArray(adjacentArray);
	}
	
	@Override
	public int getNodeWidth() { return cellSize; }
	
	@Override
	public void setNodeWidth(int nodeWidth) { cellSize = nodeWidth; }
	
	@Override
	public Node getStart() { return start; }
	
	@Override
	public Node getEnd() { return end; }
	
	@Override
	public void setPath(Node start, Node end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Both start and end must be valid nodes");
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
	
	public Node get(int x, int y) { return cells[x][y];	}
	public int getWidth() { return cells.length; }
	public int getHeight() { return cells[0].length; }
}
