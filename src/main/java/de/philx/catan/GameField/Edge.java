package de.philx.catan.GameField;

import de.philx.catan.GamePieces.Street;
import javafx.scene.paint.Color;

/**
 * Represents an edge on the game board where roads can be built
 */
public class Edge {
    private final int id;
    private final Node node1;
    private final Node node2;
    private Street road; // Road built on this edge
    
    public Edge(int id, Node node1, Node node2) {
        this.id = id;
        this.node1 = node1;
        this.node2 = node2;
        this.road = null;
    }
    
    public int getEdgeId() {
        return id;
    }
    
    public Node getNode1() {
        return node1;
    }
    
    public Node getNode2() {
        return node2;
    }
    
    public Street getRoad() {
        return road;
    }
    
    public void setRoad(Street road) {
        this.road = road;
    }
    
    public boolean hasRoad() {
        return road != null;
    }
    
    /**
     * Checks if this edge connects to the given node
     * @param node The node to check
     * @return true if this edge connects to the node
     */
    public boolean connectsToNode(Node node) {
        return node1.equals(node) || node2.equals(node);
    }
    
    /**
     * Gets the other node connected by this edge
     * @param node One of the nodes connected by this edge
     * @return The other connected node, or null if the given node is not connected
     */
    public Node getOtherNode(Node node) {
        if (node1.equals(node)) {
            return node2;
        } else if (node2.equals(node)) {
            return node1;
        }
        return null;
    }
    
    /**
     * Checks if this edge is valid for road placement
     * @param playerId The ID of the player attempting to place the road
     * @return true if road placement is valid
     */
    public boolean isValidForRoad(int playerId) {
        if (hasRoad()) {
            return false;
        }
        
        // Check if player has an adjacent road or building
        return hasAdjacentPlayerRoadOrBuilding(playerId);
    }
    
    /**
     * Checks if this edge is valid for road placement during setup phase
     * @param playerId The ID of the player attempting to place the road
     * @param connectedSettlementNode The node where the player just placed a settlement
     * @return true if road placement is valid for setup
     */
    public boolean isValidForSetupRoad(int playerId, Node connectedSettlementNode) {
        if (hasRoad()) {
            return false;
        }
        
        // During setup, road must connect to the settlement just placed
        return connectsToNode(connectedSettlementNode);
    }
    
    private boolean hasAdjacentPlayerRoadOrBuilding(int playerId) {
        // Check if either node has a building owned by the player
        if (node1.hasBuilding() && node1.getBuilding().getPlayerId() == playerId) {
            return true;
        }
        if (node2.hasBuilding() && node2.getBuilding().getPlayerId() == playerId) {
            return true;
        }
        
        // Check if any adjacent edge has a road owned by the player
        for (Edge edge : node1.getAdjacentEdges()) {
            if (!edge.equals(this) && edge.hasRoad() && edge.getRoad().getPlayerId() == playerId) {
                return true;
            }
        }
        for (Edge edge : node2.getAdjacentEdges()) {
            if (!edge.equals(this) && edge.hasRoad() && edge.getRoad().getPlayerId() == playerId) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Creates a visual representation of this edge
     * @param showAsClickable Whether to show as a clickable placement option
     * @return Group containing the visual elements
     */
    public javafx.scene.Group createVisualGroup(boolean showAsClickable) {
        javafx.scene.Group group = new javafx.scene.Group();
        
        // Create line for edge
        javafx.scene.shape.Line edgeLine = new javafx.scene.shape.Line(
            node1.getX(), node1.getY(), 
            node2.getX(), node2.getY()
        );
        
        if (hasRoad()) {
            // Show existing road
            edgeLine.setStroke(getPlayerColorAsJavaFXColor(road.getPlayerId()));
            edgeLine.setStrokeWidth(4);
        } else if (showAsClickable) {
            // Show as placement option
            edgeLine.setStroke(Color.LIGHTGREEN);
            edgeLine.setStrokeWidth(6);
            edgeLine.setOpacity(0.7);
            
            // Add ID for click handling
            edgeLine.setUserData("edge_" + id);
        } else {
            // Show as normal edge
            edgeLine.setStroke(Color.LIGHTGRAY);
            edgeLine.setStrokeWidth(1);
            edgeLine.setOpacity(0.3);
        }
        
        group.getChildren().add(edgeLine);
        return group;
    }
    
    /**
     * Convert player ID to JavaFX Color
     */
    private javafx.scene.paint.Color getPlayerColorAsJavaFXColor(int playerId) {
        switch (playerId) {
            case 0: return javafx.scene.paint.Color.RED;
            case 1: return javafx.scene.paint.Color.BLUE;
            case 2: return javafx.scene.paint.Color.WHITE;
            case 3: return javafx.scene.paint.Color.ORANGE;
            default: return javafx.scene.paint.Color.GRAY;
        }
    }
    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", node1=" + node1.getNodeId() +
                ", node2=" + node2.getNodeId() +
                ", road=" + road +
                '}';
    }
}
