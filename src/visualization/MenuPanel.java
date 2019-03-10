package visualization;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pathfinding.Cell;
import pathfinding.Pathfinder;

/*
 * Container of the control menu.
 */
public class MenuPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 300, HEIGHT = 100;
	private enum Function {
		START, STOP, STEP, CLEAR;
		
		@Override
		public String toString() {
			return name().substring(0,1).toUpperCase() + name().substring(1).toLowerCase();
		}
	}
	
	private Point mousePos, myPos;
	private JButton[] buttons = new JButton[Function.values().length];
	private GridPanel grid;
	private Pathfinder<Cell> pf = null;
	
	public MenuPanel(int x, int y, GridPanel grid) {
		super();
		this.grid = grid;
		setBounds(x, y, WIDTH, HEIGHT);
		setBorder(BorderFactory.createEtchedBorder());
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					myPos = getLocation();
					mousePos = e.getLocationOnScreen();
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					int deltaX = e.getXOnScreen() - (int)mousePos.getX();
					int deltaY = e.getYOnScreen() - (int)mousePos.getY();
					setLocation((int)myPos.getX() + deltaX, (int)myPos.getY() + deltaY);
				}
			}
		});
		
		for (int i = 0; i < Function.values().length; i++) {
			JButton b = new JButton(Function.values()[i].toString());
			buttons[i] = b;
			b.addActionListener(this);
			b.setActionCommand(Function.values()[i].name());
			add(b);
		}
		buttons[1].setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (Function.valueOf(e.getActionCommand())) {
			case START:
				//buttons[0].setEnabled(false);
				if (grid.hasPath()) {
					if (pf == null) {
						pf = new Pathfinder<Cell>(grid.getCells(), 0, grid::repaint);
						pf.start();
					} else {
						pf.resume();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please create start/end cells to find a path between them.");
				}
				break;
			case STOP:
				pf = null;
				buttons[0].setEnabled(true);
				break;
			case STEP:
				// TODO
				break;
			case CLEAR:
				grid.clearCells();
				pf = null;
				break;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}
