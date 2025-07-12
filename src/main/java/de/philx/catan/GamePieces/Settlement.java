package de.philx.catan.GamePieces;

public class Settlement extends GamePiece {

    private final String name = "Siedlung";

    public Settlement() {
        super();
    }

    public Settlement(int playerId, char color) {
        super(playerId, color);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVictoryPoints() {
        return 1;
    }
}
