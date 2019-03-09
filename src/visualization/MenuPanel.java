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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pathfinding.Pathfinder;

/*
 * Container of the control menu.
 */
public class MenuPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 250, HEIGHT = 100;
	private static final String[] buttonNames = {"Start", "Stop", "Step"};
	
	private Point mousePos, myPos;
	private JButton[] buttons = new JButton[buttonNames.length];
	private GridPanel grid;
	private Pathfinder pf = null;
	
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
		
		for (int i = 0; i < buttonNames.length; i++) {
			JButton b = new JButton(buttonNames[i]);
			buttons[i] = b;
			b.addActionListener(this);
			b.setActionCommand(buttonNames[i]);
			add(b);
		}
		buttons[1].setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Start":
				//buttons[0].setEnabled(false);
				if (pf == null) {
					pf = new Pathfinder(grid.getCells(), () -> grid.repaint());
					pf.start();
				} else {
					pf.resume();
				}
				break;
			case "Stop":
				pf = null;
				buttons[0].setEnabled(true);
				break;
			case "Step":
				break;
			default:
				throw new IllegalArgumentException("Unknown action: " + e.getActionCommand());
		}
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
