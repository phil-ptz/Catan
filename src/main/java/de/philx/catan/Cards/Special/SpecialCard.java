package de.philx.catan.Cards.Special;

/**
 * Base class for special achievement cards in Catan
 */
public abstract class SpecialCard {
    protected int playerId;
    protected boolean isActive;
    
    public SpecialCard() {
        this.playerId = -1; // No owner initially
        this.isActive = false;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
        this.isActive = (playerId != -1);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void deactivate() {
        this.playerId = -1;
        this.isActive = false;
    }
    
    /**
     * Get the victory points awarded by this special card
     */
    public abstract int getVictoryPoints();
    
    /**
     * Get the name of this special card
     */
    public abstract String getName();
}
