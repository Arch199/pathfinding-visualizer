package pathfinding;

import java.awt.Color;

/**
 * A grid cell treated as a Node in a Graph.
 */
public class Cell extends Node {
	public static final Color
		EMPTY_COL = Color.WHITE, WALL_COL = Color.BLACK,
		START_COL = Color.RED, END_COL = Color.BLUE,
		CONSIDERED_COL = Color.YELLOW, PATH_COL = Color.GREEN, VISITING_COL = Color.ORANGE;
	
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
		if (col == PATH_COL || col == CONSIDERED_COL) {
			col = EMPTY_COL;
		}
	}
	
	@Override
	public boolean canTravel() {
		return col != WALL_COL && col != START_COL;
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
	public void setState(State state) {
		super.setState(state);
		switch (state) {
			case ON_PATH: col = PATH_COL; break;
			case CONSIDERED: col = CONSIDERED_COL; break;
			case VISITING: col = VISITING_COL; break;
			default: col = EMPTY_COL; break;
		}
	}
}