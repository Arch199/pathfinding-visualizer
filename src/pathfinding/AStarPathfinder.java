package pathfinding;

public class AStarPathfinder extends DijkstraPathfinder {
    public AStarPathfinder(Graph graph, Runnable onUpdate, Runnable onFinish) {
        super(graph, onUpdate, onFinish);
    }
    
    @Override
    protected void setStartingCost(Node node) {
        if (node == graph.getStart()) {
            node.setCost(node.distanceTo(graph.getEnd()));
        } else {
            node.setCost(Integer.MAX_VALUE);
        }
    }
    
    @Override
    protected int getNextCost(Node current, Node next) {
        int distDiff = current.distanceTo(graph.getEnd()) - next.distanceTo(graph.getEnd());
        return current.getCost() + current.distanceTo(next) - distDiff;
    }
}
