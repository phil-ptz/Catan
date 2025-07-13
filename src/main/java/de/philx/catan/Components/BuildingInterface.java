package de.philx.catan.Components;

import de.philx.catan.Controllers.BuildMode;
import de.philx.catan.Controllers.GameController;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.Player.ResourceType;
import de.philx.catan.Utils.ThemeManager;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Enhanced building interface with resource costs and building information
 */
public class BuildingInterface extends VBox {
    
    private final GameController gameController;
    private final Runnable onBuildingModeChanged;
    
    // Building cost information
    private static final Map<String, Map<ResourceType, Integer>> BUILDING_COSTS = new HashMap<>();
    
    static {
        // Road costs
        Map<ResourceType, Integer> roadCost = new HashMap<>();
        roadCost.put(ResourceType.WOOD, 1);
        roadCost.put(ResourceType.CLAY, 1);
        BUILDING_COSTS.put("road", roadCost);
        
        // Settlement costs
        Map<ResourceType, Integer> settlementCost = new HashMap<>();
        settlementCost.put(ResourceType.WOOD, 1);
        settlementCost.put(ResourceType.CLAY, 1);
        settlementCost.put(ResourceType.GRAIN, 1);
        settlementCost.put(ResourceType.WOOL, 1);
        BUILDING_COSTS.put("settlement", settlementCost);
        
        // City costs (upgrade from settlement)
        Map<ResourceType, Integer> cityCost = new HashMap<>();
        cityCost.put(ResourceType.GRAIN, 2);
        cityCost.put(ResourceType.ORE, 3);
        BUILDING_COSTS.put("city", cityCost);
    }
    
    // UI Components
    private VBox buildingOptionsBox;
    private Label statusLabel;
    private Button cancelButton;
    
    public BuildingInterface(GameController gameController, Runnable onBuildingModeChanged) {
        this.gameController = gameController;
        this.onBuildingModeChanged = onBuildingModeChanged;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateDisplay();
        
        // Register for theme changes
        ThemeManager.getInstance().addThemeChangeListener(this::applyTheme);
        applyTheme();
    }
    
    private void initializeComponents() {
        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        cancelButton = new Button("❌ Abbrechen");
        
        buildingOptionsBox = new VBox(10);
    }
    
    private Button createBuildingButton(String text, String buildingType) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.setOnAction(e -> startBuildingMode(buildingType));
        return button;
    }
    
    private void setupLayout() {
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.CENTER);
        
        // Title
        Label titleLabel = new Label("🔨 Bauen");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Status
        statusLabel.setText("Wähle was du bauen möchtest:");
        
        // Building option cards
        VBox roadCard = createBuildingCard("🛣️ Straße", "road", "Verbindet Siedlungen und Städte");
        VBox settlementCard = createBuildingCard("🏠 Siedlung", "settlement", "Produziert Ressourcen und gibt 1 Siegpunkt");
        VBox cityCard = createBuildingCard("🏰 Stadt", "city", "Produziert 2x Ressourcen und gibt 2 Siegpunkte");
        
        buildingOptionsBox.getChildren().addAll(roadCard, settlementCard, cityCard);
        
        // Cancel button
        HBox cancelBox = new HBox();
        cancelBox.setAlignment(Pos.CENTER);
        cancelBox.getChildren().add(cancelButton);
        
        this.getChildren().addAll(titleLabel, statusLabel, buildingOptionsBox, cancelBox);
    }
    
    private VBox createBuildingCard(String name, String buildingType, String description) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        Button buildButton = createBuildingButton("Bauen", buildingType);
        buildButton.setPrefWidth(80);
        buildButton.setPrefHeight(30);
        
        header.getChildren().addAll(nameLabel, new Region(), buildButton);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("Arial", 12));
        
        // Cost display
        HBox costBox = createCostDisplay(buildingType);
        
        // Availability check
        Label availabilityLabel = createAvailabilityLabel(buildingType);
        
        card.getChildren().addAll(header, descLabel, costBox, availabilityLabel);
        
        return card;
    }
    
    private HBox createCostDisplay(String buildingType) {
        HBox costBox = new HBox(10);
        costBox.setAlignment(Pos.CENTER_LEFT);
        
        Label costLabel = new Label("Kosten:");
        costLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        costBox.getChildren().add(costLabel);
        
        Map<ResourceType, Integer> costs = BUILDING_COSTS.get(buildingType);
        if (costs != null) {
            for (Map.Entry<ResourceType, Integer> entry : costs.entrySet()) {
                String resourceName = getResourceName(entry.getKey());
                String resourceIcon = getResourceIcon(entry.getKey());
                
                Label resourceCost = new Label(entry.getValue() + "x " + resourceIcon + " " + resourceName);
                resourceCost.setFont(Font.font("Arial", 10));
                costBox.getChildren().add(resourceCost);
            }
        }
        
        return costBox;
    }
    
    private Label createAvailabilityLabel(String buildingType) {
        Player currentPlayer = gameController.getCurrentPlayer();
        if (currentPlayer == null) {
            return new Label("Kein Spieler aktiv");
        }
        
        boolean canAfford = false;
        boolean hasBuildings = false;
        String message = "";
        
        switch (buildingType) {
            case "road":
                canAfford = currentPlayer.canBuildRoad();
                hasBuildings = currentPlayer.getAvailableRoads() > 0;
                message = hasBuildings ? 
                    (canAfford ? "✅ Verfügbar" : "❌ Nicht genügend Ressourcen") :
                    "❌ Keine Straßen mehr verfügbar";
                break;
                
            case "settlement":
                canAfford = currentPlayer.canBuildSettlement();
                hasBuildings = currentPlayer.getAvailableSettlements() > 0;
                message = hasBuildings ? 
                    (canAfford ? "✅ Verfügbar" : "❌ Nicht genügend Ressourcen") :
                    "❌ Keine Siedlungen mehr verfügbar";
                break;
                
            case "city":
                canAfford = currentPlayer.canBuildCity();
                hasBuildings = currentPlayer.getAvailableCities() > 0;
                message = hasBuildings ? 
                    (canAfford ? "✅ Verfügbar" : "❌ Nicht genügend Ressourcen") :
                    "❌ Keine Städte mehr verfügbar";
                break;
        }
        
        Label label = new Label(message);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        
        if (canAfford && hasBuildings) {
            label.setStyle("-fx-text-fill: green;");
        } else {
            label.setStyle("-fx-text-fill: red;");
        }
        
        return label;
    }
    
    private void setupEventHandlers() {
        cancelButton.setOnAction(e -> {
            gameController.stopBuildingMode();
            if (onBuildingModeChanged != null) {
                onBuildingModeChanged.run();
            }
        });
    }
    
    private void startBuildingMode(String buildingType) {
        Player currentPlayer = gameController.getCurrentPlayer();
        if (currentPlayer == null) {
            showAlert("Fehler", "Kein aktiver Spieler!");
            return;
        }
        
        // Check if player can afford the building
        boolean canBuild = false;
        switch (buildingType) {
            case "road":
                canBuild = currentPlayer.canBuildRoad();
                break;
            case "settlement":
                canBuild = currentPlayer.canBuildSettlement();
                break;
            case "city":
                canBuild = currentPlayer.canBuildCity();
                break;
        }
        
        if (!canBuild) {
            String buildingName = getBuildingName(buildingType);
            showAlert("Nicht möglich", "Du kannst keine " + buildingName + " bauen. Überprüfe deine Ressourcen und verfügbaren Gebäude.");
            return;
        }
        
        gameController.startBuildingMode(buildingType);
        statusLabel.setText("Klicke auf eine gültige Position um " + getBuildingName(buildingType) + " zu platzieren.");
        
        if (onBuildingModeChanged != null) {
            onBuildingModeChanged.run();
        }
    }
    
    private void updateDisplay() {
        // Update availability for all building types
        if (buildingOptionsBox != null) {
            buildingOptionsBox.getChildren().clear();
            
            VBox roadCard = createBuildingCard("🛣️ Straße", "road", "Verbindet Siedlungen und Städte");
            VBox settlementCard = createBuildingCard("🏠 Siedlung", "settlement", "Produziert Ressourcen und gibt 1 Siegpunkt");
            VBox cityCard = createBuildingCard("🏰 Stadt", "city", "Produziert 2x Ressourcen und gibt 2 Siegpunkte");
            
            buildingOptionsBox.getChildren().addAll(roadCard, settlementCard, cityCard);
        }
        
        // Update status based on building mode
        if (gameController.isBuildingModeActive()) {
            BuildMode buildingMode = gameController.getCurrentBuildingMode();
            String buildingType = convertBuildModeToString(buildingMode);
            statusLabel.setText("Bauen-Modus aktiv: " + getBuildingName(buildingType));
        } else {
            statusLabel.setText("Wähle was du bauen möchtest:");
        }
    }
    
    private String convertBuildModeToString(BuildMode buildMode) {
        if (buildMode == null) return "";
        
        switch (buildMode) {
            case ROAD:
            case SETUP_ROAD:
                return "road";
            case SETTLEMENT:
                return "settlement";
            case CITY:
                return "city";
            default:
                return "";
        }
    }
    
    private String getBuildingName(String buildingType) {
        switch (buildingType) {
            case "road": return "Straße";
            case "settlement": return "Siedlung";
            case "city": return "Stadt";
            default: return buildingType;
        }
    }
    
    private String getResourceName(ResourceType resource) {
        switch (resource) {
            case WOOD: return "Holz";
            case CLAY: return "Lehm";
            case WOOL: return "Wolle";
            case GRAIN: return "Getreide";
            case ORE: return "Erz";
            default: return resource.name();
        }
    }
    
    private String getResourceIcon(ResourceType resource) {
        switch (resource) {
            case WOOD: return "🌲";
            case CLAY: return "🧱";
            case WOOL: return "🐑";
            case GRAIN: return "🌾";
            case ORE: return "⛰️";
            default: return "❓";
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void applyTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply theme to main container
        this.setStyle(themeManager.getCardStyle());
        
        // Apply theme to status label
        if (statusLabel != null) {
            statusLabel.setStyle("-fx-text-fill: " + themeManager.getTextColor() + ";");
        }
    }
    
    /**
     * Refresh the display to show current game state
     */
    public void refresh() {
        updateDisplay();
    }
    
    /**
     * Show building interface in a new window
     */
    public static void showBuildingWindow(GameController gameController, Runnable onBuildingModeChanged) {
        Stage stage = new Stage();
        stage.setTitle("Bauen");
        stage.initModality(Modality.APPLICATION_MODAL);
        
        BuildingInterface buildingInterface = new BuildingInterface(gameController, onBuildingModeChanged);
        
        Scene scene = new Scene(new ScrollPane(buildingInterface), 450, 600);
        ThemeManager.getInstance().applyTheme(scene);
        
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Create a compact horizontal layout for the building interface
     */
    public void setupHorizontalLayout() {
        this.getChildren().clear();
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setMaxHeight(180);
        this.setAlignment(Pos.CENTER);
        
        // Create horizontal main container
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        
        // Title and status section
        VBox statusSection = new VBox(5);
        statusSection.setPrefWidth(200);
        statusSection.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("🔨 Bauen");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        statusLabel.setWrapText(true);
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        statusLabel.setMaxWidth(180);
        
        statusSection.getChildren().addAll(titleLabel, statusLabel);
        
        // Building options in horizontal cards
        HBox buildingCardsContainer = new HBox(10);
        buildingCardsContainer.setAlignment(Pos.CENTER);
        
        // Create compact building cards
        VBox roadCard = createCompactBuildingCard("🛣️", "Straße", "road", "Verbindet Siedlungen");
        VBox settlementCard = createCompactBuildingCard("🏠", "Siedlung", "settlement", "1 Siegpunkt + Ressourcen");
        VBox cityCard = createCompactBuildingCard("🏰", "Stadt", "city", "2 Siegpunkte + 2x Ressourcen");
        
        buildingCardsContainer.getChildren().addAll(roadCard, settlementCard, cityCard);
        
        // Action section
        VBox actionSection = new VBox(8);
        actionSection.setPrefWidth(120);
        actionSection.setAlignment(Pos.CENTER);
        
        Label actionLabel = new Label("⚡ Aktionen");
        actionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        cancelButton.setPrefWidth(100);
        actionSection.getChildren().addAll(actionLabel, cancelButton);
        
        mainContainer.getChildren().addAll(statusSection, buildingCardsContainer, actionSection);
        this.getChildren().add(mainContainer);
    }
    
    private VBox createCompactBuildingCard(String icon, String name, String buildingType, String description) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(8));
        card.setPrefWidth(160);
        card.setMaxWidth(160);
        card.setAlignment(Pos.CENTER);
        
        // Icon and name
        VBox header = new VBox(2);
        header.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        header.getChildren().addAll(iconLabel, nameLabel);
        
        // Description (compact)
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 9));
        descLabel.setMaxWidth(150);
        descLabel.setAlignment(Pos.CENTER);
        
        // Cost display (compact)
        HBox costBox = createCompactCostDisplay(buildingType);
        
        // Availability (compact)
        Label availabilityLabel = createAvailabilityLabel(buildingType);
        availabilityLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
        
        // Build button
        Button buildButton = createBuildingButton("Bauen", buildingType);
        buildButton.setPrefWidth(120);
        buildButton.setPrefHeight(25);
        buildButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        
        card.getChildren().addAll(header, descLabel, costBox, availabilityLabel, buildButton);
        
        // Style the card
        card.setStyle("-fx-background-color: #f0f0f0; " +
                     "-fx-background-radius: 8px; " +
                     "-fx-border-color: #cccccc; " +
                     "-fx-border-radius: 8px; " +
                     "-fx-border-width: 1px;");
        
        return card;
    }
    
    private HBox createCompactCostDisplay(String buildingType) {
        HBox costBox = new HBox(3);
        costBox.setAlignment(Pos.CENTER);
        
        Map<ResourceType, Integer> costs = BUILDING_COSTS.get(buildingType);
        if (costs != null) {
            for (Map.Entry<ResourceType, Integer> entry : costs.entrySet()) {
                String resourceIcon = getResourceIcon(entry.getKey());
                
                Label resourceCost = new Label(entry.getValue() + resourceIcon);
                resourceCost.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
                costBox.getChildren().add(resourceCost);
            }
        }
        
        return costBox;
    }
}
