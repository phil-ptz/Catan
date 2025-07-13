package de.philx.catan.GameField;

import de.philx.catan.GamePieces.GamePiece;
import de.philx.catan.GamePieces.Settlement;
import de.philx.catan.GamePieces.City;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents a node on the game board where settlements and cities can be built
 */
public class Node {
    private final int id;
    private final double x;
    private final double y;
    private GamePiece building; // Settlement or City
    private final List<Hexagon> adjacentHexagons;
    private final List<Edge> adjacentEdges;
    private final List<Node> adjacentNodes;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.building = null;
        this.adjacentHexagons = new ArrayList<>();
        this.adjacentEdges = new ArrayList<>();
        this.adjacentNodes = new ArrayList<>();
    }

    public int getNodeId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public GamePiece getBuilding() {
        return building;
    }

    public void setBuilding(GamePiece building) {
        this.building = building;
    }

    public boolean hasBuilding() {
        return building != null;
    }

    public boolean hasSettlement() {
        return building instanceof Settlement;
    }

    public boolean hasCity() {
        return building instanceof City;
    }

    public List<Hexagon> getAdjacentHexagons() {
        return new ArrayList<>(adjacentHexagons);
    }

    public List<Edge> getAdjacentEdges() {
        return new ArrayList<>(adjacentEdges);
    }

    public List<Node> getAdjacentNodes() {
        return new ArrayList<>(adjacentNodes);
    }

    public void addAdjacentHexagon(Hexagon hexagon) {
        if (!adjacentHexagons.contains(hexagon)) {
            adjacentHexagons.add(hexagon);
        }
    }

    public void addAdjacentEdge(Edge edge) {
        if (!adjacentEdges.contains(edge)) {
            adjacentEdges.add(edge);
        }
    }

    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) {
            adjacentNodes.add(node);
        }
    }

    /**
     * Checks if this node satisfies the distance rule (no adjacent settlements)
     * @return true if placement is valid according to distance rule
     */
    public boolean isValidForSettlement() {
        // Rule 1: Node must not already have a building
        if (hasBuilding()) {
            return false;
        }
        
        // Rule 2: No adjacent nodes can have buildings (distance rule)
        for (Node adjacentNode : adjacentNodes) {
            if (adjacentNode.hasBuilding()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Checks if this node can be upgraded to a city
     * @param playerId The ID of the player attempting the upgrade
     * @return true if upgrade is valid
     */
    public boolean isValidForCityUpgrade(int playerId) {
        return hasSettlement() && building.getPlayerId() == playerId;
    }
    
    /**
     * Creates a visual representation of this node
     * @param showAsClickable Whether to show as a clickable placement option
     * @return Group containing the visual elements
     */
    public javafx.scene.Group createVisualGroup(boolean showAsClickable) {
        javafx.scene.Group group = new javafx.scene.Group();
        
        // Create circle for node
        javafx.scene.shape.Circle nodeCircle = new javafx.scene.shape.Circle(x, y, showAsClickable ? 8 : 5);
        
        if (hasBuilding()) {
            // Show existing building
            if (hasSettlement()) {
                nodeCircle.setFill(getPlayerColorAsJavaFXColor(building.getPlayerId()));
                nodeCircle.setStroke(javafx.scene.paint.Color.BLACK);
                nodeCircle.setStrokeWidth(2);
            } else if (hasCity()) {
                nodeCircle.setFill(getPlayerColorAsJavaFXColor(building.getPlayerId()));
                nodeCircle.setStroke(javafx.scene.paint.Color.BLACK);
                nodeCircle.setStrokeWidth(3);
                // Make cities slightly larger
                nodeCircle.setRadius(showAsClickable ? 10 : 7);
            }
        } else if (showAsClickable) {
            // Show as placement option
            nodeCircle.setFill(javafx.scene.paint.Color.LIGHTGREEN);
            nodeCircle.setStroke(javafx.scene.paint.Color.DARKGREEN);
            nodeCircle.setStrokeWidth(2);
            nodeCircle.setOpacity(0.7);
        } else {
            // Show as available spot
            nodeCircle.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            nodeCircle.setStroke(javafx.scene.paint.Color.GRAY);
            nodeCircle.setStrokeWidth(1);
            nodeCircle.setOpacity(0.5);
        }
        
        group.getChildren().add(nodeCircle);
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
        return "Node{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", building=" + building +
                '}';
    }

    /**
     * Visual representation of the node as a circle
     * @return a Group containing the visual elements of the node
     */
    public Group toVisual() {
        Circle circle = new Circle(x, y, 20); // Radius of 20 for visibility
        circle.setFill(hasBuilding() ? Color.GRAY : Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);

        Group group = new Group(circle);
        group.setUserData(this); // Store reference to this node

        return group;
    }

    /**
     * Creates a visual representation of this node
     * @param showAsClickable Whether to show as a clickable placement option
     * @return Group containing the visual elements
     */
    public Group createVisualGroup(boolean showAsClickable) {
        Group group = new Group();
        
        // Create circle for node
        Circle nodeCircle = new Circle(x, y, showAsClickable ? 8 : 5);
        
        if (hasBuilding()) {
            // Show existing building
            if (hasSettlement()) {
                nodeCircle.setFill(getPlayerColorAsJavaFXColor(building.getColor()));
                nodeCircle.setStroke(Color.BLACK);
                nodeCircle.setStrokeWidth(2);
            } else if (hasCity()) {
                nodeCircle.setFill(getPlayerColorAsJavaFXColor(building.getColor()));
                nodeCircle.setStroke(Color.BLACK);
                nodeCircle.setStrokeWidth(3);
                nodeCircle.setRadius(showAsClickable ? 10 : 7);
            }
        } else if (showAsClickable) {
            // Show as placement option
            nodeCircle.setFill(Color.LIGHTGREEN);
            nodeCircle.setOpacity(0.7);
            nodeCircle.setStroke(Color.DARKGREEN);
            nodeCircle.setStrokeWidth(2);
            
            // Add ID for click handling
            nodeCircle.setUserData("node_" + id);
        } else {
            // Show as normal node
            nodeCircle.setFill(Color.LIGHTGRAY);
            nodeCircle.setOpacity(0.3);
        }
        
        group.getChildren().add(nodeCircle);
        return group;
    }

    /**
     * Convert player color character to JavaFX Color
     * @param colorChar The color character (R, B, G, Y)
     * @return JavaFX Color object
     */
    private Color getPlayerColorAsJavaFXColor(char colorChar) {
        switch (colorChar) {
            case 'R': return Color.RED;
            case 'B': return Color.BLUE;
            case 'G': return Color.GREEN;
            case 'Y': return Color.YELLOW;
            default: return Color.GRAY;
        }
    }
}
