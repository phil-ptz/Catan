package de.philx.catan.Controllers;

/**
 * Enumeration for different building modes in the game.
 * This provides compile-time safety and prevents typos when checking building modes.
 */
public enum BuildMode {
    /** Normal road building mode */
    ROAD,
    
    /** Normal settlement building mode */
    SETTLEMENT,
    
    /** Normal city building mode */
    CITY,
    
    /** Setup phase road building mode */
    SETUP_ROAD
}
