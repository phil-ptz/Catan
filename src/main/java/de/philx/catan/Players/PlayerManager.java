package de.philx.catan.Players;

import de.philx.catan.Cards.Special.LongestRoad;
import de.philx.catan.GameField.Edge;
import java.util.*;

/**
 * Manages all players in a Catan game.
 * Handles player creation, turn order, validation, and game-wide player operations.
 * 
 * Supports 3-4 players with unique names and colors as per 1995 Catan rules.
 */
public class PlayerManager {
    
    private final List<Player> players;
    private int currentPlayerIndex;
    private final int maxPlayers;
    private final int minPlayers;
    private boolean gameStarted;
    
    // Longest Road tracking
    private LongestRoad longestRoadCard;
    private int currentLongestRoadPlayerId;
    private int currentLongestRoadLength;
    
    /**
     * Create a new PlayerManager for a Catan game
     */
    public PlayerManager() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.maxPlayers = 4;
        this.minPlayers = 3;
        this.gameStarted = false;
        
        // Initialize longest road tracking
        this.longestRoadCard = new LongestRoad();
        this.currentLongestRoadPlayerId = -1;
        this.currentLongestRoadLength = 0;
    }
    
    /**
     * Add a player to the game
     * @param name Player's name (must be unique)
     * @param color Player's color (must be unique)
     * @return true if player was added successfully
     * @throws IllegalArgumentException if name or color is already taken
     * @throws IllegalStateException if game is full or already started
     */
    public boolean addPlayer(String name, Player.PlayerColor color) {
        if (gameStarted) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Game is full (maximum " + maxPlayers + " players)");
        }
        
        // Validate unique name
        if (isNameTaken(name)) {
            throw new IllegalArgumentException("Player name '" + name + "' is already taken");
        }
        
        // Validate unique color
        if (isColorTaken(color)) {
            throw new IllegalArgumentException("Player color '" + color.getDisplayName() + "' is already taken");
        }
        
        // Create new player with turn order based on current player count
        int playerId = players.size();
        int turnOrder = players.size();
        Player newPlayer = new Player(playerId, name, color, turnOrder);
        
        players.add(newPlayer);
        return true;
    }
    
    /**
     * Remove a player from the game (only before game starts)
     * @param playerId ID of player to remove
     * @return true if player was removed successfully
     */
    public boolean removePlayer(int playerId) {
        if (gameStarted) {
            throw new IllegalStateException("Cannot remove players after game has started");
        }
        
        Player playerToRemove = getPlayerById(playerId);
        if (playerToRemove != null) {
            players.remove(playerToRemove);
            // Update turn orders for remaining players
            updateTurnOrders();
            return true;
        }
        return false;
    }
    
    /**
     * Start the game if minimum players are present
     * @return true if game was started successfully
     */
    public boolean startGame() {
        if (players.size() < minPlayers) {
            throw new IllegalStateException("Need at least " + minPlayers + " players to start");
        }
        
        if (gameStarted) {
            return false;
        }
        
        // Randomize turn order
        randomizeTurnOrder();
        
        gameStarted = true;
        
        // Set first player as active after game is marked as started
        if (!players.isEmpty()) {
            getCurrentPlayer().startTurn();
        }
        
        return true;
    }
    
    /**
     * Move to the next player's turn
     * @return the next player
     */
    public Player nextTurn() {
        if (!gameStarted || players.isEmpty()) {
            return null;
        }
        
        // End current player's turn
        getCurrentPlayer().endTurn();
        
        // Move to next player
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        
        // Start next player's turn
        Player nextPlayer = getCurrentPlayer();
        nextPlayer.startTurn();
        
        return nextPlayer;
    }
    
    /**
     * Get the currently active player
     * @return current player or null if no game is active
     */
    public Player getCurrentPlayer() {
        if (players.isEmpty() || !gameStarted) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }
    
    /**
     * Set the current player index (for setup phase management)
     * @param index The player index to set as current
     */
    public void setCurrentPlayerIndex(int index) {
        if (index >= 0 && index < players.size()) {
            currentPlayerIndex = index;
        }
    }
    
    /**
     * Get player by ID
     * @param playerId Player ID to find
     * @return Player object or null if not found
     */
    public Player getPlayerById(int playerId) {
        return players.stream()
                .filter(p -> p.getPlayerId() == playerId)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get player by name
     * @param name Player name to find
     * @return Player object or null if not found
     */
    public Player getPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get all players in turn order
     * @return List of players ordered by turn order
     */
    public List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Get all players sorted by turn order
     * @return List of players in turn order
     */
    public List<Player> getPlayersInTurnOrder() {
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparingInt(Player::getTurnOrder));
        return sortedPlayers;
    }
    
    /**
     * Check if any player has won the game
     * @return winning player or null if no winner yet
     */
    public Player getWinner() {
        return players.stream()
                .filter(Player::hasWon)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get players sorted by victory points (for leaderboard)
     * @return List of players sorted by victory points (descending)
     */
    public List<Player> getPlayersByVictoryPoints() {
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getVictoryPoints(), p1.getVictoryPoints()));
        return sortedPlayers;
    }
    
    /**
     * Check if a player name is already taken
     * @param name Name to check
     * @return true if name is taken
     */
    public boolean isNameTaken(String name) {
        return players.stream().anyMatch(p -> p.getName().equals(name));
    }
    
    /**
     * Check if a player color is already taken
     * @param color Color to check
     * @return true if color is taken
     */
    public boolean isColorTaken(Player.PlayerColor color) {
        return players.stream().anyMatch(p -> p.getColor().equals(color.getDisplayName()));
    }
    
    /**
     * Get available colors that haven't been chosen yet
     * @return List of available colors
     */
    public List<Player.PlayerColor> getAvailableColors() {
        List<Player.PlayerColor> availableColors = new ArrayList<>();
        for (Player.PlayerColor color : Player.PlayerColor.values()) {
            if (!isColorTaken(color)) {
                availableColors.add(color);
            }
        }
        return availableColors;
    }
    
    /**
     * Randomize the turn order of all players
     */
    private void randomizeTurnOrder() {
        List<Integer> turnOrders = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            turnOrders.add(i);
        }
        Collections.shuffle(turnOrders);
        
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTurnOrder(turnOrders.get(i));
        }
        
        // Sort players by new turn order
        players.sort(Comparator.comparingInt(Player::getTurnOrder));
        currentPlayerIndex = 0;
    }
    
    /**
     * Update turn orders after player removal
     */
    private void updateTurnOrders() {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTurnOrder(i);
        }
    }
    
    /**
     * Reset the game state (for new game)
     */
    public void resetGame() {
        players.clear();
        currentPlayerIndex = 0;
        gameStarted = false;
    }
    
    // === Getters ===
    
    public int getPlayerCount() {
        return players.size();
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    public boolean isGameFull() {
        return players.size() >= maxPlayers;
    }
    
    public boolean canStartGame() {
        return players.size() >= minPlayers && !gameStarted;
    }
    
    /**
     * Update longest road calculations for all players and award/reassign the card
     * @param edges All edges on the game board
     * @return true if longest road ownership changed
     */
    public boolean updateLongestRoad(List<Edge> edges) {
        if (!gameStarted) {
            return false;
        }
        
        int maxLength = 0;
        Player newLongestRoadPlayer = null;
        
        // Calculate longest road for each player
        for (Player player : players) {
            int roadLength = player.calculateLongestRoad(edges);
            if (roadLength >= LongestRoad.getMinimumRoadLength() && roadLength > maxLength) {
                maxLength = roadLength;
                newLongestRoadPlayer = player;
            }
        }
        
        // Check if ownership should change
        if (newLongestRoadPlayer == null) {
            // No one qualifies for longest road
            if (currentLongestRoadPlayerId != -1) {
                removeLongestRoadFromCurrentPlayer();
                return true;
            }
            return false;
        }
        
        // Check if there's a new longest road holder
        if (newLongestRoadPlayer.getPlayerId() != currentLongestRoadPlayerId) {
            // Remove from current holder if any
            if (currentLongestRoadPlayerId != -1) {
                removeLongestRoadFromCurrentPlayer();
            }
            
            // Award to new holder
            awardLongestRoadToPlayer(newLongestRoadPlayer, maxLength);
            return true;
        }
        
        // Update length even if same player holds it
        currentLongestRoadLength = maxLength;
        return false;
    }
    
    /**
     * Award the longest road card to a player
     * @param player The player to award the card to
     * @param roadLength The length of their longest road
     */
    private void awardLongestRoadToPlayer(Player player, int roadLength) {
        currentLongestRoadPlayerId = player.getPlayerId();
        currentLongestRoadLength = roadLength;
        longestRoadCard.setPlayerId(player.getPlayerId());
        player.addVictoryPoints(longestRoadCard.getVictoryPoints());
        player.calculateVictoryPoints(); // Recalculate total points
    }
    
    /**
     * Remove the longest road card from the current holder
     */
    private void removeLongestRoadFromCurrentPlayer() {
        if (currentLongestRoadPlayerId != -1) {
            Player currentHolder = getPlayerById(currentLongestRoadPlayerId);
            if (currentHolder != null) {
                currentHolder.removeVictoryPoints(longestRoadCard.getVictoryPoints());
                currentHolder.calculateVictoryPoints(); // Recalculate total points
            }
        }
        
        currentLongestRoadPlayerId = -1;
        currentLongestRoadLength = 0;
        longestRoadCard.deactivate();
    }
    
    /**
     * Get the player who currently holds the longest road card
     * @return Player with longest road, or null if no one has it
     */
    public Player getLongestRoadPlayer() {
        if (currentLongestRoadPlayerId == -1) {
            return null;
        }
        return getPlayerById(currentLongestRoadPlayerId);
    }
    
    /**
     * Get the current longest road length
     * @return The length of the current longest road
     */
    public int getCurrentLongestRoadLength() {
        return currentLongestRoadLength;
    }
    
    /**
     * Check if any player currently holds the longest road card
     * @return true if someone has the longest road card
     */
    public boolean hasLongestRoadHolder() {
        return currentLongestRoadPlayerId != -1;
    }

    /**
     * Get game statistics for all players
     * @return Map with player statistics
     */
    public Map<String, Object> getGameStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("playerCount", players.size());
        stats.put("gameStarted", gameStarted);
        stats.put("currentPlayer", getCurrentPlayer() != null ? getCurrentPlayer().getName() : "None");
        stats.put("winner", getWinner() != null ? getWinner().getName() : "None");
        
        // Calculate total resources in game
        int totalResources = players.stream()
                .mapToInt(Player::getTotalResourceCards)
                .sum();
        stats.put("totalResourcesInPlay", totalResources);
        
        return stats;
    }
    
    @Override
    public String toString() {
        return String.format("PlayerManager[Players: %d/%d, Started: %s, Current: %s]", 
                           players.size(), maxPlayers, gameStarted, 
                           getCurrentPlayer() != null ? getCurrentPlayer().getName() : "None");
    }
}
