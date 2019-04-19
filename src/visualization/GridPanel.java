package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pathfinding.GridGraph;
import pathfinding.GridGraph.Cell;
import pathfinding.Node;

public class GridPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private GridGraph cells;
	private Cell start, end;
	private int currentKey;
	
	public GridPanel(int cellSize) {
		super();
		cells = new GridGraph(App.WINDOW_W/App.MIN_CELL_SIZE, App.WINDOW_H/App.MIN_CELL_SIZE, cellSize);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				updateCells(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				updateCells(e);
			}
		});
		
		/*
		 * Hook into keyboard management to make key detection work without focus
		 * Probably could use key bindings for this but I can't really be bothered
		 * Code from here: https://stackoverflow.com/questions/5344823/
		 */
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					currentKey = e.getKeyCode();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					currentKey = 0;
				}
				return false;
			}
		});
	}
	
	private void updateCells(MouseEvent e) {
		int i = e.getX()/cells.getNodeSize(), j = e.getY()/cells.getNodeSize();
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (start == cells.get(i, j)) {
				start = null;
			} else if (end == cells.get(i, j)) {
				end = null;
			}
			if (currentKey == KeyEvent.VK_S || currentKey == KeyEvent.VK_CONTROL) {
				if (start != null) {
					start.setColor(Cell.EMPTY_COL);
				}
				start = cells.get(i, j);
				start.setColor(Cell.START_COL);
			} else if (currentKey == KeyEvent.VK_E || currentKey == KeyEvent.VK_SHIFT) {
				if (end != null) {
					end.setColor(Cell.EMPTY_COL);
				}
				end = cells.get(i, j);
				end.setColor(Cell.END_COL);
			} else {
				cells.get(i, j).setColor(Cell.WALL_COL);
			}
		} else {
			cells.get(i, j).setColor(Cell.EMPTY_COL);
		}
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int cellSize = cells.getNodeSize();
		for (Node node : cells) {
		    Cell cell = (Cell)node;
			int x = cell.getX(), y = cell.getY();
			g.setColor(cell.getColor());
			g.fillRect(x, y, cellSize, cellSize);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, cellSize, cellSize);
			
			// Draw node costs
			int size = 4 * cellSize / App.MIN_CELL_SIZE;
			if (size > 10) {
			    g.setFont(new Font("Arial", Font.PLAIN, size));
			    String costStr;
			    if (cell.getCost() == Integer.MAX_VALUE) {
			        costStr = "\u221e"; // infinity symbol
			    } else {
			        costStr = Integer.toString(cell.getCost());
			    }
	            g.drawString(costStr, x + cellSize / 8, y + cellSize / 2);
			}
		}
	}
	
	public boolean hasPath() {
		return start != null && end != null;
	}
	
	public void clearPath() {
	    clearCells(c -> c != Cell.WALL_COL && c != Cell.START_COL && c != Cell.END_COL);
	    cells.clearPathData();
	}
	
	public void clearCells() {
        clearCells(null);
        cells.clearPathData();
    }
	
	private void clearCells(Function<Color,Boolean> condition) {
		for (Node node : cells) {
		    Cell cell = (Cell)node;
			if (condition == null || condition.apply(cell.getColor())) {
				cell.setColor(Cell.EMPTY_COL);
			}
		}
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(App.WINDOW_W, App.WINDOW_H);
	}
	
	public int getCellSize() { return cells.getNodeSize(); }
	GridGraph getCells() {
		cells.setPath(start, end);
		return cells;
	}
	
	public void setCellSize(int cellSize) { cells.setNodeSize(cellSize); } 
}