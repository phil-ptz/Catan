package de.philx.catan.GamePieces;

public abstract class GamePiece {

    private char color;
    private int playerId;

    public GamePiece() {
        this.playerId = -1; // Default to no owner
    }

    public GamePiece(int playerId, char color) {
        this.playerId = playerId;
        this.color = color;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public abstract String getName();
    
    public abstract int getVictoryPoints();
}
