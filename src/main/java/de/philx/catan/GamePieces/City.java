package de.philx.catan.GamePieces;

public class City extends GamePiece {

    private final String name = "Stadt";

    public City() {
        super();
    }

    public City(int playerId, char color) {
        super(playerId, color);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVictoryPoints() {
        return 2;
    }
}
