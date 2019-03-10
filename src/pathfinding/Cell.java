package pathfinding;

import java.awt.Color;

/**
 * A grid cell treated as a Node in a Graph.
 */
public class Cell extends Node {
	public static final Color
		EMPTY_COL = Color.WHITE, WALL_COL = Color.BLACK,
		START_COL = Color.RED, END_COL = Color.BLUE,
		VISITED_COL = Color.GREEN;
	
	private int i, j;
	private Color col = EMPTY_COL;
	
	public Cell(int i, int j, GridGraph graph) {
		super(graph);
		this.i = i;
		this.j = j;
	}
	
	@Override
	public String toString() {
		return "Cell(" + i + ", " + j + ")";
	}
	
	@Override
	public int distanceTo(Node other) {
		Cell cell;
		try {
			cell = (Cell)other;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Cannot calculate distance to Node of a different type");
		}
		return (int)Math.sqrt(Math.pow(getX()-cell.getX(), 2) + Math.pow(getY()-cell.getY(), 2));
	}
	
	@Override
	public void clearPathData() {
		super.clearPathData();
		if (col == VISITED_COL) {
			col = EMPTY_COL;
		}
	}
	
	@Override
	public boolean canVisit() {
		return col != WALL_COL;
	}
	
	public int getX() {
		return i * getGraph().getNodeSize();
	}
	
	public int getY() {
		return j * getGraph().getNodeSize();
	}
	
	public int getI() { return i; }
	public int getJ() { return j; }
	public Color getColor() { return col; }
	
	public void setColor(Color col) { this.col = col; }
	
	@Override
	public void setVisited(boolean visited) {
		super.setVisited(visited);
		if (visited) {
			col = VISITED_COL;
		} else {
			col = EMPTY_COL;
		}
	}
}