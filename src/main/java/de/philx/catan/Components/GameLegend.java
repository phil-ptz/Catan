package de.philx.catan.Components;

import de.philx.catan.GameField.TerrainType;
import de.philx.catan.Players.Player;
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
        this.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #333333; -fx-border-width: 2;");
        this.setPrefWidth(250);
        
        initializeLegend();
    }
    
    private void initializeLegend() {
        // Main title
        Label titleLabel = new Label("CATAN Legend");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.DARKBLUE);
        
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
            createSeparator(),
            terrainSection,
            createSeparator(),
            playerSection,
            createSeparator(),
            diceSection,
            createSeparator(),
            buildingSection
        );
    }
    
    private VBox createTerrainTypesSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("Terrain Types & Resources");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(sectionTitle);
        
        for (TerrainType terrain : TerrainType.values()) {
            HBox terrainItem = new HBox(5);
            terrainItem.setAlignment(Pos.CENTER_LEFT);
            
            // Color indicator (small hexagon shape)
            Polygon hexagon = createSmallHexagon(terrain.getColor());
            
            // Terrain name and resource
            String resourceText = terrain == TerrainType.DESERT ? 
                terrain.name() + " (No production)" : 
                terrain.name() + " → " + terrain.getResource();
            Label terrainLabel = new Label(resourceText);
            terrainLabel.setFont(Font.font("Arial", 10));
            
            terrainItem.getChildren().addAll(hexagon, terrainLabel);
            section.getChildren().add(terrainItem);
        }
        
        return section;
    }
    
    private VBox createPlayerColorsSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("Player Colors");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        section.getChildren().add(sectionTitle);
        
        for (Player.PlayerColor color : Player.PlayerColor.values()) {
            HBox colorItem = new HBox(5);
            colorItem.setAlignment(Pos.CENTER_LEFT);
            
            // Color indicator
            Circle colorIndicator = new Circle(8);
            colorIndicator.setFill(getPlayerColor(color));
            colorIndicator.setStroke(Color.BLACK);
            colorIndicator.setStrokeWidth(1);
            
            Label colorLabel = new Label(color.getDisplayName());
            colorLabel.setFont(Font.font("Arial", 10));
            
            colorItem.getChildren().addAll(colorIndicator, colorLabel);
            section.getChildren().add(colorItem);
        }
        
        return section;
    }
    
    private VBox createDiceNumbersSection() {
        VBox section = new VBox(ITEM_SPACING);
        
        Label sectionTitle = new Label("Dice Numbers");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label explanationLabel = new Label("Numbers indicate dice roll needed for resource production");
        explanationLabel.setFont(Font.font("Arial", 9));
        explanationLabel.setWrapText(true);
        explanationLabel.setTextFill(Color.DARKGRAY);
        
        Label probabilityLabel = new Label("• More dots (●) = Higher probability");
        probabilityLabel.setFont(Font.font("Arial", 9));
        probabilityLabel.setTextFill(Color.DARKGRAY);
        
        Label frequentLabel = new Label("• Most frequent: 6, 8 (5 dots each)");
        frequentLabel.setFont(Font.font("Arial", 9));
        frequentLabel.setTextFill(Color.DARKGRAY);
        
        Label rareLabel = new Label("• Least frequent: 2, 12 (1 dot each)");
        rareLabel.setFont(Font.font("Arial", 9));
        rareLabel.setTextFill(Color.DARKGRAY);
        
        Label robberLabel = new Label("• Roll 7: Move robber (no production)");
        robberLabel.setFont(Font.font("Arial", 9));
        robberLabel.setTextFill(Color.RED);
        
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
        
        Label sectionTitle = new Label("Building Costs");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Road cost
        HBox roadItem = new HBox(5);
        roadItem.setAlignment(Pos.CENTER_LEFT);
        Rectangle roadIcon = new Rectangle(15, 3, Color.BROWN);
        Label roadLabel = new Label("Road: 1 Wood + 1 Clay");
        roadLabel.setFont(Font.font("Arial", 9));
        roadItem.getChildren().addAll(roadIcon, roadLabel);
        
        // Settlement cost
        HBox settlementItem = new HBox(5);
        settlementItem.setAlignment(Pos.CENTER_LEFT);
        Polygon settlementIcon = createHouseShape(Color.BLUE);
        Label settlementLabel = new Label("Settlement: 1 Wood + 1 Clay + 1 Grain + 1 Wool");
        settlementLabel.setFont(Font.font("Arial", 9));
        settlementItem.getChildren().addAll(settlementIcon, settlementLabel);
        
        // City cost
        HBox cityItem = new HBox(5);
        cityItem.setAlignment(Pos.CENTER_LEFT);
        Rectangle cityIcon = new Rectangle(12, 12, Color.RED);
        Label cityLabel = new Label("City: 2 Grain + 3 Ore (upgrade from Settlement)");
        cityLabel.setFont(Font.font("Arial", 9));
        cityItem.getChildren().addAll(cityIcon, cityLabel);
        
        // Victory points
        Label victoryLabel = new Label("Victory Points: Settlement=1, City=2, Win at 10");
        victoryLabel.setFont(Font.font("Arial", 9));
        victoryLabel.setTextFill(Color.DARKGREEN);
        victoryLabel.setStyle("-fx-font-weight: bold;");
        
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
    
    private Rectangle createSeparator() {
        Rectangle separator = new Rectangle(220, 1);
        separator.setFill(Color.LIGHTGRAY);
        return separator;
    }
}
