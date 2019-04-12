package pathfinding;

import java.util.List;

import pathfinding.Node;

public interface Graph extends Iterable<Node> {
	List<Node> getConnected(Node node);
	int getNodeSize();
	void setNodeSize(int nodeWidth);
	Node getStart();
	Node getEnd();
	void setPath(Node start, Node end);
	void clearPathData();
}
