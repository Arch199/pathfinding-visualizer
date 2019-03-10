package pathfinding;

import pathfinding.Node;

/**
 * Executor of the pathfinding algorithm (A*).
 */
public class Pathfinder<T extends Node> {
	public static final int MIN_DELAY = 0, MAX_DELAY = 2000;
	private enum State { STOPPED, RUNNING, PAUSED };
	
	private Thread thread;
	private Graph<T> graph;
	private T parent;
	private Runnable onUpdate;
	private volatile State state = State.STOPPED;
	/** Delay between steps in milliseconds. */
	private volatile int delay;
	
	public Pathfinder(Graph<T> graph, int delay, Runnable onUpdate) {
		if (graph.getStart() == null || graph.getEnd() == null) {
			throw new IllegalArgumentException("Graph must have start and end nodes");
		}
		if (delay < MIN_DELAY || delay > MAX_DELAY) {
			throw new IllegalArgumentException("Delay must be between " + MIN_DELAY + " and " + MAX_DELAY);
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
					try {
						if (state == State.PAUSED) {
							wait();
						} else {
							pathfindStep();
							wait(delay);
						}
					} catch (InterruptedException e) {
						continue;
					}
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
	public void pathfindStep() {
		T bestNode = null;
		int minCost = -1;
		for (T current : graph.getConnected(parent)) {
			System.out.println("Looking at " + current);
			if (current.getVisited()) {
				System.out.println("Skipping!");
				continue;
				// TODO: update this to include "considered" cells that weren't added to the path
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
				onUpdate.run();
				return;
			}
		}
		
		// TODO: add an else here? something like that, that return seems dodge
		
		// All good, move to the next node
		System.out.println("Got " + bestNode);
		if (bestNode == graph.getEnd()) {
			// Done! :)
			System.out.println("Time to go to the pub");
			state = State.STOPPED;
		} else {
			parent = bestNode;
			bestNode.setVisited(true);
		}
		onUpdate.run();
	}
}
