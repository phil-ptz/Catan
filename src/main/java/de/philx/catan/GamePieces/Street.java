package de.philx.catan.GamePieces;

public class Street extends GamePiece {

    private final String name = "Straße";

    public Street() {
        super();
    }

    public Street(int playerId, char color) {
        super(playerId, color);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVictoryPoints() {
        return 0; // Roads don't give victory points directly
    }
}
