package pathfinding;

import pathfinding.Node;

/**
 * Executor of the pathfinding algorithm (A*).
 */
public class Pathfinder<T extends Node> {
	private Thread thread;
	private Graph<T> graph;
	private T parent;
	private Runnable onUpdate;
	private enum State { STOPPED, RUNNING, PAUSED };
	private volatile State state = State.STOPPED;
	/** Delay between steps in seconds. */
	private volatile int delay;
	
	public Pathfinder(Graph<T> graph, int delay, Runnable onUpdate) {
		if (graph.getStart() == null || graph.getEnd() == null) {
			throw new IllegalArgumentException("Graph must have start and end nodes");
		}
		if (delay < 0) {
			throw new IllegalArgumentException("Delay must be a non-negative number of seconds");
		}
		this.graph = graph;
		this.delay = delay;
		this.onUpdate = onUpdate;
		parent = graph.getStart();		
		System.out.println("Starting at " + parent + ", going to " + graph.getEnd());
	}
	
	public void start() {
		if (state == State.STOPPED) {
			assert thread == null;
			Runnable run = () -> {
				while (state != State.STOPPED) {
					if (state == State.PAUSED) {
						try {
							wait();
						} catch (InterruptedException e) {
							continue;
						}
					}
					pathfindStep();
					onUpdate.run();
				}
			};
			thread = new Thread(run, "Pathfinder");
			thread.start();
			state = State.RUNNING;
		} else {
			throw new IllegalStateException("Can't start; pathfinder is already running");
		}
	}
	
	public void pause() {
		if (state == State.RUNNING) {
			state = State.PAUSED;
		} else {
			throw new IllegalStateException("Can't pause; pathfinder is not running");
		}
	}
	
	public void resume() {
		if (state == State.PAUSED) {
			state = State.RUNNING;
			notify();
		} else {
			throw new IllegalStateException("Can't resume; pathfinder is not paused");
		}
	}
	
	public void stop() {
		if (state != State.STOPPED) {
			state = State.STOPPED;
			if (state == State.PAUSED) {
				notify();
			}
		} else {
			throw new IllegalStateException("Can't stop; pathfinder is already stopped");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void pathfindStep() {
		T bestNode = null;
		int minCost = -1;
		for (T current : graph.getConnected(parent)) {
			System.out.println("Looking at " + current);
			if (current.getVisited()) {
				System.out.println("Skipping!");
				continue;
			}
			int cost = current.getFCost();
			if (minCost == -1 || cost < minCost) {
				minCost = cost;
				bestNode = current;
			}
		}
		
		if (bestNode == null) {
			// Reached a dead end: try to backtrack
			parent = (T)parent.getParent();
			// TODO: need a way to avoid going down the same node again
			if (parent == null) {
				// Pathfinding failed
				System.out.println("We failed!");
				state = State.STOPPED;
				return;
			}
		}
		
		// All good, move to the next node
		System.out.println("Got " + bestNode);
		if (bestNode == graph.getEnd()) {
			// Done! :)
			// TODO: add coloring stuff
			System.out.println("Time to go to the pub");
			state = State.STOPPED;
			return;
		}
		parent = bestNode;
		bestNode.setVisited(true);
	}
}
