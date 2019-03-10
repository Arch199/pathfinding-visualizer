package pathfinding;

import java.util.List;

import pathfinding.Node;

public interface Graph<T extends Node> {
	public List<T> getConnected(T node);
	public int getNodeSize();
	public void setNodeSize(int nodeWidth);
	public T getStart();
	public T getEnd();
	public void setPath(T start, T end);
	public void clearPathData();
}
