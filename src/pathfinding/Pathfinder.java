package pathfinding;

import java.util.List;

import pathfinding.Node;

/**
 * Executor of the pathfinding algorithm (A*).
 * Parametrized with the specific Node type used by the given Graph.
 */
public class Pathfinder<T extends Node> {
	public static final int MIN_DELAY = 0, MAX_DELAY = 2000;
	private enum State { STOPPED, RUNNING, PAUSED };
	
	private Thread thread;
	private Graph<T> graph;
	private T parent;
	private Runnable onUpdate, onFinish;
	private volatile State state = State.STOPPED;
	/** Delay between steps in milliseconds. */
	private volatile int delay = 0;
	
	public Pathfinder(Graph<T> graph, Runnable onUpdate, Runnable onFinish) {
		if (graph.getStart() == null || graph.getEnd() == null) {
			throw new IllegalArgumentException("Graph must have start and end nodes");
		} else if (onUpdate == null || onFinish == null) {
			throw new IllegalArgumentException("Neither onUpdate and onFinish can be null");
		}
		this.graph = graph;
		this.onUpdate = onUpdate;
		this.onFinish = onFinish;
		parent = graph.getStart();		
		System.out.println("Starting at " + parent + ", going to " + graph.getEnd());
	}
	
	public void start() {
		if (state != State.STOPPED) {
			throw new IllegalStateException("Can't start; pathfinder is already running");
		}
		assert thread == null;
		Runnable run = () -> {
			while (state != State.STOPPED) {
				try {
					synchronized (this) {
						if (state == State.PAUSED) {
							wait();
						} else {
							pathfindStep();
							if (delay > 0) {
								wait(delay);
							}
						}
					}
				} catch (InterruptedException e) {
					continue;
				}
			}
		};
		thread = new Thread(run, "Pathfinder");
		thread.start();
		state = State.RUNNING;
	}
	
	public void pause() {
		if (state != State.RUNNING) {
			throw new IllegalStateException("Can't pause; pathfinder is not running");	
		}
		state = State.PAUSED;
	}
	
	public void resume() {
		if (state != State.PAUSED) {
			throw new IllegalStateException("Can't resume; pathfinder is not paused");
		}
		state = State.RUNNING;
		synchronized (this) {
			notify();
		}
	}
	
	public void stop() {
		if (state == State.STOPPED) {
			throw new IllegalStateException("Can't stop; pathfinder is already stopped");
		} else if (state == State.PAUSED) {
			synchronized (this) {
				notify();
			}
		}
		state = State.STOPPED;
		onFinish.run();
	}
	
	@SuppressWarnings("unchecked")
	public void pathfindStep() {
		T bestNode = null;
		int minCost = -1;
		List<T> connected = graph.getConnected(parent);
		for (T current : connected) {
			System.out.println("Looking at " + current);
			if (current.getState() != Node.State.DEFAULT) {
				System.out.println("Skipping!");
				continue;
			}
			if (current != graph.getStart() && current != graph.getEnd()) {
				current.setState(Node.State.CONSIDERED);
			}
			int cost = current.getFCost();
			if (minCost == -1 || cost < minCost) {
				minCost = cost;
				bestNode = current;
			}
		}
		
		if (bestNode == null) {
			// Reached a dead end: try to backtrack
			// The cast here is unchecked but the parent is guaranteed to have the same type as the current node,
			// so it's fine.
			parent.setState(Node.State.CONSIDERED);
			for (T current : connected) {
				if (current != graph.getStart() && current != graph.getEnd()) {
					current.setState(Node.State.DEFAULT);
				}
			}
			parent = (T)parent.getParent();
			System.out.println("Backtracking to " + parent);
			// TODO: need a way to avoid going down the same node again
			if (parent == null) {
				// Pathfinding backtracked to the beginning, and therefore failed
				System.out.println("We failed!");
				stop();
			}
		} else {
			// All good, move to the next node
			bestNode.setParent(parent);
			System.out.println("Got " + bestNode);
			if (bestNode == graph.getEnd()) {
				// Done! :)
				System.out.println("Time to go to the pub");
				while (parent != graph.getStart()) {
					parent.setState(Node.State.ON_PATH);
					parent = (T)parent.getParent();
				}
				stop();
			} else {
				parent = bestNode;
				bestNode.setState(Node.State.VISITING);
			}
		}
		onUpdate.run();
	}
	
	public boolean isRunning() { return state == State.RUNNING; }
	public boolean isPaused() { return state == State.PAUSED; }
	public boolean isStopped() { return state == State.STOPPED; }
	
	public void setDelay(int delay) {
		if (delay < MIN_DELAY || delay > MAX_DELAY) {
			throw new IllegalArgumentException("Delay must be between " + MIN_DELAY + " and " + MAX_DELAY);
		}
		this.delay = delay;
	}
}
