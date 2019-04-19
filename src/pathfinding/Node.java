package pathfinding;

/**
 * A single node within a Graph.
 */
public abstract class Node implements Comparable<Node> {
	public enum State { DEFAULT, CONSIDERED, VISITING, ON_PATH }
	
	private Graph graph;
	private Node parent;
	//private int gCost /* (distance from start) */, hCost /* (distance from end) */;
	private int cost;
	private State state = State.DEFAULT;
	
	public Node(Graph graph) {
		this.graph = graph;
	}
	
	public abstract int distanceTo(Node other);
	
	public void clearPathData() {
		parent = null;
		cost = 0;
		state = State.DEFAULT;
	}
	
	public boolean canTravel() {
		return true;
	}
	
	@Override
	public int compareTo(Node other) {
	    return Integer.compare(cost, other.cost);
	}
	
	Graph getGraph() { return graph; }
	public Node getParent() { return parent; }
	public State getState() { return state; }
	public int getCost() { return cost; }
	
	public void setCost(int cost) { this.cost = cost; } 
	public void setState(State state) { this.state = state; }
	public void setParent(Node parent) {
		if (parent.getClass() != getClass()) {
			throw new IllegalArgumentException("Connected nodes must have the same type");
		}
		this.parent = parent;
	}
}
