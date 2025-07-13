package de.philx.catan.Testing;

import de.philx.catan.Controllers.GameController;
import de.philx.catan.Players.Player;

/**
 * Test class to verify that dice rolling is restricted to once per turn
 */
public class DiceRollRestrictionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Dice Roll Restriction Test ===\n");
        
        // Create game controller (initializes with test players)
        GameController gameController = new GameController();
        
        testSingleDiceRollPerTurn(gameController);
        testDiceRollAfterTurnChange(gameController);
        testButtonStateManagement(gameController);
        
        System.out.println("=== All Dice Roll Restriction Tests Completed ===");
    }
    
    /**
     * Test that a player can only roll dice once per turn
     */
    private static void testSingleDiceRollPerTurn(GameController gameController) {
        System.out.println("Test 1: Single Dice Roll Per Turn");
        System.out.println("-".repeat(40));
        
        Player currentPlayer = gameController.getCurrentPlayer();
        System.out.println("Current player: " + currentPlayer.getName());
        System.out.println("Has rolled dice: " + currentPlayer.hasRolledDice());
        System.out.println("Can roll dice: " + gameController.canCurrentPlayerRollDice());
        System.out.println();
        
        // First dice roll should work
        System.out.println("Attempting first dice roll:");
        int firstRoll = gameController.rollDice();
        System.out.println("  Result: " + firstRoll);
        System.out.println("  Has rolled dice: " + currentPlayer.hasRolledDice());
        System.out.println("  Can roll dice: " + gameController.canCurrentPlayerRollDice());
        System.out.println("  Game message: " + gameController.gameMessageProperty().get());
        System.out.println();
        
        // Second dice roll should be blocked
        System.out.println("Attempting second dice roll (should be blocked):");
        int secondRoll = gameController.rollDice();
        System.out.println("  Result: " + secondRoll);
        System.out.println("  Has rolled dice: " + currentPlayer.hasRolledDice());
        System.out.println("  Can roll dice: " + gameController.canCurrentPlayerRollDice());
        System.out.println("  Game message: " + gameController.gameMessageProperty().get());
        
        // Verify that the dice result didn't change
        if (firstRoll == secondRoll && !gameController.canCurrentPlayerRollDice()) {
            System.out.println("✓ Dice roll restriction working correctly");
        } else {
            System.out.println("✗ Dice roll restriction failed!");
        }
        System.out.println();
    }
    
    /**
     * Test that dice rolling is enabled again after turn change
     */
    private static void testDiceRollAfterTurnChange(GameController gameController) {
        System.out.println("Test 2: Dice Roll After Turn Change");
        System.out.println("-".repeat(40));
        
        Player playerBeforeTurn = gameController.getCurrentPlayer();
        System.out.println("Player before turn change: " + playerBeforeTurn.getName());
        System.out.println("Has rolled dice: " + playerBeforeTurn.hasRolledDice());
        
        // Handle robber if needed (if last roll was 7)
        if (gameController.isWaitingForRobberPlacement()) {
            gameController.moveRobber((gameController.getGameField().getRobberPosition() + 1) % 19);
            System.out.println("Moved robber to continue test");
        }
        
        // End turn
        gameController.endTurn();
        Player playerAfterTurn = gameController.getCurrentPlayer();
        
        System.out.println("Player after turn change: " + playerAfterTurn.getName());
        System.out.println("Has rolled dice: " + playerAfterTurn.hasRolledDice());
        System.out.println("Can roll dice: " + gameController.canCurrentPlayerRollDice());
        System.out.println();
        
        // New player should be able to roll dice
        System.out.println("Attempting dice roll with new player:");
        int newRoll = gameController.rollDice();
        System.out.println("  Result: " + newRoll);
        System.out.println("  Has rolled dice: " + playerAfterTurn.hasRolledDice());
        System.out.println("  Can roll dice: " + gameController.canCurrentPlayerRollDice());
        
        if (newRoll > 0 && playerAfterTurn.hasRolledDice() && !gameController.canCurrentPlayerRollDice()) {
            System.out.println("✓ Dice rolling resets correctly after turn change");
        } else {
            System.out.println("✗ Dice rolling reset failed after turn change!");
        }
        System.out.println();
    }
    
    /**
     * Test button state management
     */
    private static void testButtonStateManagement(GameController gameController) {
        System.out.println("Test 3: Button State Management");
        System.out.println("-".repeat(40));
        
        Player currentPlayer = gameController.getCurrentPlayer();
        
        // Test when player hasn't rolled yet
        boolean canRollBeforeRoll = gameController.canCurrentPlayerRollDice();
        System.out.println("Can roll dice before rolling: " + canRollBeforeRoll);
        
        // Handle robber if needed
        if (gameController.isWaitingForRobberPlacement()) {
            gameController.moveRobber((gameController.getGameField().getRobberPosition() + 1) % 19);
        }
        
        // End current turn if player has already rolled
        if (currentPlayer.hasRolledDice()) {
            gameController.endTurn();
            currentPlayer = gameController.getCurrentPlayer();
        }
        
        // Roll dice
        gameController.rollDice();
        
        // Test when player has rolled
        boolean canRollAfterRoll = gameController.canCurrentPlayerRollDice();
        System.out.println("Can roll dice after rolling: " + canRollAfterRoll);
        
        if (canRollBeforeRoll && !canRollAfterRoll) {
            System.out.println("✓ Button state management working correctly");
        } else {
            System.out.println("✗ Button state management failed!");
            System.out.println("  Before roll: " + canRollBeforeRoll + ", After roll: " + canRollAfterRoll);
        }
        System.out.println();
    }
}
