package pathfinding;

import pathfinding.Node;

public interface Graph {
	public Node[] getConnected(Node node);
	public int getNodeWidth();
	public void setNodeWidth(int nodeWidth);
	public Node getStart();
	public Node getEnd();
	public void setPath(Node start, Node end);
	public void clearPathData();
}
