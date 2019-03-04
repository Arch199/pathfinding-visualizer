package pathfinding_visualizer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

/*
 * Container of the control menu.
 */
public class MenuPanel extends JPanel implements ActionListener, MouseInputListener {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 250, HEIGHT = 100;
	
	private Point mousePos, myPos;
	private JButton[] buttons = new JButton[3];
	private App app;
	private Pathfinder pf = null;
	
	public MenuPanel(int x, int y, App app) {
		super();
		this.app = app;
		setBounds(x, y, WIDTH, HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setBorder(BorderFactory.createEtchedBorder());
		
		String[] buttonNames = {"Start", "Stop", "Step"};
		for (int i = 0; i < buttonNames.length; i++) {
			JButton b = new JButton(buttonNames[i]);
			buttons[i] = b;
			b.setActionCommand(buttonNames[i]);
			add(b);
		}
		buttons[1].setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Start":
				buttons[0].setEnabled(false);
				if (pf == null) {
					pf = new Pathfinder(app.getCells(), app.getStartCell(), app.getEndCell());
					pf.start();
				} else {
					pf.resume();
				}
				break;
			case "Stop":
				break;
			case "Step":
				break;
		}
	}
	
	
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			myPos = getLocation();
			mousePos = e.getLocationOnScreen();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			int deltaX = e.getXOnScreen() - (int)mousePos.getX();
			int deltaY = e.getYOnScreen() - (int)mousePos.getY();
			setLocation((int)myPos.getX() + deltaX, (int)myPos.getY() + deltaY);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
