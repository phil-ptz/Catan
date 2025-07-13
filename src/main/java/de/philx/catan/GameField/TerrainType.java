package de.philx.catan.GameField;

import javafx.scene.paint.Color;

/**
 * Enum representing the different terrain types in CATAN
 */
public enum TerrainType {
    FOREST(Color.DARKGREEN, "Holz"),
    PASTURE(Color.LIGHTGREEN, "Wolle"),
    FIELDS(Color.GOLD, "Getreide"),
    HILLS(Color.SANDYBROWN, "Lehm"),
    MOUNTAINS(Color.GRAY, "Erz"),
    DESERT(Color.LIGHTYELLOW, "Keine");

    private final Color color;
    private final String resource;

    TerrainType(Color color, String resource) {
        this.color = color;
        this.resource = resource;
    }

    public Color getColor() {
        return color;
    }

    public String getResource() {
        return resource;
    }

    /**
     * Get the German name for this terrain type
     * @return German name for the terrain
     */
    public String getGermanName() {
        switch (this) {
            case FOREST: return "Wald";
            case PASTURE: return "Weide";
            case FIELDS: return "Acker";
            case HILLS: return "Hügel";
            case MOUNTAINS: return "Gebirge";
            case DESERT: return "Wüste";
            default: return this.name();
        }
    }

    /**
     * Returns the standard distribution of terrain types for CATAN
     * Total: 19 hexagons (4 Forest, 4 Pasture, 4 Fields, 3 Hills, 3 Mountains, 1 Desert)
     */
    public static TerrainType[] getStandardDistribution() {
        return new TerrainType[]{
            // 4 Forest hexagons
            FOREST, FOREST, FOREST, FOREST,
            // 4 Pasture hexagons
            PASTURE, PASTURE, PASTURE, PASTURE,
            // 4 Fields hexagons
            FIELDS, FIELDS, FIELDS, FIELDS,
            // 3 Hills hexagons
            HILLS, HILLS, HILLS,
            // 3 Mountains hexagons
            MOUNTAINS, MOUNTAINS, MOUNTAINS,
            // 1 Desert hexagon
            DESERT
        };
    }
}
