package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pathfinding.GridGraph;
import pathfinding.Node;

public class GridPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private GridGraph cells;
	// TODO: replace with graph
	private Node start, end;
	private int currentKey, cellSize;
	
	public GridPanel(int cellSize) {
		super();
		this.cellSize = cellSize;
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
		int i = e.getX()/cellSize, j = e.getY()/cellSize;
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (start == cells.get(i, j)) {
				start = null;
			} else if (end == cells.get(i, j)) {
				end = null;
			}
			if (currentKey == KeyEvent.VK_S || currentKey == KeyEvent.VK_CONTROL) {
				if (start != null) {
					start.setColor(Node.EMPTY_COL);
				}
				start = cells.get(i, j);
				start.setColor(Node.START_COL);
			} else if (currentKey == KeyEvent.VK_E || currentKey == KeyEvent.VK_SHIFT) {
				if (end != null) {
					end.setColor(Node.EMPTY_COL);
				}
				end = cells.get(i, j);
				end.setColor(Node.END_COL);
			} else {
				cells.get(i, j).setColor(Node.WALL_COL);
			}
		} else {
			cells.get(i, j).setColor(Node.EMPTY_COL);
		}
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int gridWidth = App.WINDOW_W/cellSize, gridHeight = App.WINDOW_H/cellSize;
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				int x = cells.get(i, j).getI()*cellSize, y = cells.get(i, j).getJ()*cellSize;
				g.setColor(cells.get(i, j).getColor());
				g.fillRect(x, y, cellSize, cellSize);
				g.setColor(Color.BLACK);
				g.drawRect(x, y, cellSize, cellSize);
			}
		}
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(App.WINDOW_W, App.WINDOW_H);
	}
	
	public int getCellSize() { return cellSize; }
	GridGraph getCells() { return cells; }
	
	public void setCellSize(int cellSize) { this.cellSize = cellSize; } 
}