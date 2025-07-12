package de.philx.catan.Controllers;

import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Hexagon;
import de.philx.catan.GameField.Edge;
import de.philx.catan.GameField.Node;
import de.philx.catan.GamePieces.Street;
import de.philx.catan.GamePieces.Settlement;
import de.philx.catan.GamePieces.City;
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
    
    // === Building Methods ===
    
    /**
     * Attempt to build a road on the specified edge
     * @param edgeId The ID of the edge where the road should be built
     * @return true if the road was built successfully
     */
    public boolean buildRoad(int edgeId) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            setGameMessage("Kein aktiver Spieler!");
            return false;
        }
        
        // Check if player can afford a road
        if (!currentPlayer.canBuildRoad()) {
            setGameMessage("Nicht genügend Ressourcen für eine Straße!");
            return false;
        }
        
        // Check if placement is valid
        if (!gameField.canPlaceRoad(edgeId, currentPlayer.getPlayerId())) {
            setGameMessage("Straße kann hier nicht gebaut werden!");
            return false;
        }
        
        // Build the road
        Edge edge = gameField.getEdge(edgeId);
        if (edge == null) {
            setGameMessage("Ungültige Kante!");
            return false;
        }
        
        // Pay resources and place road
        if (currentPlayer.buildRoad()) {
            Street road = new Street(currentPlayer.getPlayerId(), currentPlayer.getColor().charAt(0));
            edge.setRoad(road);
            
            // Update longest road calculations
            boolean longestRoadChanged = playerManager.updateLongestRoad(gameField.getEdges());
            
            setGameMessage("Straße gebaut!" + 
                (longestRoadChanged ? " Längste Handelsstraße hat sich geändert!" : ""));
            
            // Check for winner (in case longest road gave winning points)
            Player winner = playerManager.getWinner();
            if (winner != null) {
                setGameMessage("🎉 " + winner.getName() + " hat gewonnen! 🎉");
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Attempt to build a settlement on the specified node
     * @param nodeId The ID of the node where the settlement should be built
     * @return true if the settlement was built successfully
     */
    public boolean buildSettlement(int nodeId) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            setGameMessage("Kein aktiver Spieler!");
            return false;
        }
        
        // Check if player can afford a settlement
        if (!currentPlayer.canBuildSettlement()) {
            setGameMessage("Nicht genügend Ressourcen für eine Siedlung!");
            return false;
        }
        
        // Check if placement is valid
        if (!gameField.canPlaceSettlement(nodeId, currentPlayer.getPlayerId())) {
            setGameMessage("Siedlung kann hier nicht gebaut werden!");
            return false;
        }
        
        // Build the settlement
        Node node = gameField.getNode(nodeId);
        if (node == null) {
            setGameMessage("Ungültiger Knoten!");
            return false;
        }
        
        // Pay resources and place settlement
        if (currentPlayer.buildSettlement()) {
            Settlement settlement = new Settlement(currentPlayer.getPlayerId(), currentPlayer.getColor().charAt(0));
            node.setBuilding(settlement);
            
            setGameMessage("Siedlung gebaut!");
            
            // Check for winner
            Player winner = playerManager.getWinner();
            if (winner != null) {
                setGameMessage("🎉 " + winner.getName() + " hat gewonnen! 🎉");
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Attempt to upgrade a settlement to a city
     * @param nodeId The ID of the node where the city should be built
     * @return true if the city was built successfully
     */
    public boolean buildCity(int nodeId) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            setGameMessage("Kein aktiver Spieler!");
            return false;
        }
        
        // Check if player can afford a city
        if (!currentPlayer.canBuildCity()) {
            setGameMessage("Nicht genügend Ressourcen für eine Stadt!");
            return false;
        }
        
        // Check if upgrade is valid
        if (!gameField.canUpgradeToCity(nodeId, currentPlayer.getPlayerId())) {
            setGameMessage("Stadt kann hier nicht gebaut werden!");
            return false;
        }
        
        // Build the city
        Node node = gameField.getNode(nodeId);
        if (node == null) {
            setGameMessage("Ungültiger Knoten!");
            return false;
        }
        
        // Pay resources and place city
        if (currentPlayer.buildCity()) {
            City city = new City(currentPlayer.getPlayerId(), currentPlayer.getColor().charAt(0));
            node.setBuilding(city);
            
            setGameMessage("Stadt gebaut!");
            
            // Check for winner
            Player winner = playerManager.getWinner();
            if (winner != null) {
                setGameMessage("🎉 " + winner.getName() + " hat gewonnen! 🎉");
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Get longest road information for display
     * @return String with longest road information
     */
    public String getLongestRoadInfo() {
        Player longestRoadPlayer = playerManager.getLongestRoadPlayer();
        if (longestRoadPlayer == null) {
            return "Längste Handelsstraße: Niemand (min. 5 Straßen)";
        }
        
        return String.format("Längste Handelsstraße: %s (%d Straßen, +2 Siegpunkte)", 
            longestRoadPlayer.getName(), 
            playerManager.getCurrentLongestRoadLength());
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
        
        String longestRoadStatus = "";
        if (playerManager.getLongestRoadPlayer() == player) {
            longestRoadStatus = " (Längste Straße +2)";
        }
        
        return String.format("Straßen: %d, Siedlungen: %d, Städte: %d, Siegpunkte: %d%s",
            player.getAvailableRoads(),
            player.getAvailableSettlements(),
            player.getAvailableCities(),
            player.getVictoryPoints(),
            longestRoadStatus);
    }
}
