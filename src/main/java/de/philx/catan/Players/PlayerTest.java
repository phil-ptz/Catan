package de.philx.catan.Players;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class to demonstrate and validate Player and PlayerManager functionality.
 * This class shows how the enhanced player management system works.
 */
public class PlayerTest {
    
    public static void main(String[] args) {
        System.out.println("=== CATAN PLAYER MANAGEMENT TEST ===\n");
        
        // Test PlayerManager
        testPlayerManager();
        
        // Test Player functionality
        testPlayerFunctionality();
        
        System.out.println("=== ALL TESTS COMPLETED ===");
    }
    
    /**
     * Test PlayerManager functionality
     */
    private static void testPlayerManager() {
        System.out.println("--- Testing PlayerManager ---");
        
        PlayerManager manager = new PlayerManager();
        
        // Test adding players
        try {
            manager.addPlayer("Alice", Player.PlayerColor.RED);
            manager.addPlayer("Bob", Player.PlayerColor.BLUE);
            manager.addPlayer("Charlie", Player.PlayerColor.ORANGE);
            System.out.println("✓ Successfully added 3 players");
            
            // Test duplicate name/color validation
            try {
                manager.addPlayer("Alice", Player.PlayerColor.WHITE);
                System.out.println("✗ Should have failed - duplicate name");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly rejected duplicate name: " + e.getMessage());
            }
            
            try {
                manager.addPlayer("David", Player.PlayerColor.RED);
                System.out.println("✗ Should have failed - duplicate color");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly rejected duplicate color: " + e.getMessage());
            }
            
            // Test game start
            if (manager.canStartGame()) {
                manager.startGame();
                System.out.println("✓ Game started with " + manager.getPlayerCount() + " players");
            }
            
            // Test turn management
            Player currentPlayer = manager.getCurrentPlayer();
            System.out.println("✓ Current player: " + currentPlayer.getName() + " (" + currentPlayer.getColor() + ")");
            
            // Test next turn
            Player nextPlayer = manager.nextTurn();
            System.out.println("✓ Next player: " + nextPlayer.getName() + " (" + nextPlayer.getColor() + ")");
            
            // Show all players in turn order
            System.out.println("\n--- Players in turn order ---");
            List<Player> orderedPlayers = manager.getPlayersInTurnOrder();
            for (Player player : orderedPlayers) {
                System.out.printf("%d. %s (%s) - VP: %d%n", 
                    player.getTurnOrder() + 1, player.getName(), 
                    player.getColor(), player.getVictoryPoints());
            }
            
        } catch (Exception e) {
            System.out.println("✗ PlayerManager test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test Player functionality
     */
    private static void testPlayerFunctionality() {
        System.out.println("--- Testing Player Functionality ---");
        
        // Create a test player
        Player player = new Player(0, "TestPlayer", Player.PlayerColor.RED, 0);
        
        // Test resource management
        System.out.println("--- Resource Management ---");
        player.addResource(Player.ResourceType.WOOD, 3);
        player.addResource(Player.ResourceType.CLAY, 2);
        player.addResource(Player.ResourceType.GRAIN, 4);
        player.addResource(Player.ResourceType.WOOL, 1);
        player.addResource(Player.ResourceType.ORE, 2);
        
        System.out.printf("✓ Added resources - Total: %d cards%n", player.getTotalResourceCards());
        System.out.printf("  Wood: %d, Clay: %d, Grain: %d, Wool: %d, Ore: %d%n",
            player.getResourceAmount(Player.ResourceType.WOOD),
            player.getResourceAmount(Player.ResourceType.CLAY),
            player.getResourceAmount(Player.ResourceType.GRAIN),
            player.getResourceAmount(Player.ResourceType.WOOL),
            player.getResourceAmount(Player.ResourceType.ORE));
        
        // Test building roads
        System.out.println("\n--- Building Management ---");
        System.out.printf("Available roads before: %d%n", player.getAvailableRoads());
        
        if (player.canBuildRoad()) {
            player.buildRoad();
            System.out.printf("✓ Built road - Available roads now: %d%n", player.getAvailableRoads());
        }
        
        // Test building settlements
        System.out.printf("Available settlements before: %d%n", player.getAvailableSettlements());
        
        if (player.canBuildSettlement()) {
            player.buildSettlement();
            System.out.printf("✓ Built settlement - Available: %d, Placed: %d, VP: %d%n", 
                player.getAvailableSettlements(), player.getPlacedSettlements(), player.getVictoryPoints());
        }
        
        // Test building cities
        player.addResource(Player.ResourceType.GRAIN, 2); // Add more grain for city
        player.addResource(Player.ResourceType.ORE, 3);   // Add ore for city
        
        System.out.printf("Available cities before: %d%n", player.getAvailableCities());
        
        if (player.canBuildCity()) {
            player.buildCity();
            System.out.printf("✓ Built city - Available cities: %d, Placed cities: %d, VP: %d%n", 
                player.getAvailableCities(), player.getPlacedCities(), player.getVictoryPoints());
        }
        
        // Test victory conditions
        System.out.println("\n--- Victory Point System ---");
        System.out.printf("Current victory points: %d%n", player.getVictoryPoints());
        System.out.printf("Has won: %s%n", player.hasWon() ? "Yes" : "No");
        
        // Simulate getting 10 victory points
        player.addVictoryPoints(8); // Add enough to win
        System.out.printf("After adding 8 VP: %d total, Has won: %s%n", 
            player.getVictoryPoints(), player.hasWon() ? "Yes" : "No");
        
        // Test resource costs
        System.out.println("\n--- Resource Cost Testing ---");
        Map<Player.ResourceType, Integer> roadCost = new HashMap<>();
        roadCost.put(Player.ResourceType.WOOD, 1);
        roadCost.put(Player.ResourceType.CLAY, 1);
        
        System.out.printf("Can afford another road: %s%n", player.canAfford(roadCost) ? "Yes" : "No");
        
        // Display final player state
        System.out.println("\n--- Final Player State ---");
        System.out.println(player.toString());
        
        Map<Player.ResourceType, Integer> inventory = player.getResourceInventory();
        System.out.println("Resource Inventory:");
        for (Map.Entry<Player.ResourceType, Integer> entry : inventory.entrySet()) {
            System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
        }
        
        System.out.println();
    }
}
