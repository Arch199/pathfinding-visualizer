package pathfinding_visualizer;

import java.awt.Color;

/*
 * Node for A* algorithm to pathfind through.
 */
public class Node {
	public static final Color
			EMPTY_COL = Color.WHITE, WALL_COL = Color.BLACK,
			START_COL = Color.RED, END_COL = Color.BLUE;
	
	private int i, j, g, h;
	private Color col = EMPTY_COL;
	
	public Node(int i, int j) {
		this.i = i;
		this.j = j;
	}
	
	public int getI() { return i; }
	public int getJ() { return j; }
	public int getG() { return g; }
	public int getH() { return h; }
	public Color getColor() { return col; }
	
	public void setColor(Color col) { this.col = col; }  
}
