package de.philx.catan.Players;

import de.philx.catan.Cards.Developments.DevelopmentCard;
import de.philx.catan.Cards.Resources.ResourceCard;
import de.philx.catan.Cards.Special.SpecialCard;
import de.philx.catan.GamePieces.GamePiece;

/**
 * This class represents a player in the game. A player has attributes related
 * to their identity, their game actions, and their inventory of game pieces and cards.
 */

public class Player {

    // Name des Spielers
    private String name;
    // Farbe der Figuren
    private char color;
    // Ist der Spieler am Zug
    private boolean active;
    // Spielfiguren im Inventar
    private GamePiece[] gamePieces;
    // Rohstoffkarten im Inventar
    private ResourceCard[] resourceCards;
    // Entwicklungskarten im Inventar
    private DevelopmentCard[] developmentCards;
    // Sonderkarten
    private SpecialCard[] specialCards;
    // Siegespunkte
    private int victoryPoints;

    public Player(String name, char color) {
        this.name = name;
        this.color = color;
        this.active = false;

        // 5 Siedlungen + 4 Städte + 15 Straßen
        this.gamePieces = new GamePiece[24];
        // Offiziell maximal 95 Rohstoffkarten
        this.resourceCards = new ResourceCard[95];
        // Offiziell maximal 25 Entwicklungskarten
        this.developmentCards = new DevelopmentCard[25];
        // 2 Sonderkarten pro Spiel
        this.specialCards = new SpecialCard[2];
    }
}
