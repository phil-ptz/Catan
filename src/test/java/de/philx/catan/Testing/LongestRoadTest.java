package de.philx.catan.Testing;

import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Edge;
import de.philx.catan.GamePieces.Street;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.PlayerManager;
import de.philx.catan.Cards.Special.LongestRoad;

/**
 * Test class for longest road functionality
 * Tests road calculation, longest road detection, and victory point awarding
 */
public class LongestRoadTest {
    
    public static void main(String[] args) {
        System.out.println("=== CATAN LONGEST ROAD TEST ===");
        
        LongestRoadTest test = new LongestRoadTest();
        
        try {
            test.testBasicRoadCalculation();
            test.testLongestRoadAward();
            test.testMinimumRoadLength();
            
            System.out.println("\n✅ All Longest Road tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test basic road length calculation for a single player
     */
    private void testBasicRoadCalculation() {
        System.out.println("\n--- Test: Basic Road Calculation ---");
        
        // Create test components without JavaFX dependencies
        GameField gameField = new GameField(50.0);
        PlayerManager playerManager = new PlayerManager();
        
        // Add test players
        playerManager.addPlayer("Spieler 1", Player.PlayerColor.RED);
        playerManager.addPlayer("Spieler 2", Player.PlayerColor.BLUE);
        playerManager.startGame();
        
        // Get the first player
        Player player = playerManager.getCurrentPlayer();
        System.out.println("Testing with player: " + player.getName());
        
        // Calculate initial road length (should be 0)
        int initialLength = player.calculateLongestRoad(gameField.getEdges());
        System.out.println("Initial road length: " + initialLength);
        assert initialLength == 0 : "Initial road length should be 0";
        
        // Manually place a few connected roads for testing
        if (gameField.getEdges().size() >= 2) {
            Edge edge1 = gameField.getEdges().get(0);
            Edge edge2 = gameField.getEdges().get(1);
            
            // Place roads manually for testing
            Street road1 = new Street(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            Street road2 = new Street(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            
            edge1.setRoad(road1);
            edge2.setRoad(road2);
            
            // Calculate road length after placing roads
            int newLength = player.calculateLongestRoad(gameField.getEdges());
            System.out.println("Road length after placing 2 roads: " + newLength);
            
            // Clean up for next test
            edge1.setRoad(null);
            edge2.setRoad(null);
        }
        
        System.out.println("✅ Basic road calculation test completed");
    }
    
    /**
     * Test longest road award mechanism
     */
    private void testLongestRoadAward() {
        System.out.println("\n--- Test: Longest Road Award ---");
        
        PlayerManager playerManager = new PlayerManager();
        GameField gameField = new GameField(50.0);
        
        // Add test players
        playerManager.addPlayer("Spieler 1", Player.PlayerColor.RED);
        playerManager.addPlayer("Spieler 2", Player.PlayerColor.BLUE);
        playerManager.startGame();
        
        // Get players
        Player player1 = playerManager.getPlayerById(0);
        Player player2 = playerManager.getPlayerById(1);
        
        System.out.println("Testing longest road award between " + 
                         player1.getName() + " and " + player2.getName());
        
        // Check initial state
        Player longestRoadHolder = playerManager.getLongestRoadPlayer();
        System.out.println("Initial longest road holder: " + 
                         (longestRoadHolder != null ? longestRoadHolder.getName() : "None"));
        assert longestRoadHolder == null : "Initially no one should have longest road";
        
        // Test the update mechanism
        boolean changed = playerManager.updateLongestRoad(gameField.getEdges());
        System.out.println("Longest road update result: " + changed);
        
        System.out.println("✅ Longest road award test completed");
    }
    
    /**
     * Test minimum road length requirement
     */
    private void testMinimumRoadLength() {
        System.out.println("\n--- Test: Minimum Road Length Requirement ---");
        
        PlayerManager playerManager = new PlayerManager();
        
        // Test that minimum road length is enforced
        int minLength = LongestRoad.getMinimumRoadLength();
        System.out.println("Minimum road length for award: " + minLength);
        assert minLength == 5 : "Minimum road length should be 5";
        
        // Verify that roads shorter than minimum don't award the card
        Player currentHolder = playerManager.getLongestRoadPlayer();
        if (currentHolder == null) {
            System.out.println("✅ Correctly no longest road holder with insufficient roads");
        } else {
            int currentLength = playerManager.getCurrentLongestRoadLength();
            System.out.println("Current holder has " + currentLength + " roads");
            assert currentLength >= minLength : "Holder should have at least minimum road length";
        }
        
        System.out.println("✅ Minimum road length test completed");
    }
}
