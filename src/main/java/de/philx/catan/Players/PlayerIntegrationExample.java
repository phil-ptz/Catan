package de.philx.catan.Players;

import de.philx.catan.Players.Player.ResourceType;
import de.philx.catan.Players.Player.PlayerColor;

/**
 * Example integration showing how the enhanced Player Management system 
 * can be used within the CATAN game architecture.
 * 
 * This demonstrates typical game flow operations using the new Player class.
 */
public class PlayerIntegrationExample {
    
    private PlayerManager playerManager;
    
    public PlayerIntegrationExample() {
        this.playerManager = new PlayerManager();
    }
    
    /**
     * Example of setting up a new game with players
     */
    public void setupNewGame() {
        System.out.println("=== Setting up new CATAN game ===");
        
        // Add players
        playerManager.addPlayer("Alice", PlayerColor.RED);
        playerManager.addPlayer("Bob", PlayerColor.BLUE);
        playerManager.addPlayer("Charlie", PlayerColor.ORANGE);
        
        System.out.println("Added " + playerManager.getPlayerCount() + " players");
        
        // Start the game
        playerManager.startGame();
        System.out.println("Game started! Current player: " + playerManager.getCurrentPlayer().getName());
    }
    
    /**
     * Example of a typical turn sequence
     */
    public void exampleTurnSequence() {
        System.out.println("\n=== Example Turn Sequence ===");
        
        Player currentPlayer = playerManager.getCurrentPlayer();
        System.out.println(currentPlayer.getName() + "'s turn begins");
        
        // 1. Dice roll phase (simulated resource production)
        simulateResourceProduction(currentPlayer, 8); // Roll of 8
        
        // 2. Building phase
        System.out.println("Building phase:");
        attemptToBuild(currentPlayer);
        
        // 3. End turn
        Player nextPlayer = playerManager.nextTurn();
        System.out.println(currentPlayer.getName() + "'s turn ends, " + nextPlayer.getName() + "'s turn begins");
    }
    
    /**
     * Simulate resource production from dice roll
     */
    private void simulateResourceProduction(Player player, int diceRoll) {
        System.out.println("Dice roll: " + diceRoll);
        
        // Simulate resources based on settlements/cities near fields with this number
        // This would be calculated by the GameField system in the real implementation
        switch (diceRoll) {
            case 6, 8 -> {
                player.addResource(ResourceType.WOOD, 2);
                player.addResource(ResourceType.CLAY, 1);
                System.out.println("Received 2 Wood, 1 Clay");
            }
            case 5, 9 -> {
                player.addResource(ResourceType.GRAIN, 1);
                player.addResource(ResourceType.WOOL, 1);
                System.out.println("Received 1 Grain, 1 Wool");
            }
            case 10, 11 -> {
                player.addResource(ResourceType.ORE, 1);
                System.out.println("Received 1 Ore");
            }
            default -> System.out.println("No resources produced this turn");
        }
        
        System.out.println("Total resources: " + player.getTotalResourceCards());
    }
    
    /**
     * Attempt to build something with current resources
     */
    private void attemptToBuild(Player player) {
        // Try to build in order of priority: settlement > road > city
        
        if (player.canBuildSettlement()) {
            player.buildSettlement();
            System.out.println("Built a settlement! Victory points: " + player.getVictoryPoints());
        } else if (player.canBuildRoad()) {
            player.buildRoad();
            System.out.println("Built a road! Roads remaining: " + player.getAvailableRoads());
        } else if (player.canBuildCity()) {
            player.buildCity();
            System.out.println("Upgraded to city! Victory points: " + player.getVictoryPoints());
        } else {
            System.out.println("Cannot afford any buildings this turn");
            displayResourceStatus(player);
        }
    }
    
    /**
     * Display current resource status
     */
    private void displayResourceStatus(Player player) {
        System.out.printf("Resources - Wood:%d Clay:%d Grain:%d Wool:%d Ore:%d%n",
            player.getResourceAmount(ResourceType.WOOD),
            player.getResourceAmount(ResourceType.CLAY),
            player.getResourceAmount(ResourceType.GRAIN),
            player.getResourceAmount(ResourceType.WOOL),
            player.getResourceAmount(ResourceType.ORE));
    }
    
    /**
     * Example of checking win condition
     */
    public void checkWinCondition() {
        Player winner = playerManager.getWinner();
        if (winner != null) {
            System.out.println("\n🎉 " + winner.getName() + " wins with " + winner.getVictoryPoints() + " victory points!");
            displayFinalStandings();
        }
    }
    
    /**
     * Display final standings
     */
    private void displayFinalStandings() {
        System.out.println("\nFinal standings:");
        var playersByVP = playerManager.getPlayersByVictoryPoints();
        for (int i = 0; i < playersByVP.size(); i++) {
            Player p = playersByVP.get(i);
            System.out.printf("%d. %s - %d VP (%d settlements, %d cities)%n", 
                i + 1, p.getName(), p.getVictoryPoints(), 
                p.getPlacedSettlements(), p.getPlacedCities());
        }
    }
    
    /**
     * Example of trading between players (simplified)
     */
    public void exampleTrading() {
        System.out.println("\n=== Example Trading ===");
        
        Player player1 = playerManager.getPlayerById(0);
        Player player2 = playerManager.getPlayerById(1);
        
        if (player1 != null && player2 != null) {
            // Give some resources to demonstrate trading
            player1.addResource(ResourceType.WOOD, 3);
            player2.addResource(ResourceType.GRAIN, 2);
            
            System.out.println(player1.getName() + " wants to trade 2 Wood for 1 Grain");
            
            // Check if trade is possible
            if (player1.getResourceAmount(ResourceType.WOOD) >= 2 && 
                player2.getResourceAmount(ResourceType.GRAIN) >= 1) {
                
                // Execute trade
                player1.removeResource(ResourceType.WOOD, 2);
                player1.addResource(ResourceType.GRAIN, 1);
                
                player2.removeResource(ResourceType.GRAIN, 1);
                player2.addResource(ResourceType.WOOD, 2);
                
                System.out.println("Trade completed!");
                System.out.println(player1.getName() + " now has " + 
                    player1.getResourceAmount(ResourceType.GRAIN) + " Grain");
                System.out.println(player2.getName() + " now has " + 
                    player2.getResourceAmount(ResourceType.WOOD) + " Wood");
            } else {
                System.out.println("Trade not possible - insufficient resources");
            }
        }
    }
    
    /**
     * Main demonstration method
     */
    public static void main(String[] args) {
        PlayerIntegrationExample example = new PlayerIntegrationExample();
        
        // Setup game
        example.setupNewGame();
        
        // Simulate a few turns
        for (int turn = 1; turn <= 3; turn++) {
            System.out.println("\n--- Turn " + turn + " ---");
            example.exampleTurnSequence();
            example.checkWinCondition();
        }
        
        // Example trading
        example.exampleTrading();
        
        // Show game statistics
        System.out.println("\nGame Statistics:");
        System.out.println(example.playerManager.getGameStatistics());
    }
}
