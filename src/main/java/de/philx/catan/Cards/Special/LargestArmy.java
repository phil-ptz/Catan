package de.philx.catan.Cards.Special;

/**
 * Largest Army special card - not implemented in basic Catan version
 * This is a placeholder for potential future expansion
 */
public class LargestArmy extends SpecialCard {
    
    @Override
    public int getVictoryPoints() {
        return 2; // Standard Catan largest army award
    }
    
    @Override
    public String getName() {
        return "Größte Rittermacht";
    }
}
