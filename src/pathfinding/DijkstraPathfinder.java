package pathfinding;

import java.util.PriorityQueue;

public class DijkstraPathfinder extends Pathfinder {
    private PriorityQueue<Node> nodes;
    
    public DijkstraPathfinder(Graph graph, Runnable onUpdate, Runnable onFinish) {
        super(graph, onUpdate, onFinish);
        
        // Initialize the priority queue
        nodes = new PriorityQueue<Node>();
        
        // Set the cost of the start node to 0 and the rest to the maximum
        for (Node node : graph) {
            if (node == graph.getStart()) {
                node.setCost(0);
            } else {
                node.setCost(Integer.MAX_VALUE);
            }
            nodes.add(node);
        }
    }
    
    @Override
    public void pathfindStep() {        
        // Visit the next best node from the priority queue
        Node current = nodes.remove();
        current.setState(Node.State.ON_PATH);
        
        // Stop if we've found our destination
        if (current == graph.getEnd()) {
            stop();
        }
        
        // Explore the adjacent nodes, checking if they achieve smaller costs
        for (Node next : graph.getConnected(current)) {
            int newCost = current.getCost() + current.distanceTo(next);
            if (newCost < next.getCost()) {
                nodes.remove(next);
                next.setCost(newCost);
                next.setParent(current);
                next.setState(Node.State.VISITING);
                nodes.add(next);
            }
        }
        
        // Stop if the priority queue is empty
        if (nodes.isEmpty()) {
            stop();
        }
    }

}
