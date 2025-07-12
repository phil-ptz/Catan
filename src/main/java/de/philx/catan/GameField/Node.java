package de.philx.catan.GameField;

import de.philx.catan.GamePieces.GamePiece;
import de.philx.catan.GamePieces.Settlement;
import de.philx.catan.GamePieces.City;
import java.util.ArrayList;
import java.util.List;

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
        if (hasBuilding()) {
            return false;
        }
        
        // Check that no adjacent nodes have buildings
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

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", building=" + building +
                '}';
    }
}
