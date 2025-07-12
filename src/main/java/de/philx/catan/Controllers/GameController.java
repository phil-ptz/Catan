package de.philx.catan.Controllers;

import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Hexagon;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.PlayerManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.List;
import java.util.Random;

/**
 * Main game controller that manages the game state and coordinates between
 * the game logic and the UI components.
 */
public class GameController {
    
    private final GameField gameField;
    private final PlayerManager playerManager;
    private final Random diceRandom;
    
    // Observable properties for UI binding
    private final StringProperty currentPlayerProperty;
    private final StringProperty diceResultProperty;
    private final StringProperty gameMessageProperty;
    
    // Game state
    private boolean waitingForRobberPlacement;
    private int lastDiceRoll;
    
    public GameController() {
        this.gameField = new GameField(50.0);
        this.playerManager = new PlayerManager();
        this.diceRandom = new Random();
        
        // Initialize observable properties
        this.currentPlayerProperty = new SimpleStringProperty("Spiel nicht gestartet");
        this.diceResultProperty = new SimpleStringProperty("");
        this.gameMessageProperty = new SimpleStringProperty("Willkommen bei Catan!");
        
        this.waitingForRobberPlacement = false;
        this.lastDiceRoll = 0;
        
        // Initialize test players for development
        initializeTestPlayers();
    }
    
    /**
     * Initialize test players for development purposes
     */
    private void initializeTestPlayers() {
        try {
            playerManager.addPlayer("Spieler 1", Player.PlayerColor.RED);
            playerManager.addPlayer("Spieler 2", Player.PlayerColor.BLUE);
            playerManager.addPlayer("Spieler 3", Player.PlayerColor.WHITE);
            playerManager.startGame();
            updateCurrentPlayerDisplay();
            setGameMessage("Spiel gestartet! " + getCurrentPlayer().getName() + " ist am Zug.");
        } catch (Exception e) {
            setGameMessage("Fehler beim Spielstart: " + e.getMessage());
        }
    }
    
    /**
     * Roll the dice and handle the resulting game logic
     * @return The dice roll result (2-12)
     */
    public int rollDice() {
        if (!playerManager.isGameStarted()) {
            setGameMessage("Spiel ist noch nicht gestartet!");
            return 0;
        }
        
        if (waitingForRobberPlacement) {
            setGameMessage("Bitte platziere zuerst den Räuber!");
            return lastDiceRoll;
        }
        
        // Roll two dice
        int dice1 = diceRandom.nextInt(6) + 1;
        int dice2 = diceRandom.nextInt(6) + 1;
        lastDiceRoll = dice1 + dice2;
        
        // Update dice display
        diceResultProperty.set(String.format("Würfel: %d + %d = %d", dice1, dice2, lastDiceRoll));
        
        // Handle dice result
        if (lastDiceRoll == 7) {
            handleRobberActivation();
        } else {
            handleResourceProduction(lastDiceRoll);
        }
        
        return lastDiceRoll;
    }
    
    /**
     * Handle robber activation when dice roll is 7
     */
    private void handleRobberActivation() {
        waitingForRobberPlacement = true;
        setGameMessage("Würfel 7! " + getCurrentPlayer().getName() + " muss den Räuber bewegen. Klicke auf ein Feld!");
    }
    
    /**
     * Handle resource production for non-7 dice rolls
     * @param diceRoll The dice roll result
     */
    private void handleResourceProduction(int diceRoll) {
        List<Integer> producingHexagons = gameField.produceResources(diceRoll);
        
        if (producingHexagons.isEmpty()) {
            setGameMessage("Keine Rohstoffproduktion für Würfel " + diceRoll);
        } else {
            // In a full implementation, this would distribute resources to players
            // based on their settlements and cities adjacent to producing hexagons
            setGameMessage(producingHexagons.size() + " Felder produzieren Rohstoffe für Würfel " + diceRoll);
            
            // Simulate resource distribution for current player (for demonstration)
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer != null) {
                // Simple simulation - give random resources based on dice roll
                distributeTestResources(currentPlayer, diceRoll);
            }
        }
    }
    
    /**
     * Simulate resource distribution for testing purposes
     * @param player The player to receive resources
     * @param diceRoll The dice roll that triggered production
     */
    private void distributeTestResources(Player player, int diceRoll) {
        // This is a simplified simulation for testing
        // In the full game, resources would be distributed based on 
        // settlements/cities adjacent to producing hexagons
        
        switch (diceRoll) {
            case 6, 8 -> {
                player.addResource(Player.ResourceType.WOOD, 1);
                player.addResource(Player.ResourceType.CLAY, 1);
            }
            case 5, 9 -> {
                player.addResource(Player.ResourceType.GRAIN, 1);
                player.addResource(Player.ResourceType.WOOL, 1);
            }
            case 4, 10 -> {
                player.addResource(Player.ResourceType.ORE, 1);
            }
            case 3, 11 -> {
                player.addResource(Player.ResourceType.WOOL, 1);
            }
            case 2, 12 -> {
                player.addResource(Player.ResourceType.GRAIN, 1);
            }
        }
    }
    
    /**
     * Move the robber to a specific hexagon
     * @param hexagonId The ID of the hexagon to move the robber to
     * @return true if the robber was moved successfully
     */
    public boolean moveRobber(int hexagonId) {
        if (!waitingForRobberPlacement) {
            setGameMessage("Der Räuber kann nur nach einer 7 bewegt werden!");
            return false;
        }
        
        Hexagon targetHex = gameField.getHexagon(hexagonId);
        if (targetHex == null) {
            setGameMessage("Ungültiges Feld ausgewählt!");
            return false;
        }
        
        if (gameField.getRobberPosition() == hexagonId) {
            setGameMessage("Der Räuber ist bereits auf diesem Feld!");
            return false;
        }
        
        // Move the robber
        gameField.moveRobber(hexagonId);
        waitingForRobberPlacement = false;
        
        setGameMessage("Räuber wurde auf " + targetHex.getTerrainType().toString().toLowerCase() + " bewegt.");
        
        return true;
    }
    
    /**
     * End the current player's turn and move to the next player
     */
    public void endTurn() {
        if (!playerManager.isGameStarted()) {
            setGameMessage("Spiel ist noch nicht gestartet!");
            return;
        }
        
        if (waitingForRobberPlacement) {
            setGameMessage("Bitte platziere zuerst den Räuber!");
            return;
        }
        
        Player nextPlayer = playerManager.nextTurn();
        updateCurrentPlayerDisplay();
        setGameMessage(nextPlayer.getName() + " ist jetzt am Zug.");
        
        // Clear dice result for new turn
        diceResultProperty.set("");
        
        // Check for winner
        Player winner = playerManager.getWinner();
        if (winner != null) {
            setGameMessage("🎉 " + winner.getName() + " hat gewonnen! 🎉");
        }
    }
    
    /**
     * Update the current player display
     */
    private void updateCurrentPlayerDisplay() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayerProperty.set(currentPlayer.getName() + " (" + currentPlayer.getColor() + ")");
        } else {
            currentPlayerProperty.set("Kein Spieler");
        }
    }
    
    /**
     * Set a game message
     * @param message The message to display
     */
    private void setGameMessage(String message) {
        gameMessageProperty.set(message);
        System.out.println("[GAME] " + message); // Also log to console
    }
    
    // Getters
    public GameField getGameField() {
        return gameField;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public Player getCurrentPlayer() {
        return playerManager.getCurrentPlayer();
    }
    
    public boolean isWaitingForRobberPlacement() {
        return waitingForRobberPlacement;
    }
    
    public int getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    // Observable properties for UI binding
    public StringProperty currentPlayerProperty() {
        return currentPlayerProperty;
    }
    
    public StringProperty diceResultProperty() {
        return diceResultProperty;
    }
    
    public StringProperty gameMessageProperty() {
        return gameMessageProperty;
    }
    
    // Resource information for current player
    public String getCurrentPlayerResources() {
        Player player = getCurrentPlayer();
        if (player == null) return "Keine Ressourcen";
        
        return String.format("Holz: %d, Lehm: %d, Getreide: %d, Wolle: %d, Erz: %d (Gesamt: %d)",
            player.getResourceAmount(Player.ResourceType.WOOD),
            player.getResourceAmount(Player.ResourceType.CLAY),
            player.getResourceAmount(Player.ResourceType.GRAIN),
            player.getResourceAmount(Player.ResourceType.WOOL),
            player.getResourceAmount(Player.ResourceType.ORE),
            player.getTotalResourceCards());
    }
    
    public String getCurrentPlayerBuildings() {
        Player player = getCurrentPlayer();
        if (player == null) return "Keine Gebäude";
        
        return String.format("Straßen: %d, Siedlungen: %d, Städte: %d, Siegpunkte: %d",
            player.getAvailableRoads(),
            player.getAvailableSettlements(),
            player.getAvailableCities(),
            player.getVictoryPoints());
    }
}
