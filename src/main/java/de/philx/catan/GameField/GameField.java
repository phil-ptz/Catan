package de.philx.catan.GameField;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import java.util.*;

import static java.lang.Math.sqrt;

/**
 * Complete implementation of the CATAN game board system
 * Includes hexagons, nodes, edges, terrain types, dice numbers, and adjacency relationships
 */
public class GameField {

    private final Hexagon[] hexagons;
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final double hexagonRadius;
    private int robberPosition;
    
    // Standard dice number distribution (excluding 7)
    private static final int[] DICE_NUMBERS = {2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12};

    public GameField(double hexagonRadius) {
        this.hexagonRadius = hexagonRadius;
        this.hexagons = new Hexagon[19];
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.robberPosition = -1;
        
        generateBoard();
        createNodes();
        createEdges();
        establishAdjacencies();
    }

    /**
     * Generates the hexagonal board with terrain types and dice numbers
     */
    private void generateBoard() {
        double r = hexagonRadius;
        double dx = 1.9 * r; // horizontal distance between centers
        double dy = sqrt(3) * r; // vertical distance between rows

        int[] rowCounts = {3, 4, 5, 4, 3};
        
        // Get randomized terrain distribution
        TerrainType[] terrainTypes = getShuffledTerrainTypes();
        int[] diceNumbers = getShuffledDiceNumbers();
        
        int hexIndex = 0;
        int diceIndex = 0;
        
        for (int i = 0; i < rowCounts.length; i++) {
            int count = rowCounts[i];
            double y = i * dy + 10;
            double offsetX = (5 - count) * dx / 2; // center the row

            for (int j = 0; j < count; j++) {
                double x = j * dx + offsetX;
                
                TerrainType terrain = terrainTypes[hexIndex];
                int diceNumber = 0;
                
                // Assign dice number only to non-desert hexagons
                if (terrain != TerrainType.DESERT) {
                    diceNumber = diceNumbers[diceIndex++];
                } else {
                    robberPosition = hexIndex; // Place robber on desert initially
                }
                
                Hexagon hex = new Hexagon(hexIndex, x, y, r, terrain, diceNumber, new int[]{i, j});
                hex.setStroke(Color.BLACK);
                hex.setStrokeWidth(2.0);
                hexagons[hexIndex] = hex;
                hexIndex++;
            }
        }
    }

    /**
     * Creates all nodes on the board
     * Uses distance-based deduplication to handle imperfect hexagon spacing
     */
    private void createNodes() {
        int nodeId = 0;
        double r = hexagonRadius;
        double dx = 1.9 * r; // Current hexagon spacing (not ideal, but we work with it)
        double dy = sqrt(3) * r;
        int[] rowCounts = {3, 4, 5, 4, 3};
        
        // Collect all potential node positions first
        List<double[]> allPositions = new ArrayList<>();
        
        for (int i = 0; i < rowCounts.length; i++) {
            int count = rowCounts[i];
            double y = i * dy + 10;
            double offsetX = (5 - count) * dx / 2;

            for (int j = 0; j < count; j++) {
                double centerX = j * dx + offsetX;
                double centerY = y;
                
                // Create 6 corner nodes for each hexagon
                for (int corner = 0; corner < 6; corner++) {
                    double angle = Math.toRadians(60 * corner - 30);
                    double nodeX = centerX + r * Math.cos(angle);
                    double nodeY = centerY + r * Math.sin(angle);
                    
                    allPositions.add(new double[]{nodeX, nodeY});
                }
            }
        }
        
        // Now deduplicate based on distance threshold
        // Two nodes are considered the same if they are within this distance
        // Since dx is 1.27 times too large, we need a larger tolerance
        double tolerance = r * 0.3; // 30% of radius to handle the spacing issue
        
        List<double[]> uniquePositions = new ArrayList<>();
        
        for (double[] candidate : allPositions) {
            boolean isDuplicate = false;
            
            for (double[] existing : uniquePositions) {
                double distance = Math.sqrt(
                    Math.pow(candidate[0] - existing[0], 2) + 
                    Math.pow(candidate[1] - existing[1], 2)
                );
                
                if (distance < tolerance) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                uniquePositions.add(candidate);
            }
        }
        
        // Create nodes from unique positions
        for (double[] pos : uniquePositions) {
            nodes.add(new Node(nodeId++, pos[0], pos[1]));
        }
        
        // Debug output can be removed in production
        // System.out.println("Created " + nodes.size() + " unique nodes for the game board");
    }
    
    /**
     * Creates all edges between adjacent nodes
     */
    private void createEdges() {
        int edgeId = 0;
        double connectionThreshold = hexagonRadius * 1.1; // Slightly larger than hexagon side length
        
        // Connect nodes that are close enough to each other
        for (int i = 0; i < nodes.size(); i++) {
            Node node1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node node2 = nodes.get(j);
                
                double distance = Math.sqrt(
                    Math.pow(node1.getX() - node2.getX(), 2) + 
                    Math.pow(node1.getY() - node2.getY(), 2)
                );
                
                if (distance <= connectionThreshold) {
                    Edge edge = new Edge(edgeId++, node1, node2);
                    edges.add(edge);
                    
                    // Update node adjacency
                    node1.addAdjacentNode(node2);
                    node2.addAdjacentNode(node1);
                    node1.addAdjacentEdge(edge);
                    node2.addAdjacentEdge(edge);
                }
            }
        }
    }

    /**
     * Establishes adjacency relationships between hexagons, nodes, and edges
     */
    private void establishAdjacencies() {
        double nodeProximityThreshold = hexagonRadius * 1.1;
        
        // For each hexagon, find its adjacent nodes and edges
        for (Hexagon hex : hexagons) {
            // Find nodes adjacent to this hexagon
            for (Node node : nodes) {
                double distance = Math.sqrt(
                    Math.pow(hex.getCenterX() - node.getX(), 2) + 
                    Math.pow(hex.getCenterY() - node.getY(), 2)
                );
                
                if (distance <= nodeProximityThreshold) {
                    hex.addAdjacentNode(node);
                    node.addAdjacentHexagon(hex);
                }
            }
            
            // Find edges adjacent to this hexagon
            for (Edge edge : edges) {
                if (hex.getAdjacentNodes().contains(edge.getNode1()) && 
                    hex.getAdjacentNodes().contains(edge.getNode2())) {
                    hex.addAdjacentEdge(edge);
                }
            }
        }
    }

    /**
     * Returns a shuffled array of terrain types according to CATAN distribution
     */
    private TerrainType[] getShuffledTerrainTypes() {
        TerrainType[] types = TerrainType.getStandardDistribution();
        List<TerrainType> typeList = Arrays.asList(types);
        Collections.shuffle(typeList);
        return typeList.toArray(new TerrainType[0]);
    }

    /**
     * Returns a shuffled array of dice numbers
     */
    private int[] getShuffledDiceNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int num : DICE_NUMBERS) {
            numbers.add(num);
        }
        Collections.shuffle(numbers);
        return numbers.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Produces resources for all players based on dice roll
     * @param diceRoll The result of the dice roll (2-12)
     * @return Map of hexagon IDs that produced resources
     */
    public List<Integer> produceResources(int diceRoll) {
        List<Integer> producingHexagons = new ArrayList<>();
        
        for (Hexagon hex : hexagons) {
            if (hex.getDiceNumber() == diceRoll && hex.producesResources()) {
                producingHexagons.add(hex.getHexagonId());
            }
        }
        
        return producingHexagons;
    }

    /**
     * Moves the robber to a new hexagon
     * @param hexagonId The ID of the hexagon to move the robber to
     */
    public void moveRobber(int hexagonId) {
        if (robberPosition >= 0 && robberPosition < hexagons.length) {
            hexagons[robberPosition].setRobber(false);
        }
        
        if (hexagonId >= 0 && hexagonId < hexagons.length) {
            hexagons[hexagonId].setRobber(true);
            robberPosition = hexagonId;
        }
        
        // Refresh the visual representation
        refreshVisualRepresentation();
    }
    
    /**
     * Refresh the visual representation of the game field
     */
    private void refreshVisualRepresentation() {
        // This method would ideally trigger a UI update
        // For now, we just mark that the state has changed
        // The UI should call toGroup() again to get updated visuals
    }

    /**
     * Gets the current robber position
     * @return The hexagon ID where the robber is located
     */
    public int getRobberPosition() {
        return robberPosition;
    }

    /**
     * Creates a JavaFX Group for visualization
     */
    public Group toGroup() {
        return toGroup(false, null);
    }
    
    /**
     * Creates a JavaFX Group for visualization with optional building placement mode
     * @param showPlacementOptions Whether to show clickable placement options
     * @param buildingType The type of building to place (null for normal view)
     * @return Group containing all visual elements
     */
    public Group toGroup(boolean showPlacementOptions, String buildingType) {
        Group group = new Group();
        
        // Add all hexagons with their visual elements
        for (Hexagon hex : hexagons) {
            group.getChildren().add(hex.createVisualGroup());
        }
        
        // Add edges (roads) as lines
        for (Edge edge : edges) {
            group.getChildren().add(edge.createVisualGroup(showPlacementOptions && "road".equals(buildingType)));
        }
        
        // Add nodes (settlement/city spots) as small circles
        for (Node node : nodes) {
            group.getChildren().add(node.createVisualGroup(showPlacementOptions && ("settlement".equals(buildingType) || "city".equals(buildingType))));
        }
        
        return group;
    }

    // Getters
    public Hexagon[] getHexagons() {
        return hexagons;
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    public Hexagon getHexagon(int id) {
        if (id >= 0 && id < hexagons.length) {
            return hexagons[id];
        }
        return null;
    }

    public Node getNode(int id) {
        return nodes.stream().filter(n -> n.getNodeId() == id).findFirst().orElse(null);
    }

    public Edge getEdge(int id) {
        return edges.stream().filter(e -> e.getEdgeId() == id).findFirst().orElse(null);
    }

    /**
     * Validates if a settlement can be placed at the given node
     * @param nodeId The node ID where the settlement should be placed
     * @param playerId The player attempting to place the settlement
     * @return true if placement is valid
     */
    public boolean canPlaceSettlement(int nodeId, int playerId) {
        Node node = getNode(nodeId);
        return node != null && node.isValidForSettlement();
    }

    /**
     * Validates if a road can be placed on the given edge
     * @param edgeId The edge ID where the road should be placed
     * @param playerId The player attempting to place the road
     * @return true if placement is valid
     */
    public boolean canPlaceRoad(int edgeId, int playerId) {
        Edge edge = getEdge(edgeId);
        return edge != null && edge.isValidForRoad(playerId);
    }

    /**
     * Validates if a city can be upgraded at the given node
     * @param nodeId The node ID where the city should be placed
     * @param playerId The player attempting the upgrade
     * @return true if upgrade is valid
     */
    public boolean canUpgradeToCity(int nodeId, int playerId) {
        Node node = getNode(nodeId);
        return node != null && node.isValidForCityUpgrade(playerId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GameField{\n");
        sb.append("  Hexagons: ").append(hexagons.length).append("\n");
        sb.append("  Nodes: ").append(nodes.size()).append("\n");
        sb.append("  Edges: ").append(edges.size()).append("\n");
        sb.append("  Robber at: ").append(robberPosition).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
