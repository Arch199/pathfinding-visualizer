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
            setStartingCost(node);
            nodes.add(node);
        }
    }
    
    @Override
    protected void pathfindStep() {        
        // Visit the next best node from the priority queue
        Node current = nodes.remove();
        current.setState(Node.State.VISITING);
        
        // Stop if we've found our destination (so this isn't real Dijkstra's)
        if (current == graph.getEnd()) {
            Node parent = graph.getEnd();
            do {
                parent.setState(Node.State.ON_PATH);
            } while ((parent = parent.getParent()) != null);
            stop();
        }
        
        // Explore the adjacent nodes, checking if they achieve smaller costs
        for (Node next : graph.getConnected(current)) {
            int newCost = getNextCost(current, next);
            if (newCost < next.getCost()) {
                nodes.remove(next);
                next.setCost(newCost);
                next.setParent(current);
                next.setState(Node.State.CONSIDERED);
                nodes.add(next);
            }
        }
        
        // Stop if the priority queue is empty
        if (nodes.isEmpty()) {
            System.out.println("Priority queue is empty :(");
            stop();
        }
    }
    
    protected void setStartingCost(Node node) {
        if (node == graph.getStart()) {
            node.setCost(0);
        } else {
            node.setCost(Integer.MAX_VALUE);
        }
    }
    
    protected int getNextCost(Node current, Node next) {
        return current.getCost() + current.distanceTo(next);
    }
}
