package de.philx.catan.Controllers;

import de.philx.catan.GameField.Edge;
import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Hexagon;
import de.philx.catan.GameField.Node;
import de.philx.catan.GamePieces.City;
import de.philx.catan.GamePieces.Settlement;
import de.philx.catan.GamePieces.Street;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.PlayerManager;
import java.util.List;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Main game controller that manages the game state and coordinates between
 * the game logic and the UI components.
 */
public class GameController {
    
    private final GameField gameField;
    private final PlayerManager playerManager;
    private final TradeController tradeController;
    private final Random diceRandom;
    
    // Observable properties for UI binding
    private final StringProperty currentPlayerProperty;
    private final StringProperty diceResultProperty;
    private final StringProperty gameMessageProperty;
    
    // Game state
    private boolean waitingForRobberPlacement;
    private int lastDiceRoll;
    private BuildMode currentBuildingMode; // null, ROAD, SETTLEMENT, CITY, SETUP_ROAD
    private boolean buildingModeActive;
    
    // Track last settlement for road validation in setup phase
    private int lastPlacedSettlementNodeId;

    public GameController() {
        this.gameField = new GameField(50.0);
        this.playerManager = new PlayerManager();
        this.tradeController = new TradeController(playerManager);
        this.diceRandom = new Random();
        
        // Initialize observable properties
        this.currentPlayerProperty = new SimpleStringProperty("Spiel nicht gestartet");
        this.diceResultProperty = new SimpleStringProperty("");
        this.gameMessageProperty = new SimpleStringProperty("Willkommen bei Catan!");
        
        this.waitingForRobberPlacement = false;
        this.lastDiceRoll = 0;
        this.currentBuildingMode = null;
        this.buildingModeActive = false;
        this.lastPlacedSettlementNodeId = -1;
    }
    
    /**
     * Start a test game with pre-configured test players
     * This method is intended for development and testing purposes
     */
    public void startTestGame() {
        System.out.println("[DEBUG] Testspiel gestartet.");
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
            
            // Setup phase is automatically started by PlayerManager
            updateCurrentPlayerDisplay();
            Player currentPlayer = playerManager.getCurrentPlayer();
            setGameMessage("🏗️ AUFBAUPHASE STARTET! Jeder Spieler platziert 2 Siedlungen und 2 Straßen kostenlos.\n" +
                          "RUNDE 1: " + currentPlayer.getName() + 
                          " (" + currentPlayer.getColorDisplayName() + ") platziere deine erste Siedlung!");
        } catch (Exception e) {
            setGameMessage("Fehler beim Spielstart: " + e.getMessage());
        }
    }
    
    /**
     * Get the current player during setup phase
     */
    private Player getCurrentSetupPlayer() {
        return playerManager.getCurrentPlayer();
    }
    
    /**
     * Handle settlement placement during setup phase
     */
    public boolean placeSetupSettlement(int nodeId) {
        if (!playerManager.isSetupPhase()) {
            setGameMessage("Nicht in der Aufbauphase!");
            return false;
        }
        
        Node node = gameField.getNode(nodeId);
        if (node == null || !node.isValidForSettlement()) {
            setGameMessage("Ungültige Position für Siedlung!");
            return false;
        }
        
        Player currentPlayer = getCurrentSetupPlayer();
        
        // Place settlement for free during setup
        Settlement settlement = new Settlement(currentPlayer.getPlayerId(), currentPlayer.getColorDisplayName().charAt(0));
        node.setBuilding(settlement);
        currentPlayer.placeSettlement(); // This updates the count but doesn't charge resources
        
        // Track the settlement node for road validation
        lastPlacedSettlementNodeId = nodeId;
        
        setGameMessage("✅ Siedlung platziert! " + currentPlayer.getName() + 
                      " (" + currentPlayer.getColorDisplayName() + ") platziere jetzt deine dazugehörige Straße!");
        startBuildingMode(BuildMode.SETUP_ROAD);
        
        return true;
    }
    
    /**
     * Handle road placement during setup phase
     */
    public boolean placeSetupRoad(int edgeId) {
        if (!playerManager.isSetupPhase()) {
            setGameMessage("Nicht in der Aufbauphase!");
            return false;
        }
        
        Edge edge = gameField.getEdge(edgeId);
        if (edge == null || edge.hasRoad()) {
            setGameMessage("Ungültige Position für Straße!");
            return false;
        }
        
        // Check if road is connected to the settlement that was just placed
        Node settlementNode = gameField.getNode(lastPlacedSettlementNodeId);
        if (settlementNode == null || 
            (!edge.getNode1().equals(settlementNode) && !edge.getNode2().equals(settlementNode))) {
            setGameMessage("Straße muss an die gerade platzierte Siedlung anschließen!");
            return false;
        }
        
        Player currentPlayer = getCurrentSetupPlayer();
        
        // Place road for free during setup
        Street road = new Street(currentPlayer.getPlayerId(), currentPlayer.getColorDisplayName().charAt(0));
        edge.setRoad(road);
        currentPlayer.placeRoad(); // This updates the count but doesn't charge resources
        
        // Give resources for second settlement
        if (playerManager.getSetupRound() == 2) {
            giveStartingResources(currentPlayer, lastPlacedSettlementNodeId);
        }
        
        // Move to next player or finish setup - delegate to PlayerManager
        boolean setupContinues = playerManager.advanceSetupPhase();
        if (!setupContinues) {
            finishSetupPhase();
        } else {
            // Update display for next player
            Player nextPlayer = playerManager.getCurrentPlayer();
            String phaseText = playerManager.getSetupRound() == 1 ? "RUNDE 1" : "RUNDE 2";
            String buildingCount = playerManager.getSetupRound() == 1 ? "erste" : "zweite";
            String roundInfo = playerManager.getSetupRound() == 1 ? "(alle Spieler in Reihenfolge)" : "(alle Spieler in Rückwärts-Reihenfolge)";
            
            setGameMessage("🏗️ AUFBAUPHASE " + phaseText + " " + roundInfo + ":\n" + 
                          nextPlayer.getName() + " (" + nextPlayer.getColorDisplayName() + ") platziere deine " + 
                          buildingCount + " Siedlung!");
            updateCurrentPlayerDisplay();
        }
        
        stopBuildingMode();
        return true;
    }
    
    /**
     * Give starting resources for the second settlement placement
     */
    private void giveStartingResources(Player player, int settlementNodeId) {
        Node settlementNode = gameField.getNode(settlementNodeId);
        if (settlementNode == null) return;
        
        for (Hexagon hex : settlementNode.getAdjacentHexagons()) {
            if (hex.getTerrainType() != de.philx.catan.GameField.TerrainType.DESERT && 
                !hex.hasRobber()) {
                
                Player.ResourceType resourceType = getResourceTypeFromTerrain(hex.getTerrainType());
                if (resourceType != null) {
                    player.addResource(resourceType, 1);
                }
            }
        }
        
        setGameMessage(player.getName() + " erhält Startressourcen!");
    }
    
    /**
     * Convert terrain type to resource type
     */
    private Player.ResourceType getResourceTypeFromTerrain(de.philx.catan.GameField.TerrainType terrain) {
        switch (terrain) {
            case FOREST: return Player.ResourceType.WOOD;
            case HILLS: return Player.ResourceType.CLAY;
            case FIELDS: return Player.ResourceType.GRAIN;
            case PASTURE: return Player.ResourceType.WOOL;
            case MOUNTAINS: return Player.ResourceType.ORE;
            default: return null;
        }
    }
    
    /**
     * Finish the setup phase and start normal gameplay
     */
    private void finishSetupPhase() {
        // PlayerManager already handles setup phase transition
        updateCurrentPlayerDisplay();
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            setGameMessage("🎉 AUFBAUPHASE BEENDET! Alle Spieler haben 2 Siedlungen und 2 Straßen platziert.\n" +
                          "Das normale Spiel beginnt: " + currentPlayer.getName() + 
                          " (" + currentPlayer.getColorDisplayName() + ") würfle!");
        } else {
            setGameMessage("🎉 AUFBAUPHASE BEENDET! Alle Spieler haben 2 Siedlungen und 2 Straßen platziert.\n" +
                          "Das normale Spiel beginnt!");
        }
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
        
        // Check if placement is valid
        if (!gameField.canPlaceRoad(edgeId, currentPlayer.getPlayerId())) {
            setGameMessage("Straße kann hier nicht gebaut werden!");
            return false;
        }
        
        Edge edge = gameField.getEdge(edgeId);
        if (edge == null) {
            setGameMessage("Ungültige Kante!");
            return false;
        }
        
        boolean success;
        if (playerManager.isSetupPhase()) {
            success = handleRoadPlacementInSetup(currentPlayer, edge);
        } else {
            success = handleRoadPlacementInGame(currentPlayer, edge);
        }
        
        if (success) {
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
     * Handle road placement during setup phase
     * @param player The current player
     * @param edge The edge to place the road on
     * @return true if the road was placed successfully
     */
    private boolean handleRoadPlacementInSetup(Player player, Edge edge) {
        boolean success = player.buildRoadSetup();
        if (success) {
            Street road = new Street(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            edge.setRoad(road);
            setGameMessage("Straße in der Startphase platziert!");
            
            // Advance setup phase
            if (!playerManager.advanceSetupPhase()) {
                setGameMessage("Startphase beendet! Das normale Spiel beginnt.");
            }
        }
        return success;
    }
    
    /**
     * Handle road placement during normal game phase
     * @param player The current player
     * @param edge The edge to place the road on
     * @return true if the road was placed successfully
     */
    private boolean handleRoadPlacementInGame(Player player, Edge edge) {
        // Check if player can afford a road
        if (!player.canBuildRoad()) {
            setGameMessage("Nicht genügend Ressourcen für eine Straße!");
            return false;
        }
        
        // Pay resources and place road
        boolean success = player.buildRoad();
        if (success) {
            Street road = new Street(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            edge.setRoad(road);
            
            // Update longest road calculations
            boolean longestRoadChanged = playerManager.updateLongestRoad(gameField.getEdges());
            
            setGameMessage("Straße gebaut!" + 
                (longestRoadChanged ? " Längste Handelsstraße hat sich geändert!" : ""));
        }
        return success;
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
        
        // Check if placement is valid
        if (!gameField.canPlaceSettlement(nodeId, currentPlayer.getPlayerId())) {
            setGameMessage("Siedlung kann hier nicht gebaut werden!");
            return false;
        }
        
        Node node = gameField.getNode(nodeId);
        if (node == null) {
            setGameMessage("Ungültiger Knoten!");
            return false;
        }
        
        boolean success;
        if (playerManager.isSetupPhase()) {
            success = handleSettlementPlacementInSetup(currentPlayer, node);
        } else {
            success = handleSettlementPlacementInGame(currentPlayer, node);
        }
        
        if (success) {
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
     * Handle settlement placement during setup phase
     * @param player The current player
     * @param node The node to place the settlement on
     * @return true if the settlement was placed successfully
     */
    private boolean handleSettlementPlacementInSetup(Player player, Node node) {
        boolean success = player.buildSettlementSetup();
        if (success) {
            Settlement settlement = new Settlement(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            node.setBuilding(settlement);
            setGameMessage("Siedlung in der Startphase platziert!");
            
            // Advance setup phase
            if (!playerManager.advanceSetupPhase()) {
                setGameMessage("Startphase beendet! Das normale Spiel beginnt.");
            }
        }
        return success;
    }
    
    /**
     * Handle settlement placement during normal game phase
     * @param player The current player
     * @param node The node to place the settlement on
     * @return true if the settlement was placed successfully
     */
    private boolean handleSettlementPlacementInGame(Player player, Node node) {
        // Check if player can afford a settlement
        if (!player.canBuildSettlement()) {
            setGameMessage("Nicht genügend Ressourcen für eine Siedlung!");
            return false;
        }
        
        // Pay resources and place settlement
        boolean success = player.buildSettlement();
        if (success) {
            Settlement settlement = new Settlement(player.getPlayerId(), player.getColorDisplayName().charAt(0));
            node.setBuilding(settlement);
            setGameMessage("Siedlung gebaut!");
        }
        return success;
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
            City city = new City(currentPlayer.getPlayerId(), currentPlayer.getColorDisplayName().charAt(0));
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
    
    /**
     * Check if the current player can roll dice
     * @return true if the current player can roll dice
     */
    public boolean canCurrentPlayerRollDice() {
        if (!playerManager.isGameStarted() || waitingForRobberPlacement) {
            return false;
        }
        
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer != null && !currentPlayer.hasRolledDice();
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
        if (player == null) {
            return "Kein Spieler aktiv";
        }
        
        return String.format("Holz: %d, Lehm: %d, Getreide: %d, Wolle: %d, Erz: %d", 
            player.getResourceAmount(Player.ResourceType.WOOD),
            player.getResourceAmount(Player.ResourceType.CLAY),
            player.getResourceAmount(Player.ResourceType.GRAIN),
            player.getResourceAmount(Player.ResourceType.WOOL),
            player.getResourceAmount(Player.ResourceType.ORE));
    }
    
    public String getCurrentPlayerBuildings() {
        Player player = getCurrentPlayer();
        if (player == null) {
            return "Kein Spieler aktiv";
        }
        
        int placedRoads = 15 - player.getAvailableRoads(); // Calculate placed roads
        return String.format("Straßen: %d, Siedlungen: %d, Städte: %d, Punkte: %d", 
            placedRoads, player.getPlacedSettlements(), 
            player.getPlacedCities(), player.getVictoryPoints());
    }
    
    /**
     * Start building mode for placing buildings
     * @param buildingType The type of building to place ("road", "settlement", "city", "setup_road")
     */
    public void startBuildingMode(String buildingType) {
        this.currentBuildingMode = convertStringToBuildMode(buildingType);
        this.buildingModeActive = true;
        setGameMessage("Klicke auf eine gültige Position um " + getBuildingTypeName(this.currentBuildingMode) + " zu platzieren.");
    }
    
    /**
     * Start building mode for placing buildings using BuildMode enum
     * @param buildMode The BuildMode enum to set
     */
    public void startBuildingMode(BuildMode buildMode) {
        this.currentBuildingMode = buildMode;
        this.buildingModeActive = true;
        setGameMessage("Klicke auf eine gültige Position um " + getBuildingTypeName(buildMode) + " zu platzieren.");
    }
    
    /**
     * Convert string building type to BuildMode enum
     * @param buildingType The string building type
     * @return The corresponding BuildMode enum value
     */
    private BuildMode convertStringToBuildMode(String buildingType) {
        switch (buildingType) {
            case "road": return BuildMode.ROAD;
            case "settlement": return BuildMode.SETTLEMENT;
            case "city": return BuildMode.CITY;
            case "setup_road": return BuildMode.SETUP_ROAD;
            default: return null;
        }
    }
    
    /**
     * Get the German name for a BuildMode
     * @param buildMode The BuildMode enum
     * @return German name for the building type
     */
    private String getBuildingTypeName(BuildMode buildMode) {
        if (buildMode == null) return "ein Gebäude";
        
        switch (buildMode) {
            case ROAD:
            case SETUP_ROAD:
                return "eine Straße";
            case SETTLEMENT:
                return "eine Siedlung";
            case CITY:
                return "eine Stadt";
            default:
                return "ein Gebäude";
        }
    }
    
    /**
     * Stop building mode
     */
    public void stopBuildingMode() {
        this.currentBuildingMode = null;
        this.buildingModeActive = false;
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            setGameMessage(currentPlayer.getName() + " ist am Zug.");
        } else {
            setGameMessage("Spiel ist bereit.");
        }
    }
    
    /**
     * Check if currently in building mode
     * @return true if building mode is active
     */
    public boolean isBuildingModeActive() {
        return buildingModeActive;
    }
    
    /**
     * Get the current building mode
     * @return current building mode or null
     */
    public BuildMode getCurrentBuildingMode() {
        return currentBuildingMode;
    }
    
    /**
     * Handle resource production for non-7 dice rolls with proper distribution
     * @param diceRoll The dice roll result
     */
    private void handleResourceProduction(int diceRoll) {
        List<Integer> producingHexagons = gameField.produceResources(diceRoll);
        
        if (producingHexagons.isEmpty()) {
            setGameMessage("Keine Rohstoffproduktion für Würfel " + diceRoll);
            return;
        }
        
        int totalResourcesProduced = 0;
        StringBuilder productionMessage = new StringBuilder();
        
        // Distribute resources to all players based on their settlements/cities
        for (int hexId : producingHexagons) {
            Hexagon hex = gameField.getHexagon(hexId);
            if (hex == null || hex.hasRobber()) continue;
            
            Player.ResourceType resourceType = getResourceTypeFromTerrain(hex.getTerrainType());
            if (resourceType == null) continue;
            
            // Check each adjacent node for settlements/cities
            for (Node node : hex.getAdjacentNodes()) {
                if (node.hasBuilding()) {
                    int playerId = node.getBuilding().getPlayerId();
                    Player player = playerManager.getPlayerById(playerId);
                    
                    if (player != null) {
                        int resourceAmount = node.hasCity() ? 2 : 1; // Cities produce 2, settlements produce 1
                        player.addResource(resourceType, resourceAmount);
                        totalResourcesProduced += resourceAmount;
                        
                        if (productionMessage.length() > 0) productionMessage.append(", ");
                        productionMessage.append(player.getName()).append(" +").append(resourceAmount)
                                       .append(" ").append(getResourceName(resourceType));
                    }
                }
            }
        }
        
        if (totalResourcesProduced > 0) {
            setGameMessage("Würfel " + diceRoll + ": " + productionMessage.toString());
        } else {
            setGameMessage("Würfel " + diceRoll + ": Keine Rohstoffe produziert");
        }
    }
    
    /**
     * Get display name for resource type
     */
    private String getResourceName(Player.ResourceType resourceType) {
        switch (resourceType) {
            case WOOD: return "Holz";
            case CLAY: return "Lehm";
            case GRAIN: return "Getreide";
            case WOOL: return "Wolle";
            case ORE: return "Erz";
            default: return "Unbekannt";
        }
    }
    
    /**
     * Roll the dice and handle the resulting game logic
     * @return The dice roll result (2-12)
     */
    public int rollDice() {
        if (playerManager.isSetupPhase()) {
            setGameMessage("Erst die Aufbauphase beenden!");
            return 0;
        }
        
        if (!playerManager.isGameStarted()) {
            setGameMessage("Spiel ist noch nicht gestartet!");
            return 0;
        }
        
        if (waitingForRobberPlacement) {
            setGameMessage("Bitte platziere zuerst den Räuber!");
            return lastDiceRoll;
        }
        
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            setGameMessage("Kein aktiver Spieler!");
            return 0;
        }
        
        // Check if player has already rolled dice this turn
        if (currentPlayer.hasRolledDice()) {
            setGameMessage(currentPlayer.getName() + " hat bereits gewürfelt! Beende deinen Zug.");
            return lastDiceRoll;
        }
        
        // Roll two dice
        int dice1 = diceRandom.nextInt(6) + 1;
        int dice2 = diceRandom.nextInt(6) + 1;
        lastDiceRoll = dice1 + dice2;
        
        // Mark that player has rolled dice this turn
        currentPlayer.setHasRolledDice(true);
        
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
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            setGameMessage("Würfel 7! " + currentPlayer.getName() + " muss den Räuber bewegen. Klicke auf ein Feld!");
        } else {
            setGameMessage("Würfel 7! Der Räuber muss bewegt werden. Klicke auf ein Feld!");
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
        if (playerManager.isSetupPhase()) {
            Player setupPlayer = getCurrentSetupPlayer();
            String phase = playerManager.getSetupRound() == 1 ? "Aufbauphase 1/2" : "Aufbauphase 2/2";
            currentPlayerProperty.set("🏗️ " + phase + " - " + setupPlayer.getName() + " (" + setupPlayer.getColorDisplayName() + ") ist dran");
        } else {
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayerProperty.set("🎮 " + currentPlayer.getName() + " (" + currentPlayer.getColorDisplayName() + ") ist am Zug");
            } else {
                currentPlayerProperty.set("Kein Spieler");
            }
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
    
    /**
     * Handle clicks on building placement (nodes or edges)
     * @param elementId The ID of the clicked element (node_X or edge_X)
     * @return true if building was placed successfully
     */
    public boolean handleBuildingPlacement(String elementId) {
        if (playerManager.isSetupPhase()) {
            return handleSetupBuildingPlacement(elementId);
        }
        
        if (!buildingModeActive || currentBuildingMode == null) {
            return false;
        }
        
        boolean success = false;
        
        if (elementId.startsWith("node_") && (currentBuildingMode == BuildMode.SETTLEMENT || currentBuildingMode == BuildMode.CITY)) {
            int nodeId = Integer.parseInt(elementId.substring(5));
            if (currentBuildingMode == BuildMode.SETTLEMENT) {
                success = buildSettlement(nodeId);
            } else if (currentBuildingMode == BuildMode.CITY) {
                success = buildCity(nodeId);
            }
        } else if (elementId.startsWith("edge_") && currentBuildingMode == BuildMode.ROAD) {
            int edgeId = Integer.parseInt(elementId.substring(5));
            success = buildRoad(edgeId);
        } else {
            System.err.println("[WARN] Ungültiger elementId: " + elementId);
        }
        
        if (success) {
            stopBuildingMode();
        }
        
        return success;
    }
    
    /**
     * Handle building placement during setup phase
     */
    private boolean handleSetupBuildingPlacement(String elementId) {
        if (currentBuildingMode == BuildMode.SETUP_ROAD && elementId.startsWith("edge_")) {
            int edgeId = Integer.parseInt(elementId.substring(5));
            return placeSetupRoad(edgeId);
        } else if (elementId.startsWith("node_")) {
            int nodeId = Integer.parseInt(elementId.substring(5));
            return placeSetupSettlement(nodeId);
        }
        
        return false;
    }
    
    // Getter for setup phase status
    public boolean isInSetupPhase() {
        return playerManager.isSetupPhase();
    }
    
    // === Enhanced Building and Trading Methods ===
    
    /**
     * Get the trade controller for handling trades
     * @return The trade controller instance
     */
    public TradeController getTradeController() {
        return tradeController;
    }
}
