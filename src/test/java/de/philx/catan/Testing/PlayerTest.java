package de.philx.catan.Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import de.philx.catan.Players.Player;
import de.philx.catan.Players.PlayerManager;

import java.util.List;
import java.util.Map;

/**
 * Test class to demonstrate and validate Player and PlayerManager functionality.
 * This class shows how the enhanced player management system works.
 */
public class PlayerTest {

    private PlayerManager manager;
    private Player player;

    @BeforeEach
    void setUp() {
        manager = new PlayerManager();
        player = new Player(0, "TestPlayer", Player.PlayerColor.RED, 0);
    }

    @Test
    void testPlayerManager() {
        assertDoesNotThrow(() -> manager.addPlayer("Alice", Player.PlayerColor.RED));
        assertDoesNotThrow(() -> manager.addPlayer("Bob", Player.PlayerColor.BLUE));
        assertDoesNotThrow(() -> manager.addPlayer("Charlie", Player.PlayerColor.ORANGE));
        assertEquals(3, manager.getPlayerCount());

        assertThrows(IllegalArgumentException.class, () -> manager.addPlayer("Alice", Player.PlayerColor.WHITE));
        assertThrows(IllegalArgumentException.class, () -> manager.addPlayer("David", Player.PlayerColor.RED));

        assertTrue(manager.canStartGame());
        manager.startGame();

        Player currentPlayer = manager.getCurrentPlayer();
        assertNotNull(currentPlayer);

        Player nextPlayer = manager.nextTurn();
        assertNotNull(nextPlayer);
        assertNotEquals(currentPlayer, nextPlayer);

        List<Player> orderedPlayers = manager.getPlayersInTurnOrder();
        assertEquals(3, orderedPlayers.size());
    }

    @Test
    void testPlayerFunctionality() {
        player.addResource(Player.ResourceType.WOOD, 3);
        player.addResource(Player.ResourceType.CLAY, 2);
        player.addResource(Player.ResourceType.GRAIN, 4);
        player.addResource(Player.ResourceType.WOOL, 1);
        player.addResource(Player.ResourceType.ORE, 2);

        assertEquals(12, player.getTotalResourceCards());
        assertEquals(3, player.getResourceAmount(Player.ResourceType.WOOD));

        int initialRoads = player.getAvailableRoads();
        assertTrue(player.canBuildRoad());
        player.buildRoad();
        assertEquals(initialRoads - 1, player.getAvailableRoads());

        int initialSettlements = player.getAvailableSettlements();
        assertTrue(player.canBuildSettlement());
        player.buildSettlement();
        assertEquals(initialSettlements - 1, player.getAvailableSettlements());
        assertEquals(1, player.getPlacedSettlements());
        assertEquals(1, player.getVictoryPoints());

        player.addResource(Player.ResourceType.GRAIN, 2);
        player.addResource(Player.ResourceType.ORE, 3);

        int initialCities = player.getAvailableCities();
        assertTrue(player.canBuildCity());
        player.buildCity();
        assertEquals(initialCities - 1, player.getAvailableCities());
        assertEquals(1, player.getPlacedCities());
        assertEquals(2, player.getVictoryPoints());

        assertFalse(player.hasWon());
        player.addVictoryPoints(8);
        assertTrue(player.hasWon());

        Map<Player.ResourceType, Integer> roadCost = Map.of(Player.ResourceType.WOOD, 1, Player.ResourceType.CLAY, 1);
        assertTrue(player.canAfford(roadCost));

        assertNotNull(player.toString());
        assertNotNull(player.getResourceInventory());
    }
}
