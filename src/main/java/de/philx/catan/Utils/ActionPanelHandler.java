package de.philx.catan.Utils;

/**
 * Interface for handling UI action callbacks
 */
public interface ActionPanelHandler {
    
    /**
     * Show the trading interface
     */
    void showTradingInterface();
    
    /**
     * Show the building interface
     */
    void showBuildingInterface();
    
    /**
     * Hide any currently visible interface
     */
    void hideInterface();
}
