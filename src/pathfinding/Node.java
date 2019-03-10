package pathfinding;

/**
 * A single node within a Graph.
 */
public abstract class Node {
	private Graph<? extends Node> graph;
	
	private Node parent;
	private int gCost /* (distance from start) */, hCost /* (distance from end) */;
	private boolean visited = false;
	
	public Node(Graph<? extends Node> graph) {
		this.graph = graph;
	}
	
	public void clearPathData() {
		parent = null;
		gCost = hCost = 0;
	}
	
	public abstract int distanceTo(Node other);/* {
		throw new UnsupportedOperationException("Can't calculate distance for generic Node");
	}*/
	
	// TODO: maybe make this "canTravel()" and also add a "considered" field
	public boolean canVisit() {
		return true;
	}
	
	Graph<? extends Node> getGraph() { return graph; }
	public Node getParent() { return parent; }
	public boolean getVisited() { return visited; }
	public int getGCost() {
		if (gCost == 0) {
			if (parent == null) {
				return 0;
			}
			gCost = distanceTo(parent) + parent.getGCost();
		}
		return gCost;
	}
	public int getHCost() {
		if (hCost == 0) {
			if (this == graph.getEnd()) {
				return 0;
			}
			hCost = distanceTo(graph.getEnd());
		}
		return hCost;
	}
	public int getFCost() {
		return getGCost() + getHCost();
	}
	
	public void setGCost(int gCost) { this.gCost = gCost; }
	public void setHCost(int hCost) { this.hCost = hCost; }
	public void setVisited(boolean visited) { this.visited = visited; }
	public void setParent(Node parent) {
		if (parent.getClass() != getClass()) {
			throw new IllegalArgumentException("Connected nodes must have the same type");
		}
		this.parent = parent;
	}
}
