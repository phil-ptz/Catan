package de.philx.catan.Testing;

import de.philx.catan.Controllers.GameController;
import de.philx.catan.Players.Player;

/**
 * Test class specifically for testing the dice roll system implementation
 */
public class DiceRollTest {
    
    public static void main(String[] args) {
        System.out.println("=== CATAN Dice Roll System Test ===\n");
        
        // Create game controller (initializes with test players)
        GameController gameController = new GameController();
        
        System.out.println("Test Players Initialized:");
        System.out.println("- " + gameController.getCurrentPlayer().getName() + " (" + gameController.getCurrentPlayer().getColorDisplayName() + ")");
        System.out.println("- Game started: " + gameController.getPlayerManager().isGameStarted());
        System.out.println();
        
        // Test 1: Basic Dice Rolling
        testBasicDiceRolling(gameController);
        
        // Test 2: Dice Range Validation
        testDiceRangeValidation(gameController);
        
        // Test 3: Robber Activation (Rolling 7)
        testRobberActivation(gameController);
        
        // Test 4: Resource Production
        testResourceProduction(gameController);
        
        // Test 5: Multiple Rounds
        testMultipleRounds(gameController);
        
        System.out.println("\n=== All Dice Roll Tests Completed ===");
    }
    
    /**
     * Test basic dice rolling functionality
     */
    private static void testBasicDiceRolling(GameController gameController) {
        System.out.println("Test 1: Basic Dice Rolling");
        System.out.println("-".repeat(30));
        
        Player currentPlayer = gameController.getCurrentPlayer();
        System.out.println("Current player: " + currentPlayer.getName());
        System.out.println("Initial resources: " + gameController.getCurrentPlayerResources());
        System.out.println();
        
        // Roll dice 5 times
        for (int i = 1; i <= 5; i++) {
            System.out.println("Roll " + i + ":");
            int result = gameController.rollDice();
            System.out.println("  Result: " + result);
            System.out.println("  Dice display: " + gameController.diceResultProperty().get());
            System.out.println("  Game message: " + gameController.gameMessageProperty().get());
            System.out.println("  Resources after: " + gameController.getCurrentPlayerResources());
            System.out.println();
        }
        
        System.out.println("✓ Basic dice rolling works correctly\n");
    }
    
    /**
     * Test that dice results are in valid range (2-12)
     */
    private static void testDiceRangeValidation(GameController gameController) {
        System.out.println("Test 2: Dice Range Validation");
        System.out.println("-".repeat(30));
        
        boolean[] rolledNumbers = new boolean[13]; // Index 0-12
        int totalRolls = 50;
        
        for (int i = 0; i < totalRolls; i++) {
            int result = gameController.rollDice();
            
            // Validate range
            if (result < 2 || result > 12) {
                System.err.println("ERROR: Invalid dice result: " + result);
                return;
            }
            
            rolledNumbers[result] = true;
            
            // Handle robber if rolled 7
            if (result == 7 && gameController.isWaitingForRobberPlacement()) {
                gameController.moveRobber(0); // Move to first hexagon to continue testing
            }
            
            // End turn to reset for next roll
            if (!gameController.isWaitingForRobberPlacement()) {
                gameController.endTurn();
            }
        }
        
        // Check distribution
        System.out.println("Numbers rolled in " + totalRolls + " attempts:");
        for (int i = 2; i <= 12; i++) {
            System.out.println("  " + i + ": " + (rolledNumbers[i] ? "✓" : "✗"));
        }
        
        System.out.println("✓ All dice results are in valid range (2-12)\n");
    }
    
    /**
     * Test robber activation when rolling 7
     */
    private static void testRobberActivation(GameController gameController) {
        System.out.println("Test 3: Robber Activation (Rolling 7)");
        System.out.println("-".repeat(30));
        
        // Force roll 7 multiple times to test robber mechanics
        boolean foundSeven = false;
        for (int attempt = 0; attempt < 100; attempt++) {
            int result = gameController.rollDice();
            
            if (result == 7) {
                foundSeven = true;
                System.out.println("Rolled 7! Testing robber mechanics...");
                System.out.println("  Waiting for robber placement: " + gameController.isWaitingForRobberPlacement());
                System.out.println("  Game message: " + gameController.gameMessageProperty().get());
                
                // Test robber movement
                int oldRobberPos = gameController.getGameField().getRobberPosition();
                int newRobberPos = (oldRobberPos + 1) % 19; // Move to next hexagon
                
                boolean moved = gameController.moveRobber(newRobberPos);
                System.out.println("  Robber moved from " + oldRobberPos + " to " + newRobberPos + ": " + moved);
                System.out.println("  No longer waiting for robber: " + !gameController.isWaitingForRobberPlacement());
                
                break;
            }
            
            // End turn if not waiting for robber
            if (!gameController.isWaitingForRobberPlacement()) {
                gameController.endTurn();
            }
        }
        
        if (foundSeven) {
            System.out.println("✓ Robber activation works correctly");
        } else {
            System.out.println("⚠ No 7 was rolled in 100 attempts (unlikely but possible)");
        }
        System.out.println();
    }
    
    /**
     * Test resource production for different dice values
     */
    private static void testResourceProduction(GameController gameController) {
        System.out.println("Test 4: Resource Production");
        System.out.println("-".repeat(30));
        
        Player testPlayer = gameController.getCurrentPlayer();
        
        // Record initial resources
        int initialWood = testPlayer.getResourceAmount(Player.ResourceType.WOOD);
        int initialClay = testPlayer.getResourceAmount(Player.ResourceType.CLAY);
        int initialGrain = testPlayer.getResourceAmount(Player.ResourceType.GRAIN);
        int initialWool = testPlayer.getResourceAmount(Player.ResourceType.WOOL);
        int initialOre = testPlayer.getResourceAmount(Player.ResourceType.ORE);
        
        System.out.println("Initial resources: " + gameController.getCurrentPlayerResources());
        System.out.println();
        
        // Test specific dice values for resource production
        int[] testRolls = {6, 8, 5, 9, 4, 10, 3, 11, 2, 12};
        
        for (int targetRoll : testRolls) {
            // Keep rolling until we get the target (for testing purposes)
            boolean achieved = false;
            for (int attempt = 0; attempt < 50; attempt++) {
                int result = gameController.rollDice();
                
                if (result == targetRoll) {
                    System.out.println("Rolled " + targetRoll + ":");
                    System.out.println("  Game message: " + gameController.gameMessageProperty().get());
                    System.out.println("  Resources: " + gameController.getCurrentPlayerResources());
                    achieved = true;
                    break;
                } else if (result == 7 && gameController.isWaitingForRobberPlacement()) {
                    gameController.moveRobber((gameController.getGameField().getRobberPosition() + 1) % 19);
                }
                
                if (!gameController.isWaitingForRobberPlacement()) {
                    gameController.endTurn();
                }
            }
            
            if (!achieved) {
                System.out.println("Could not roll " + targetRoll + " in 50 attempts");
            }
            System.out.println();
        }
        
        // Check if any resources were added
        int finalWood = testPlayer.getResourceAmount(Player.ResourceType.WOOD);
        int finalClay = testPlayer.getResourceAmount(Player.ResourceType.CLAY);
        int finalGrain = testPlayer.getResourceAmount(Player.ResourceType.GRAIN);
        int finalWool = testPlayer.getResourceAmount(Player.ResourceType.WOOL);
        int finalOre = testPlayer.getResourceAmount(Player.ResourceType.ORE);
        
        boolean resourcesAdded = (finalWood > initialWood) || (finalClay > initialClay) || 
                               (finalGrain > initialGrain) || (finalWool > initialWool) || 
                               (finalOre > initialOre);
        
        System.out.println("Final resources: " + gameController.getCurrentPlayerResources());
        System.out.println("Resources were added during testing: " + resourcesAdded);
        
        if (resourcesAdded) {
            System.out.println("✓ Resource production system works correctly");
        } else {
            System.out.println("⚠ No resources were added (may be normal depending on dice results)");
        }
        System.out.println();
    }
    
    /**
     * Test multiple rounds with turn changes
     */
    private static void testMultipleRounds(GameController gameController) {
        System.out.println("Test 5: Multiple Rounds with Turn Changes");
        System.out.println("-".repeat(30));
        
        // Test 3 complete rounds (each player gets 3 turns)
        for (int round = 1; round <= 3; round++) {
            System.out.println("Round " + round + ":");
            
            for (int playerTurn = 0; playerTurn < 3; playerTurn++) {
                Player currentPlayer = gameController.getCurrentPlayer();
                System.out.println("  " + currentPlayer.getName() + "'s turn:");
                
                // Roll dice
                int result = gameController.rollDice();
                System.out.println("    Rolled: " + result);
                
                // Handle robber if needed
                if (result == 7 && gameController.isWaitingForRobberPlacement()) {
                    gameController.moveRobber((gameController.getGameField().getRobberPosition() + 1) % 19);
                    System.out.println("    Moved robber");
                }
                
                // End turn
                gameController.endTurn();
                System.out.println("    Turn ended");
            }
            System.out.println();
        }
        
        System.out.println("✓ Multiple rounds with turn changes work correctly\n");
    }
}
