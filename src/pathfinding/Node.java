package pathfinding;

import java.awt.Color;

import pathfinding.Graph;

// Should probably be Cell and extend a Node class
/*
 * Node for A* algorithm to pathfind through.
 */
public class Node {
	public static final Color
		EMPTY_COL = Color.WHITE, WALL_COL = Color.BLACK,
		START_COL = Color.RED, END_COL = Color.BLUE,
		VISITED_COL = Color.YELLOW;
	
	private int i, j, gCost /* (distance from start) */, hCost /* (distance from end) */;
	private Graph graph;
	private Node parent;
	private Color col = EMPTY_COL;
	
	public Node(int i, int j, Graph graph) {
		this.i = i;
		this.j = j;
		this.graph = graph;
	}
	
	@Override
	public String toString() {
		return "Node(" + i + ", " + j + ")";
	}
	
	public int distanceTo(Node other) {
		return (int)Math.sqrt(Math.pow(getX()-other.getX(), 2) + Math.pow(getY()-other.getY(), 2));
	}
	
	public void clearPathData() {
		parent = null;
		gCost = hCost = 0;
		if (col == VISITED_COL) {
			col = EMPTY_COL;
		}
	}
	
	public boolean canVisit() {
		return col != WALL_COL;
	}
	
	public boolean wasVisited() {
		return col == VISITED_COL;
	}
	
	public void visit() {
		col = VISITED_COL;
	}
	
	public int getX() {
		return i * graph.getNodeWidth();
	}
	
	public int getY() {
		return j * graph.getNodeWidth();
	}
	
	public int getI() { return i; }
	public int getJ() { return j; }
	public Node getParent() { return parent; }
	public Color getColor() { return col; }
	public int getGCost() {
		if (gCost == 0) {
			if (parent == null) {
				return 0;
			}
			gCost = distanceTo(parent) + parent.getGCost();
		}
		return gCost;
	}
	public int getHCost() {
		if (hCost == 0) {
			if (this == graph.getEnd()) {
				return 0;
			}
			hCost = distanceTo(graph.getEnd());
		}
		return hCost;
	}
	public int getFCost() {
		return getGCost() + getHCost();
	}
	
	public void setGCost(int gCost) { this.gCost = gCost; }
	public void setHCost(int hCost) { this.hCost = hCost; }
	public void setParent(Node parent) { this.parent = parent; }
	public void setColor(Color col) { this.col = col; }
}
