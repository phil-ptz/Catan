package de.philx.catan.Components;

import de.philx.catan.GameField.TerrainType;
import de.philx.catan.Players.Player;
import de.philx.catan.Utils.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Game legend component that explains the meaning of colors, symbols, and numbers in the Catan game
 */
public class GameLegend extends VBox {
    
    private static final double LEGEND_SPACING = 10.0;
    private static final double ITEM_SPACING = 5.0;
    
    public GameLegend() {
        this.setSpacing(LEGEND_SPACING);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWidth(250);
        
        // Apply current theme when component is created
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyCurrentTheme();
            }
        });
        
        // Register for theme change notifications
        ThemeManager.getInstance().addThemeChangeListener(this::applyCurrentTheme);
        
        initializeLegend();
    }
    
    /**
     * Apply the current theme to the GameLegend component
     */
    public void applyCurrentTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply modern card styling
        this.setStyle(themeManager.getCardStyle());
        
        // Update text colors for all labels
        updateTextColors();
    }
    
    /**
     * Update text colors for all labels in the legend
     */
    private void updateTextColors() {
        ThemeManager themeManager = ThemeManager.getInstance();
        String textColor = themeManager.isDarkMode() ? "#ffffff" : "#000000";
        
        // Recursively update all Label nodes
        updateLabelsInNode(this, textColor);
    }
    
    /**
     * Recursively update all Label nodes in a container
     */
    private void updateLabelsInNode(javafx.scene.Parent parent, String textColor) {
        parent.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                // Don't override special colored labels (like dice numbers)
                if (!label.getStyle().contains("-fx-text-fill: rgb(") && 
                    !label.getStyle().contains("-fx-text-fill: #")) {
                    label.setStyle(label.getStyle() + "-fx-text-fill: " + textColor + ";");
                }
            } else if (node instanceof javafx.scene.Parent) {
                updateLabelsInNode((javafx.scene.Parent) node, textColor);
            }
        });
    }
    
    private void initializeLegend() {
        // Main title with enhanced styling
        Label titleLabel = new Label("CATAN Legende");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web(ThemeManager.ACCENT_COLOR));
        titleLabel.setStyle("-fx-padding: 0 0 10 0;");
        
        // Terrain Types Section
        VBox terrainSection = createTerrainTypesSection();
        
        // Player Colors Section
        VBox playerSection = createPlayerColorsSection();
        
        // Dice Numbers Section
        VBox diceSection = createDiceNumbersSection();
        
        // Building Costs Section
        VBox buildingSection = createBuildingCostsSection();
        
        this.getChildren().addAll(
            titleLabel,
            createStyledSeparator(),
            terrainSection,
            createStyledSeparator(),
            playerSection,
            createStyledSeparator(),
            diceSection,
            createStyledSeparator(),
            buildingSection
        );
    }
    
    private VBox createTerrainTypesSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("Gelände & Ressourcen");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        section.getChildren().add(sectionTitle);
        
        for (TerrainType terrain : TerrainType.values()) {
            HBox terrainItem = new HBox(8);
            terrainItem.setAlignment(Pos.CENTER_LEFT);
            terrainItem.setPadding(new Insets(4, 0, 4, 0));
            
            // Color indicator (small hexagon shape)
            Polygon hexagon = createSmallHexagon(terrain.getColor());
            
            // Terrain name and resource
            String resourceText = terrain == TerrainType.DESERT ? 
                terrain.getGermanName() + " (Keine Produktion)" : 
                terrain.getGermanName() + " → " + terrain.getResource();
            Label terrainLabel = new Label(resourceText);
            terrainLabel.setFont(Font.font("Segoe UI", 11));
            
            terrainItem.getChildren().addAll(hexagon, terrainLabel);
            section.getChildren().add(terrainItem);
        }
        
        return section;
    }
    
    private VBox createPlayerColorsSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("👥 Spielerfarben");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        section.getChildren().add(sectionTitle);
        
        for (Player.PlayerColor color : Player.PlayerColor.values()) {
            HBox colorItem = new HBox(8);
            colorItem.setAlignment(Pos.CENTER_LEFT);
            colorItem.setPadding(new Insets(4, 0, 4, 0));
            
            // Color indicator with enhanced styling
            Circle colorIndicator = new Circle(10);
            colorIndicator.setFill(getPlayerColor(color));
            colorIndicator.setStroke(Color.web(ThemeManager.getInstance().getBorderColor()));
            colorIndicator.setStrokeWidth(1.5);
            
            Label colorLabel = new Label(color.getDisplayName());
            colorLabel.setFont(Font.font("Segoe UI", 11));
            
            colorItem.getChildren().addAll(colorIndicator, colorLabel);
            section.getChildren().add(colorItem);
        }
        
        return section;
    }
    
    private VBox createDiceNumbersSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("🎲 Würfelzahlen");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        Label explanationLabel = new Label("Zahlen zeigen benötigte Würfelergebnisse für Ressourcenproduktion");
        explanationLabel.setFont(Font.font("Segoe UI", 10));
        explanationLabel.setWrapText(true);
        explanationLabel.setTextFill(Color.web(ThemeManager.getInstance().getSecondaryTextColor()));
        
        Label probabilityLabel = new Label("• Mehr Punkte (●) = Höhere Wahrscheinlichkeit");
        probabilityLabel.setFont(Font.font("Segoe UI", 10));
        probabilityLabel.setTextFill(Color.web(ThemeManager.getInstance().getSecondaryTextColor()));
        
        Label frequentLabel = new Label("• Häufigste: 6, 8 (je 5 Punkte)");
        frequentLabel.setFont(Font.font("Segoe UI", 10));
        frequentLabel.setTextFill(Color.web(ThemeManager.getInstance().getSecondaryTextColor()));
        
        Label rareLabel = new Label("• Seltenste: 2, 12 (je 1 Punkt)");
        rareLabel.setFont(Font.font("Segoe UI", 10));
        rareLabel.setTextFill(Color.web(ThemeManager.getInstance().getSecondaryTextColor()));
        
        Label robberLabel = new Label("• Würfel 7: Räuber bewegen (keine Produktion)");
        robberLabel.setFont(Font.font("Segoe UI", 10));
        robberLabel.setTextFill(Color.web(ThemeManager.DANGER_COLOR));
        robberLabel.setStyle("-fx-font-weight: bold;");
        
        section.getChildren().addAll(
            sectionTitle,
            explanationLabel,
            probabilityLabel,
            frequentLabel,
            rareLabel,
            robberLabel
        );
        
        return section;
    }
    
    private VBox createBuildingCostsSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("Baukosten");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        // Road cost
        HBox roadItem = new HBox(8);
        roadItem.setAlignment(Pos.CENTER_LEFT);
        roadItem.setPadding(new Insets(4, 0, 4, 0));
        Rectangle roadIcon = new Rectangle(18, 4, Color.web("#8B4513"));
        roadIcon.setArcWidth(2);
        roadIcon.setArcHeight(2);
        Label roadLabel = new Label("Straße: 1 Holz + 1 Lehm");
        roadLabel.setFont(Font.font("Segoe UI", 10));
        roadItem.getChildren().addAll(roadIcon, roadLabel);
        
        // Settlement cost
        HBox settlementItem = new HBox(8);
        settlementItem.setAlignment(Pos.CENTER_LEFT);
        settlementItem.setPadding(new Insets(4, 0, 4, 0));
        Polygon settlementIcon = createHouseShape(Color.web(ThemeManager.ACCENT_COLOR));
        Label settlementLabel = new Label("Siedlung: 1 Holz + 1 Lehm + 1 Getreide + 1 Wolle");
        settlementLabel.setFont(Font.font("Segoe UI", 10));
        settlementItem.getChildren().addAll(settlementIcon, settlementLabel);
        
        // City cost
        HBox cityItem = new HBox(8);
        cityItem.setAlignment(Pos.CENTER_LEFT);
        cityItem.setPadding(new Insets(4, 0, 4, 0));
        Rectangle cityIcon = new Rectangle(14, 14, Color.web(ThemeManager.DANGER_COLOR));
        cityIcon.setArcWidth(3);
        cityIcon.setArcHeight(3);
        Label cityLabel = new Label("Stadt: 2 Getreide + 3 Erz (Upgrade von Siedlung)");
        cityLabel.setFont(Font.font("Segoe UI", 10));
        cityItem.getChildren().addAll(cityIcon, cityLabel);
        
        // Victory points
        Label victoryLabel = new Label("🏆 Siegpunkte: Siedlung=1, Stadt=2, Sieg bei 10");
        victoryLabel.setFont(Font.font("Segoe UI", 10));
        victoryLabel.setTextFill(Color.web(ThemeManager.SUCCESS_COLOR));
        victoryLabel.setStyle("-fx-font-weight: bold; -fx-padding: 8 0 0 0;");
        
        section.getChildren().addAll(
            sectionTitle,
            roadItem,
            settlementItem,
            cityItem,
            victoryLabel
        );
        
        return section;
    }
    
    private Polygon createSmallHexagon(Color color) {
        Polygon hexagon = new Polygon();
        double size = 8.0;
        
        // Create hexagon points
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i;
            double x = size * Math.cos(angle);
            double y = size * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        
        hexagon.setFill(color);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(0.5);
        
        return hexagon;
    }
    
    private Polygon createHouseShape(Color color) {
        Polygon house = new Polygon();
        house.getPoints().addAll(new Double[]{
            6.0, 0.0,   // top
            0.0, 6.0,   // left bottom
            12.0, 6.0,  // right bottom
            9.0, 3.0,   // right roof
            3.0, 3.0    // left roof
        });
        house.setFill(color);
        house.setStroke(Color.BLACK);
        house.setStrokeWidth(0.5);
        return house;
    }
    
    private Color getPlayerColor(Player.PlayerColor playerColor) {
        switch (playerColor) {
            case RED: return Color.RED;
            case BLUE: return Color.BLUE;
            case ORANGE: return Color.ORANGE;
            case WHITE: return Color.WHITE;
            default: return Color.GRAY;
        }
    }
    
    private Rectangle createStyledSeparator() {
        Rectangle separator = new Rectangle(220, 1);
        ThemeManager themeManager = ThemeManager.getInstance();
        separator.setFill(Color.web(themeManager.getBorderColor()));
        separator.setOpacity(0.5);
        return separator;
    }
}
