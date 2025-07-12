package de.philx.catan.Cards.Special;

/**
 * Longest Road special card - awards 2 victory points to the player 
 * with the longest continuous road of 5 or more segments
 */
public class LongestRoad extends SpecialCard {
    private static final int VICTORY_POINTS = 2;
    private static final int MINIMUM_ROAD_LENGTH = 5;
    
    @Override
    public int getVictoryPoints() {
        return VICTORY_POINTS;
    }
    
    @Override
    public String getName() {
        return "Längste Handelsstraße";
    }
    
    /**
     * Get the minimum road length required to claim this card
     */
    public static int getMinimumRoadLength() {
        return MINIMUM_ROAD_LENGTH;
    }
}
