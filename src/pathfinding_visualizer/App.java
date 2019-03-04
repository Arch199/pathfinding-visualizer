package pathfinding_visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * Main handler for rendering, event listening, etc.
 */
public class App implements MouseWheelListener {
	private static final int WINDOW_W = 600, WINDOW_H = 600, MIN_CELL_SIZE = 10, MAX_CELL_SIZE = 40;
	private static int cellSize = 20;
	
	private GridPanel grid;
	
	public static void main(String[] args) {
		new App();
	}
	
	public App() {
		JFrame frame = new JFrame("Pathfinding Visualizer");
		frame.setPreferredSize(new Dimension(WINDOW_W, WINDOW_H));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.addMouseWheelListener(this);
		
		grid = new GridPanel();
		frame.add(grid);
		int menuX = (int)(cellSize * 1.5), menuY = WINDOW_H - MenuPanel.HEIGHT - (int)(cellSize * 2.5);
		frame.getLayeredPane().add(new MenuPanel(menuX, menuY, this), JLayeredPane.PALETTE_LAYER);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		cellSize -= rotation;
		if (cellSize > MAX_CELL_SIZE) {
			cellSize = MAX_CELL_SIZE;
		} else if (cellSize < MIN_CELL_SIZE) {
			cellSize = MIN_CELL_SIZE;
		}
		grid.repaint();
	}
	
	protected Node[][] getCells() { return grid.cells; }
	protected Node getStartCell() { return grid.start; }
	protected Node getEndCell() { return grid.end; }
	
	public class GridPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		
		private Node[][] cells = new Node[WINDOW_W/MIN_CELL_SIZE][WINDOW_H/MIN_CELL_SIZE];
		private Node start, end;
		private char currentKey = 0;
		
		public GridPanel() {
			super();
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[i].length; j++) {
					cells[i][j] = new Node(i, j);
				}
			}
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
			// Hook into keyboard management to make key detection work without focus
			// Probably could use key bindings for this but I can't really be bothered
			// Code from here: https://stackoverflow.com/questions/5344823/
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
				@Override
			    public boolean dispatchKeyEvent(KeyEvent e) {
					if (e.getID() == KeyEvent.KEY_PRESSED) {
						currentKey = e.getKeyChar();
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
				if (start == cells[i][j]) {
					start = null;
				} else if (end == cells[i][j]) {
					end = null;
				}
				if (currentKey == 's') {
					if (start != null) {
						start.setColor(Node.EMPTY_COL);
					}
					start = cells[i][j];
					start.setColor(Node.START_COL);
				} else if (currentKey == 'e') {
					if (end != null) {
						end.setColor(Node.EMPTY_COL);
					}
					end = cells[i][j];
					end.setColor(Node.END_COL);
				} else {
					cells[i][j].setColor(Node.WALL_COL);
				}
			} else {
				cells[i][j].setColor(Node.EMPTY_COL);
			}
			repaint();
		}
		
		// TODO: remove this or repurpose it with a "clear" button
		private void purgeColor(Color col) {
			for (var column : cells) {
				for (var cell : column) {
					if (cell.getColor() == col) {
						cell.setColor(Node.EMPTY_COL);
						if (col == Node.START_COL || col == Node.END_COL) {
							return;
						}
					}
				}
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int gridWidth = WINDOW_W/cellSize, gridHeight = WINDOW_H/cellSize;
			for (int i = 0; i < gridWidth; i++) {
				for (int j = 0; j < gridHeight; j++) {
					int x = cells[i][j].getI()*cellSize, y = cells[i][j].getJ()*cellSize;
					g.setColor(cells[i][j].getColor());
					g.fillRect(x, y, cellSize, cellSize);
					g.setColor(Color.BLACK);
					g.drawRect(x, y, cellSize, cellSize);
				}
			}
		}
		
		@Override
        public Dimension getPreferredSize() {
            return new Dimension(WINDOW_W, WINDOW_H);
		}
	}
}
