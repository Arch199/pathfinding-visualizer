package visualization;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
	private GridPanel grid;
	private Pathfinder<Cell> pf = null;
	private Map<Function,JButton> buttons = new HashMap<Function,JButton>(Function.values().length);
	private JSlider delaySlider = new JSlider(Pathfinder.MIN_DELAY, Pathfinder.MAX_DELAY, 100);
	
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
		
		// Create button controls
		for (int i = 0; i < Function.values().length; i++) {
			Function f = Function.values()[i];
			JButton b = new JButton(f.toString());
			buttons.put(f, b);
			b.addActionListener(this);
			b.setActionCommand(Function.values()[i].name());
			add(b);
		}
		buttons.get(Function.STOP).setEnabled(false);
		
		// Format delay slider
		JLabel delayLabel = new JLabel("Delay (ms)", JLabel.CENTER);
		add(delayLabel);
		delaySlider.setMajorTickSpacing(500);
		delaySlider.setMinorTickSpacing(100);
		delaySlider.setPaintTicks(true);
		delaySlider.setPaintLabels(true);
		add(delaySlider);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (Function.valueOf(e.getActionCommand())) {
			case START:
				buttons.get(Function.START).setEnabled(false);
				if (pf == null) {
					startPathfinder();
				} else {
					pf.resume();
				}
				break;
			case STOP:
				buttons.get(Function.START).setEnabled(true);
				pf.stop();
				pf = null;
				break;
			case STEP:
				if (pf == null) {
					startPathfinder();
				} else {
					pf.pause();
					pf.pathfindStep();
				}
				break;
			case CLEAR:
				grid.clearCells();
				pf = null;
				break;
		}
	}
	
	private void startPathfinder() {
		if (grid.hasPath()) {
			pf = new Pathfinder<Cell>(grid.getCells(), delaySlider.getValue(), grid::repaint);
			pf.start();
		} else {
			JOptionPane.showMessageDialog(null, "Please create start/end cells to find a path between them.");
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}
