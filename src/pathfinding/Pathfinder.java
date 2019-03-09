package pathfinding;

import pathfinding.Node;

/*
 * Executor of the pathfinding algorithm (A*).
 */
public class Pathfinder {
	private Graph graph;
	private Node parent;
	private boolean running = false;
	Runnable onUpdate;
	// TODO: add speed field
	
	public Pathfinder(Graph graph, Runnable onUpdate) {
		this.graph = graph;
		this.onUpdate = onUpdate;
		parent = graph.getStart();		
		System.out.println("Starting at " + parent + ", going to " + graph.getEnd());
	}
	
	public void start() {
		running = true;
		while (running && !pathfindStep()) {
			onUpdate.run();
		}
	}
	
	public void pause() {
		running = false;
	}
	
	public void resume() {
		// TODO
	}
	
	private boolean pathfindStep() {
		Node bestNode = null;
		int minCost = -1;
		for (Node current : graph.getConnected(parent)) {
			System.out.println("Looking at " + current);
			if (current.wasVisited()) {
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
			parent = parent.getParent();
			// TODO: need a way to avoid going down the same node again
			if (parent == null) {
				// Pathfinding failed
				running = false;
				System.out.println("We failed!");
				return true;
				// TODO: add better handling
			}
		}
		
		// All good, move to the next node
		System.out.println("Got " + bestNode);
		if (bestNode == graph.getEnd()) {
			// Done! :)
			// TODO: add coloring stuff
			System.out.println("Time to go to the pub");
			return true;
		}
		parent = bestNode;
		bestNode.visit();
		return false;
	}
	
	public boolean isRunning() { return running; }
}
