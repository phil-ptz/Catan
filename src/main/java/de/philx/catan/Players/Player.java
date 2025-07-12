package de.philx.catan.Players;

import de.philx.catan.GameField.Edge;
import de.philx.catan.GameField.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a player in the Catan game. A player has attributes related
 * to their identity, their game actions, and their inventory of game pieces and resources.
 * 
 * Enhanced for the 1995 Catan implementation supporting:
 * - 3-4 players with unique names and colors
 * - Resource inventory management
 * - Building inventory tracking
 * - Victory point calculation
 * - Turn order management
 */
public class Player {
    
    // Player identity
    private String name;
    private String color;  // Changed to String for better color representation
    private int playerId;
    private boolean isActive;
    
    // Resource inventory (Wood, Clay, Grain, Wool, Ore)
    private Map<ResourceType, Integer> resourceInventory;
    
    // Building inventory
    private int availableRoads;
    private int availableSettlements;
    private int availableCities;
    
    // Placed buildings for victory point calculation
    private int placedSettlements;
    private int placedCities;
    
    // Victory points
    private int victoryPoints;
    
    // Turn order
    private int turnOrder;
    
    // Game statistics
    private int totalResourcesCollected;
    private int longestRoadLength;
    
    /**
     * Resource types for the inventory
     */
    public enum ResourceType {
        WOOD, CLAY, GRAIN, WOOL, ORE
    }
    
    /**
     * Valid player colors for 3-4 player game
     */
    public enum PlayerColor {
        RED("Red"),
        BLUE("Blue"), 
        ORANGE("Orange"),
        WHITE("White");
        
        private final String displayName;
        
        PlayerColor(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Constructor for creating a new player
     * @param playerId Unique player identifier (0-3)
     * @param name Player's chosen name
     * @param color Player's color
     * @param turnOrder Turn order position (0-3)
     */
    public Player(int playerId, String name, PlayerColor color, int turnOrder) {
        this.playerId = playerId;
        this.name = name;
        this.color = color.getDisplayName();
        this.turnOrder = turnOrder;
        this.isActive = false;
        
        // Initialize resource inventory
        initializeResourceInventory();
        
        // Initialize building inventory (standard Catan amounts)
        this.availableRoads = 15;      // 15 roads per player
        this.availableSettlements = 5; // 5 settlements per player
        this.availableCities = 4;       // 4 cities per player
        
        // Initialize placed buildings
        this.placedSettlements = 0;
        this.placedCities = 0;
        
        // Initialize victory points
        this.victoryPoints = 0;
        
        // Initialize statistics
        this.totalResourcesCollected = 0;
        this.longestRoadLength = 0;
    }
    
    /**
     * Initialize the resource inventory with zero amounts
     */
    private void initializeResourceInventory() {
        resourceInventory = new HashMap<>();
        for (ResourceType type : ResourceType.values()) {
            resourceInventory.put(type, 0);
        }
    }
    
    // === Resource Management ===
    
    /**
     * Add resources to the player's inventory
     * @param resourceType Type of resource to add
     * @param amount Amount to add
     */
    public void addResource(ResourceType resourceType, int amount) {
        if (amount > 0) {
            int currentAmount = resourceInventory.get(resourceType);
            resourceInventory.put(resourceType, currentAmount + amount);
            totalResourcesCollected += amount;
        }
    }
    
    /**
     * Remove resources from the player's inventory
     * @param resourceType Type of resource to remove
     * @param amount Amount to remove
     * @return true if removal was successful, false if insufficient resources
     */
    public boolean removeResource(ResourceType resourceType, int amount) {
        if (amount <= 0) return false;
        
        int currentAmount = resourceInventory.get(resourceType);
        if (currentAmount >= amount) {
            resourceInventory.put(resourceType, currentAmount - amount);
            return true;
        }
        return false;
    }
    
    /**
     * Get the amount of a specific resource
     * @param resourceType Resource type to check
     * @return Amount of the resource
     */
    public int getResourceAmount(ResourceType resourceType) {
        return resourceInventory.get(resourceType);
    }
    
    /**
     * Get total number of resource cards
     * @return Total resource cards in hand
     */
    public int getTotalResourceCards() {
        return resourceInventory.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Check if player has enough resources for a specific cost
     * @param costs Map of resource costs
     * @return true if player can afford the cost
     */
    public boolean canAfford(Map<ResourceType, Integer> costs) {
        for (Map.Entry<ResourceType, Integer> cost : costs.entrySet()) {
            if (getResourceAmount(cost.getKey()) < cost.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Pay resources (remove them from inventory)
     * @param costs Map of resource costs to pay
     * @return true if payment was successful
     */
    public boolean payResources(Map<ResourceType, Integer> costs) {
        if (!canAfford(costs)) {
            return false;
        }
        
        for (Map.Entry<ResourceType, Integer> cost : costs.entrySet()) {
            removeResource(cost.getKey(), cost.getValue());
        }
        return true;
    }
    
    // === Building Management ===
    
    /**
     * Check if player can build a road
     * @return true if player has available roads and resources
     */
    public boolean canBuildRoad() {
        return availableRoads > 0 && 
               getResourceAmount(ResourceType.WOOD) >= 1 && 
               getResourceAmount(ResourceType.CLAY) >= 1;
    }
    
    /**
     * Build a road (consume resources and reduce available roads)
     * @return true if road was built successfully
     */
    public boolean buildRoad() {
        if (!canBuildRoad()) return false;
        
        removeResource(ResourceType.WOOD, 1);
        removeResource(ResourceType.CLAY, 1);
        availableRoads--;
        return true;
    }
    
    /**
     * Check if player can build a settlement
     * @return true if player has available settlements and resources
     */
    public boolean canBuildSettlement() {
        return availableSettlements > 0 && 
               getResourceAmount(ResourceType.WOOD) >= 1 && 
               getResourceAmount(ResourceType.CLAY) >= 1 &&
               getResourceAmount(ResourceType.GRAIN) >= 1 && 
               getResourceAmount(ResourceType.WOOL) >= 1;
    }
    
    /**
     * Build a settlement (consume resources and track building)
     * @return true if settlement was built successfully
     */
    public boolean buildSettlement() {
        if (!canBuildSettlement()) return false;
        
        removeResource(ResourceType.WOOD, 1);
        removeResource(ResourceType.CLAY, 1);
        removeResource(ResourceType.GRAIN, 1);
        removeResource(ResourceType.WOOL, 1);
        
        availableSettlements--;
        placedSettlements++;
        calculateVictoryPoints();
        return true;
    }
    
    /**
     * Check if player can upgrade a settlement to a city
     * @return true if player has available cities and resources
     */
    public boolean canBuildCity() {
        return availableCities > 0 && 
               placedSettlements > 0 &&
               getResourceAmount(ResourceType.GRAIN) >= 2 && 
               getResourceAmount(ResourceType.ORE) >= 3;
    }
    
    /**
     * Upgrade a settlement to a city
     * @return true if city was built successfully
     */
    public boolean buildCity() {
        if (!canBuildCity()) return false;
        
        removeResource(ResourceType.GRAIN, 2);
        removeResource(ResourceType.ORE, 3);
        
        availableCities--;
        placedSettlements--;  // Settlement is replaced by city
        placedCities++;
        availableSettlements++;  // Settlement piece returns to inventory
        calculateVictoryPoints();
        return true;
    }
    
    // === Victory Point Management ===
    
    /**
     * Calculate and update victory points based on current buildings
     */
    public void calculateVictoryPoints() {
        victoryPoints = placedSettlements + (placedCities * 2);
        // Note: Longest road points would be added separately when implemented
    }
    
    /**
     * Add victory points (for special achievements like longest road)
     * @param points Points to add
     */
    public void addVictoryPoints(int points) {
        victoryPoints += points;
    }
    
    /**
     * Remove victory points (when losing special achievements)
     * @param points Points to remove
     */
    public void removeVictoryPoints(int points) {
        victoryPoints = Math.max(0, victoryPoints - points);
    }
    
    /**
     * Check if player has won the game (10+ victory points)
     * @return true if player has won
     */
    public boolean hasWon() {
        return victoryPoints >= 10;
    }
    
    // === Turn Management ===
    
    /**
     * Start this player's turn
     */
    public void startTurn() {
        isActive = true;
    }
    
    /**
     * End this player's turn
     */
    public void endTurn() {
        isActive = false;
    }
    
    // === Getters and Setters ===
    
    public int getPlayerId() {
        return playerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getVictoryPoints() {
        return victoryPoints;
    }
    
    public int getTurnOrder() {
        return turnOrder;
    }
    
    public void setTurnOrder(int turnOrder) {
        this.turnOrder = turnOrder;
    }
    
    public int getAvailableRoads() {
        return availableRoads;
    }
    
    public int getAvailableSettlements() {
        return availableSettlements;
    }
    
    public int getAvailableCities() {
        return availableCities;
    }
    
    public int getPlacedSettlements() {
        return placedSettlements;
    }
    
    public int getPlacedCities() {
        return placedCities;
    }
    
    public int getTotalResourcesCollected() {
        return totalResourcesCollected;
    }
    
    public int getLongestRoadLength() {
        return longestRoadLength;
    }
    
    public void setLongestRoadLength(int length) {
        this.longestRoadLength = length;
    }
    
    /**
     * Calculate the longest continuous road for this player
     * @param edges List of all edges on the game board
     * @return The length of the longest continuous road
     */
    public int calculateLongestRoad(List<Edge> edges) {
        // Get all edges with roads owned by this player
        List<Edge> playerRoads = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.hasRoad() && edge.getRoad().getPlayerId() == this.playerId) {
                playerRoads.add(edge);
            }
        }
        
        if (playerRoads.isEmpty()) {
            return 0;
        }
        
        int maxLength = 0;
        
        // Try starting from each road segment to find the longest path
        for (Edge startEdge : playerRoads) {
            Set<Edge> visited = new HashSet<>();
            int length = findLongestPath(startEdge, playerRoads, visited);
            maxLength = Math.max(maxLength, length);
        }
        
        this.longestRoadLength = maxLength;
        return maxLength;
    }
    
    /**
     * Recursively find the longest path starting from a given edge
     * @param currentEdge The current edge in the path
     * @param playerRoads All roads owned by this player
     * @param visited Set of already visited edges
     * @return The length of the longest path from this edge
     */
    private int findLongestPath(Edge currentEdge, List<Edge> playerRoads, Set<Edge> visited) {
        visited.add(currentEdge);
        int maxLength = 1; // Current edge counts as 1
        
        // Check both nodes of the current edge for connecting roads
        Node[] nodes = {currentEdge.getNode1(), currentEdge.getNode2()};
        
        for (Node node : nodes) {
            // Skip if this node has an opponent's building (blocks the road)
            if (node.hasBuilding() && node.getBuilding().getPlayerId() != this.playerId) {
                continue;
            }
            
            // Find all roads connected to this node
            for (Edge adjacentEdge : node.getAdjacentEdges()) {
                // Skip if not our road, already visited, or same as current edge
                if (!playerRoads.contains(adjacentEdge) || 
                    visited.contains(adjacentEdge) || 
                    adjacentEdge.equals(currentEdge)) {
                    continue;
                }
                
                // Recursively explore this path
                Set<Edge> newVisited = new HashSet<>(visited);
                int pathLength = 1 + findLongestPath(adjacentEdge, playerRoads, newVisited);
                maxLength = Math.max(maxLength, pathLength);
            }
        }
        
        return maxLength;
    }
    
    /**
     * Get a copy of the resource inventory for display purposes
     * @return Map of resource types and amounts
     */
    public Map<ResourceType, Integer> getResourceInventory() {
        return new HashMap<>(resourceInventory);
    }
    
    /**
     * String representation of the player for debugging
     */
    @Override
    public String toString() {
        return String.format("Player[%d]: %s (%s) - VP: %d, Turn: %d, Resources: %d", 
                           playerId, name, color, victoryPoints, turnOrder, getTotalResourceCards());
    }
}
