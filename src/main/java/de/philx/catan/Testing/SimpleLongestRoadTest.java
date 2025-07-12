package de.philx.catan.Testing;

import de.philx.catan.GameField.Edge;
import de.philx.catan.GameField.Node;
import de.philx.catan.GamePieces.Street;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.PlayerManager;
import de.philx.catan.Cards.Special.LongestRoad;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified test for longest road functionality that doesn't require JavaFX
 */
public class SimpleLongestRoadTest {
    
    public static void main(String[] args) {
        System.out.println("=== SIMPLIFIED LONGEST ROAD TEST ===");
        
        SimpleLongestRoadTest test = new SimpleLongestRoadTest();
        
        try {
            test.testLongestRoadCardBasics();
            test.testPlayerManagerLongestRoad();
            test.testMinimumRoadLength();
            
            System.out.println("\n✅ All Simplified Longest Road tests passed!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test the LongestRoad card basics
     */
    private void testLongestRoadCardBasics() {
        System.out.println("\n--- Test: LongestRoad Card Basics ---");
        
        LongestRoad card = new LongestRoad();
        
        // Test card properties
        System.out.println("Card name: " + card.getName());
        System.out.println("Card victory points: " + card.getVictoryPoints());
        System.out.println("Minimum road length: " + LongestRoad.getMinimumRoadLength());
        
        assert card.getVictoryPoints() == 2 : "Longest road should give 2 victory points";
        assert LongestRoad.getMinimumRoadLength() == 5 : "Minimum road length should be 5";
        assert !card.isActive() : "Card should not be active initially";
        assert card.getPlayerId() == -1 : "Card should have no owner initially";
        
        // Test card assignment
        card.setPlayerId(0);
        assert card.isActive() : "Card should be active when assigned";
        assert card.getPlayerId() == 0 : "Card should have correct player ID";
        
        // Test card deactivation
        card.deactivate();
        assert !card.isActive() : "Card should not be active after deactivation";
        assert card.getPlayerId() == -1 : "Card should have no owner after deactivation";
        
        System.out.println("✅ LongestRoad card basics test completed");
    }
    
    /**
     * Test PlayerManager longest road functionality
     */
    private void testPlayerManagerLongestRoad() {
        System.out.println("\n--- Test: PlayerManager Longest Road ---");
        
        PlayerManager playerManager = new PlayerManager();
        
        // Add test players
        playerManager.addPlayer("Spieler 1", Player.PlayerColor.RED);
        playerManager.addPlayer("Spieler 2", Player.PlayerColor.BLUE);
        playerManager.addPlayer("Spieler 3", Player.PlayerColor.WHITE);
        playerManager.startGame();
        
        // Test initial state
        Player longestRoadHolder = playerManager.getLongestRoadPlayer();
        assert longestRoadHolder == null : "Initially no one should have longest road";
        assert !playerManager.hasLongestRoadHolder() : "Should report no longest road holder initially";
        assert playerManager.getCurrentLongestRoadLength() == 0 : "Initial longest road length should be 0";
        
        // Create a simple edge list for testing
        List<Edge> testEdges = createTestEdges();
        
        // Test update with no roads
        boolean changed = playerManager.updateLongestRoad(testEdges);
        assert !changed : "Should not change when no roads are placed";
        
        System.out.println("Initial longest road holder: None (correct)");
        System.out.println("Current longest road length: " + playerManager.getCurrentLongestRoadLength());
        
        System.out.println("✅ PlayerManager longest road test completed");
    }
    
    /**
     * Create simple test edges for testing without GameField
     */
    private List<Edge> createTestEdges() {
        List<Edge> edges = new ArrayList<>();
        
        // Create simple nodes
        Node node1 = new Node(1, 0, 0);
        Node node2 = new Node(2, 10, 0);
        Node node3 = new Node(3, 20, 0);
        
        // Create edges connecting the nodes
        Edge edge1 = new Edge(1, node1, node2);
        Edge edge2 = new Edge(2, node2, node3);
        
        edges.add(edge1);
        edges.add(edge2);
        
        return edges;
    }
    
    /**
     * Test minimum road length requirement
     */
    private void testMinimumRoadLength() {
        System.out.println("\n--- Test: Minimum Road Length Requirement ---");
        
        int minLength = LongestRoad.getMinimumRoadLength();
        System.out.println("Minimum road length for award: " + minLength);
        assert minLength == 5 : "Minimum road length should be 5";
        
        // Test that a player with 4 roads doesn't get the award
        PlayerManager playerManager = new PlayerManager();
        playerManager.addPlayer("Test Player 1", Player.PlayerColor.RED);
        playerManager.addPlayer("Test Player 2", Player.PlayerColor.BLUE);
        playerManager.addPlayer("Test Player 3", Player.PlayerColor.WHITE);
        playerManager.startGame();
        
        Player testPlayer = playerManager.getCurrentPlayer();
        
        // Simulate a road length of 4 (below minimum)
        testPlayer.setLongestRoadLength(4);
        
        // Create empty edge list for testing
        List<Edge> edges = new ArrayList<>();
        
        // This should not award the longest road card
        boolean changed = playerManager.updateLongestRoad(edges);
        assert !changed : "Should not award card for roads below minimum length";
        assert playerManager.getLongestRoadPlayer() == null : "No player should have longest road card";
        
        System.out.println("✅ Minimum road length test completed");
    }
}
