package visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/*
 * Main handler for rendering, event listening, etc.
 */
public class App implements MouseWheelListener {
	public static final int
		WINDOW_W = 600, WINDOW_H = 600,
		MIN_CELL_SIZE = 10, MAX_CELL_SIZE = 40, DEFAULT_CELL_SIZE = 20;
	
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
		
		grid = new GridPanel(DEFAULT_CELL_SIZE);
		frame.add(grid);
		int menuX = (int)(DEFAULT_CELL_SIZE * 1.5);
		int menuY = WINDOW_H - MenuPanel.HEIGHT - (int)(DEFAULT_CELL_SIZE * 2.5);
		frame.getLayeredPane().add(new MenuPanel(menuX, menuY, grid), JLayeredPane.PALETTE_LAYER);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int rotation = e.getWheelRotation();
		int cellSize = grid.getCellSize();
		cellSize -= rotation;
		if (cellSize > MAX_CELL_SIZE) {
			cellSize = MAX_CELL_SIZE;
		} else if (cellSize < MIN_CELL_SIZE) {
			cellSize = MIN_CELL_SIZE;
		}
		grid.setCellSize(cellSize);
		grid.repaint();
	}
}
