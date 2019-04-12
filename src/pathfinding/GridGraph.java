package pathfinding;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A grid of Cells viewed as a Graph.
 */
public class GridGraph implements Graph {	
	private Cell[][] cells;
	private Cell start, end;
	private int cellSize;
	
	public GridGraph(int width, int height, int cellSize) {
		this.cellSize = cellSize;
		cells = new Cell[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				cells[i][j] = new Cell(i, j, this);
			}
		}
	}
	
	@Override
	public List<Node> getConnected(Node node) {
	    Cell cell = (Cell)node;
		int i = cell.getI(), j = cell.getJ();
		List<Cell> adjacents = new ArrayList<Cell>();
		int[] xOffsets = {1, 0, -1, 0}, yOffsets = {0, 1, 0, -1};
		for (int counter = 0; counter < xOffsets.length; counter++) {
			int x = xOffsets[counter], y = yOffsets[counter];
			if (i+x >= 0 && i+x < cells.length && j+y >= 0 && j+y < cells[0].length && cells[i+x][j+y].canTravel()) {
				adjacents.add(cells[i+x][j+y]);
			}
		}
		return Collections.unmodifiableList(adjacents);
	}
	
	@Override
	public void setPath(Node start, Node end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Both start and end must be valid cells");
		}
		this.start = (Cell)start;
		this.end = (Cell)end;
	}
	
	@Override
	public void clearPathData() {
		start = end = null;
		for (Node node : this) {
		    node.clearPathData();
		}
	}
	
	@Override
    public Iterator<Node> iterator() {
        return Arrays.stream(cells)
               .flatMap(column -> Arrays.stream(column).map(cell -> (Node)cell))
               .iterator();
    }
	
	@Override public int getNodeSize() { return cellSize; }
	@Override public void setNodeSize(int nodeSize) { cellSize = nodeSize; }
	@Override public Cell getStart() { return start; }
	@Override public Cell getEnd() { return end; }
	
	public Cell get(int x, int y) { return cells[x][y];	}
	public int getWidth() { return cells.length; }
	public int getHeight() { return cells[0].length; }
	
	/**
	 * A grid cell treated as a Node in a Graph.
	 */
	public static class Cell extends Node {
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
	        return (int)Math.hypot(getX()-cell.getX(), getY()-cell.getY());
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
	        if (col != START_COL && col != END_COL) {
    	        switch (state) {
    	            case ON_PATH:col = PATH_COL; break;
    	            case CONSIDERED: col = CONSIDERED_COL; break;
    	            case VISITING: col = VISITING_COL; break;
    	            default: col = EMPTY_COL; break;
    	        }
	        }
	    }
	}
}
