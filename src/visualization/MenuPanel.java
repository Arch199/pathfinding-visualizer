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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pathfinding.Pathfinder;
import pathfinding.Pathfinder.Algorithm;;

/*
 * Container of the control menu.
 */
public class MenuPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 400, HEIGHT = 100;
	private enum Function {
		START, STOP, STEP, CLEAR;
		
		@Override
		public String toString() {
			return name().substring(0,1).toUpperCase() + name().substring(1).toLowerCase();
		}
	}
	
	private Point mousePos, myPos;
	private GridPanel grid;
	private Pathfinder pf = null;
	private Map<Function,JButton> buttons = new HashMap<Function,JButton>(Function.values().length);
	private JSlider delaySlider = new JSlider(Pathfinder.MIN_DELAY, Pathfinder.MAX_DELAY, 100);
	private JComboBox<Algorithm> algoChooser = new JComboBox<Algorithm>(Algorithm.values());
	
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
		
		// Add algorithm dropdown box
        add(algoChooser);
		
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
		delaySlider.setMajorTickSpacing(250);
		delaySlider.setMinorTickSpacing(50);
		delaySlider.setPaintTicks(true);
		delaySlider.setPaintLabels(true);
		delaySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (pf != null && delaySlider != null) {
					pf.setDelay(delaySlider.getValue());
				}
			}
		});
		add(delaySlider);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (Function.valueOf(e.getActionCommand())) {
			case START:
				buttons.get(Function.STOP).setEnabled(true);
				if (pf == null) {
					if (tryStartPathfinder()) {
						buttons.get(Function.START).setEnabled(false);
					}
				} else {
					pf.resume();
				}
				break;
			case STOP:
				buttons.get(Function.START).setEnabled(true);
				if (pf == null || pf.isStopped()) {
					// Clear away the previous Pathfinder's path
					grid.clearPath();
				} else {
					// Stop the existing Pathfinder for good
					pf.stop();
					pf = null;
				}
				break;
			case STEP:
			    buttons.get(Function.START).setEnabled(true);
			    buttons.get(Function.STOP).setEnabled(true);
				if (pf == null) {
					tryStartPathfinder();
				}
				if (pf.isRunning()) {
					pf.pause();
				}
				if (!pf.isStopped()) {
					pf.step();
				}
				break;
			case CLEAR:
				buttons.get(Function.START).setEnabled(true);
				grid.clearCells();
				pf = null;
				break;
		}
	}
	
	/**
	 * Attempts to start the pathfinder.
	 * @return A boolean representing whether the attempt succeeded or failed.
	 */
	private boolean tryStartPathfinder() {
		if (grid.hasPath()) {
			pf = Pathfinder.create((Algorithm)algoChooser.getSelectedItem(), grid.getCells(), grid::repaint, () -> {
				pf = null;
				buttons.get(Function.START).setEnabled(true);
			});
			pf.setDelay(delaySlider.getValue());
			pf.start();
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Please create start/end cells to find a path between them.");
			return false;
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}
}
