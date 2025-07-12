package de.philx.catan.GameField;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Group;
import java.util.ArrayList;
import java.util.List;

public class Hexagon extends Polygon {

    private final int id;
    private final int[] pos;
    private final double centerX;
    private final double centerY;
    private final double radius;
    private TerrainType terrainType;
    private int diceNumber;
    private boolean hasRobber;
    private final List<Node> adjacentNodes;
    private final List<Edge> adjacentEdges;

    public Hexagon(int id, double centerX, double centerY, double radius, TerrainType terrainType, int diceNumber, int[] pos) {
        this.id = id;
        this.pos = pos;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.terrainType = terrainType;
        this.diceNumber = diceNumber;
        this.hasRobber = (terrainType == TerrainType.DESERT);
        this.adjacentNodes = new ArrayList<>();
        this.adjacentEdges = new ArrayList<>();
        
        // Create hexagon shape
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            this.getPoints().addAll(x, y);
        }
        this.setFill(terrainType.getColor());
    }

    // Legacy constructor for compatibility
    public Hexagon(double centerX, double centerY, double radius, Color color, int[] pos) {
        this(0, centerX, centerY, radius, TerrainType.DESERT, 0, pos);
        this.setFill(color);
    }

    public int getHexagonId() {
        return id;
    }

    public int[] getPos() {
        return pos;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
        this.setFill(terrainType.getColor());
    }

    public int getDiceNumber() {
        return diceNumber;
    }

    public void setDiceNumber(int diceNumber) {
        this.diceNumber = diceNumber;
    }

    public boolean hasRobber() {
        return hasRobber;
    }

    public void setRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }

    public boolean producesResources() {
        return terrainType != TerrainType.DESERT && !hasRobber;
    }

    public String getResourceType() {
        return terrainType.getResource();
    }

    public List<Node> getAdjacentNodes() {
        return new ArrayList<>(adjacentNodes);
    }

    public List<Edge> getAdjacentEdges() {
        return new ArrayList<>(adjacentEdges);
    }

    public void addAdjacentNode(Node node) {
        if (!adjacentNodes.contains(node)) {
            adjacentNodes.add(node);
        }
    }

    public void addAdjacentEdge(Edge edge) {
        if (!adjacentEdges.contains(edge)) {
            adjacentEdges.add(edge);
        }
    }

    /**
     * Creates a visual group containing the hexagon and its dice number
     * @return Group containing the hexagon visualization
     */
    public Group createVisualGroup() {
        Group group = new Group();
        group.getChildren().add(this);
        
        // Add dice number text if not desert
        if (terrainType != TerrainType.DESERT) {
            Text diceText = new Text(centerX, centerY + 5, String.valueOf(diceNumber));
            diceText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            diceText.setFill(Color.BLACK);
            diceText.setX(centerX - diceText.getBoundsInLocal().getWidth() / 2);
            group.getChildren().add(diceText);
        }
        
        // Add robber indicator if present
        if (hasRobber) {
            Text robberText = new Text(centerX, centerY - 10, "R");
            robberText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            robberText.setFill(Color.RED);
            robberText.setX(centerX - robberText.getBoundsInLocal().getWidth() / 2);
            group.getChildren().add(robberText);
        }
        
        return group;
    }

    @Override
    public String toString() {
        return "Hexagon{" +
                "id=" + id +
                ", terrainType=" + terrainType +
                ", diceNumber=" + diceNumber +
                ", hasRobber=" + hasRobber +
                '}';
    }
}
