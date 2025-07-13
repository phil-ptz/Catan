package de.philx.catan.Screens;

import de.philx.catan.Components.GameLegend;
import de.philx.catan.Components.PlayerInterface;
import de.philx.catan.Controllers.BuildMode;
import de.philx.catan.Controllers.GameController;
import de.philx.catan.GameField.Edge;
import de.philx.catan.GameField.GameField;
import de.philx.catan.GameField.Node;
import de.philx.catan.Utils.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GameScreen extends HBox {

    private final GameController gameController;
    private Group gameFieldGroup;
    private final PlayerInterface playerInterface;
    private final GameLegend gameLegend;
    private VBox gameAreaContainer;
    private ScrollPane gameFieldScrollPane;

    public GameScreen(int width, int height, Runnable onReturnToMenu) {
        this.gameController = new GameController();
        // Start the test game to initialize players
        this.gameController.startTestGame();
        // Initialize with visual elements for nodes and edges
        this.gameFieldGroup = gameController.getGameField().toGroup(false, null);
        this.playerInterface = new PlayerInterface(gameController, onReturnToMenu, this::refreshGameFieldDisplay);
        this.gameLegend = new GameLegend();
        
        this.setPrefSize(width, height);
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        
        setupLayout();
        setupGameFieldInteraction();
        applyTheme();
        setupAnimations();
        
        // Apply current theme when screen is created
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.getInstance().applyTheme(newScene);
                applyTheme();
            }
        });
        
        // Register for theme changes
        ThemeManager.getInstance().addThemeChangeListener(this::applyTheme);
    }
    
    private void setupLayout() {
        // Left side: Player Interface
        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(340);
        leftPanel.getChildren().add(playerInterface);
        
        // Center: Game Field with scroll support
        gameAreaContainer = new VBox(10);
        gameAreaContainer.setAlignment(Pos.CENTER);
        
        // Create scrollable game field with centering
        gameFieldScrollPane = new ScrollPane();
        gameFieldScrollPane.setContent(gameFieldGroup);
        gameFieldScrollPane.setFitToWidth(false);  // Don't fit to width to maintain aspect ratio
        gameFieldScrollPane.setFitToHeight(false); // Don't fit to height to maintain aspect ratio
        gameFieldScrollPane.setPannable(true);
        gameFieldScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gameFieldScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gameFieldScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        // Setup centering for the game field
        setupGameFieldCentering();
        
        // Apply initial centering when the scene is ready
        gameFieldScrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                javafx.application.Platform.runLater(this::centerGameField);
            }
        });
        
        gameAreaContainer.getChildren().add(gameFieldScrollPane);
        VBox.setVgrow(gameFieldScrollPane, Priority.ALWAYS);
        
        // Right side: Game Legend
        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(280);
        rightPanel.getChildren().add(gameLegend);
        
        // Add all panels to main container
        this.getChildren().addAll(leftPanel, gameAreaContainer, rightPanel);
        HBox.setHgrow(gameAreaContainer, Priority.ALWAYS);
    }
    
    private void setupGameFieldInteraction() {
        // Add click handler for building placement and robber placement
        gameFieldGroup.setOnMouseClicked(this::handleGameFieldClick);
        
        // Add hover effects
        gameFieldGroup.setOnMouseEntered(e -> {
            if (gameController.isWaitingForRobberPlacement()) {
                gameFieldGroup.setStyle("-fx-cursor: hand;");
            } else if (gameController.isBuildingModeActive() || gameController.getPlayerManager().isSetupPhase()) {
                gameFieldGroup.setStyle("-fx-cursor: crosshair;");
            }
        });
        
        gameFieldGroup.setOnMouseExited(e -> {
            gameFieldGroup.setStyle("-fx-cursor: default;");
        });
    }
    
    private void setupAnimations() {
        // Fade in animation for the entire screen
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void applyTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply background styling
        this.setStyle(String.format("""
            -fx-background-color: %s;
            """, themeManager.getBackgroundColor()));
        
        // Style the game area container
        gameAreaContainer.setStyle(themeManager.getCardStyle() + " -fx-alignment: center;");
    }
    
    
    /**
     * Handle clicks on the game field
     * @param event The mouse click event
     */
    private void handleGameFieldClick(MouseEvent event) {
        // Handle robber placement
        if (gameController.isWaitingForRobberPlacement()) {
            handleRobberPlacement(event);
            return;
        }
        
        // Handle building placement (either in building mode or setup phase)
        if (gameController.isBuildingModeActive()) {
            handleBuildingPlacement(event);
            return;
        }
        
        // Handle setup phase - allow direct building without mode activation
        if (gameController.getPlayerManager().isSetupPhase()) {
            handleSetupPhaseBuilding(event);
            return;
        }
    }
    
    /**
     * Handle robber placement clicks
     */
    private void handleRobberPlacement(MouseEvent event) {
        double clickX = event.getX();
        double clickY = event.getY();
        
        GameField gameField = gameController.getGameField();
        for (int i = 0; i < gameField.getHexagons().length; i++) {
            var hex = gameField.getHexagon(i);
            if (hex != null && isPointInHexagon(clickX, clickY, hex.getCenterX(), hex.getCenterY(), hex.getRadius())) {
                boolean moved = gameController.moveRobber(i);
                if (moved) {
                    refreshGameFieldDisplay();
                }
                break;
            }
        }
    }
    
    /**
     * Convert BuildMode enum to string for legacy UI compatibility
     */
    private String convertBuildModeToString(BuildMode buildMode) {
        if (buildMode == null) return null;
        
        switch (buildMode) {
            case ROAD:
            case SETUP_ROAD:
                return "road";
            case SETTLEMENT:
                return "settlement";
            case CITY:
                return "city";
            default:
                return null;
        }
    }
    
    /**
     * Handle building placement clicks
     */
    private void handleBuildingPlacement(MouseEvent event) {
        double clickX = event.getX();
        double clickY = event.getY();
        BuildMode buildingMode = gameController.getCurrentBuildingMode();
        
        if (buildingMode == null) return;
        
        if (buildingMode == BuildMode.SETTLEMENT || buildingMode == BuildMode.CITY) {
            // Handle node-based building (settlements/cities)
            String buildingType = convertBuildModeToString(buildingMode);
            handleNodeBuilding(clickX, clickY, buildingType);
        } else if (buildingMode == BuildMode.ROAD || buildingMode == BuildMode.SETUP_ROAD) {
            // Handle edge-based building (roads)
            handleEdgeBuilding(clickX, clickY);
        }
    }
    
    /**
     * Handle settlement and city placement
     * @return true if building was placed successfully
     */
    private boolean handleNodeBuilding(double clickX, double clickY, String buildingType) {
        GameField gameField = gameController.getGameField();
        
        // Find the closest node to the click
        Node closestNode = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Node node : gameField.getNodes()) {
            double distance = Math.sqrt(Math.pow(clickX - node.getX(), 2) + Math.pow(clickY - node.getY(), 2));
            if (distance < 15 && distance < closestDistance) { // 15 pixel tolerance
                closestNode = node;
                closestDistance = distance;
            }
        }
        
        if (closestNode != null) {
            boolean success = false;
            if ("settlement".equals(buildingType)) {
                success = gameController.buildSettlement(closestNode.getNodeId());
            } else if ("city".equals(buildingType)) {
                success = gameController.buildCity(closestNode.getNodeId());
            }
            
            if (success) {
                refreshGameFieldDisplay();
                if (gameController.isBuildingModeActive()) {
                    gameController.stopBuildingMode();
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handle road placement
     * @return true if building was placed successfully
     */
    private boolean handleEdgeBuilding(double clickX, double clickY) {
        GameField gameField = gameController.getGameField();
        
        // Find the closest edge to the click
        Edge closestEdge = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Edge edge : gameField.getEdges()) {
            // Calculate distance to edge (line segment)
            double distance = distanceToLineSegment(clickX, clickY, 
                edge.getNode1().getX(), edge.getNode1().getY(),
                edge.getNode2().getX(), edge.getNode2().getY());
            
            if (distance < 10 && distance < closestDistance) { // 10 pixel tolerance
                closestEdge = edge;
                closestDistance = distance;
            }
        }
        
        if (closestEdge != null) {
            boolean success = gameController.buildRoad(closestEdge.getEdgeId());
            if (success) {
                refreshGameFieldDisplay();
                if (gameController.isBuildingModeActive()) {
                    gameController.stopBuildingMode();
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calculate distance from a point to a line segment
     */
    private double distanceToLineSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        if (length == 0) return Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));
        
        double t = Math.max(0, Math.min(1, ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / (length * length)));
        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);
        
        return Math.sqrt(Math.pow(px - projX, 2) + Math.pow(py - projY, 2));
    }
    
    /**
     * Refresh the game field display to show updated state
     */
    private void refreshGameFieldDisplay() {
        // Create new game field with updated state and building mode options
        boolean showPlacementOptions = gameController.isBuildingModeActive();
        BuildMode buildingMode = gameController.getCurrentBuildingMode();
        String buildingType = convertBuildModeToString(buildingMode);
        
        gameFieldGroup = gameController.getGameField().toGroup(showPlacementOptions, buildingType);
        setupGameFieldInteraction();
        
        // Update the scroll pane content
        gameFieldScrollPane.setContent(gameFieldGroup);
        
        // Apply centering immediately and set up listener for future changes
        centerGameField();
        setupGameFieldCentering();
    }
    
    /**
     * Center the game field within the scroll pane viewport
     */
    private void centerGameField() {
        double contentWidth = gameFieldGroup.getBoundsInLocal().getWidth();
        double contentHeight = gameFieldGroup.getBoundsInLocal().getHeight();
        double viewportWidth = gameFieldScrollPane.getViewportBounds().getWidth();
        double viewportHeight = gameFieldScrollPane.getViewportBounds().getHeight();
        
        // Center horizontally
        if (contentWidth < viewportWidth) {
            gameFieldGroup.setTranslateX((viewportWidth - contentWidth) / 2);
        } else {
            gameFieldGroup.setTranslateX(0);
        }
        
        // Center vertically
        if (contentHeight < viewportHeight) {
            gameFieldGroup.setTranslateY((viewportHeight - contentHeight) / 2);
        } else {
            gameFieldGroup.setTranslateY(0);
        }
    }
    
    /**
     * Setup centering listener for the game field
     */
    private void setupGameFieldCentering() {
        // Remove any existing listeners to avoid duplicates
        gameFieldScrollPane.viewportBoundsProperty().removeListener(this::handleViewportBoundsChange);
        
        // Add the centering listener
        gameFieldScrollPane.viewportBoundsProperty().addListener(this::handleViewportBoundsChange);
    }
    
    /**
     * Handle viewport bounds changes for centering
     */
    private void handleViewportBoundsChange(javafx.beans.value.ObservableValue<?> observable, 
                                           javafx.geometry.Bounds oldBounds, 
                                           javafx.geometry.Bounds newBounds) {
        centerGameField();
    }
    
    /**
     * Check if a point is inside a hexagon
     * @param clickX Click X coordinate
     * @param clickY Click Y coordinate
     * @param hexCenterX Hexagon center X
     * @param hexCenterY Hexagon center Y
     * @param hexRadius Hexagon radius
     * @return true if point is inside the hexagon
     */
    private boolean isPointInHexagon(double clickX, double clickY, double hexCenterX, double hexCenterY, double hexRadius) {
        double dx = clickX - hexCenterX;
        double dy = clickY - hexCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= hexRadius;
    }
    
    /**
     * Handle setup phase building clicks
     */
    private void handleSetupPhaseBuilding(MouseEvent event) {
        double clickX = event.getX();
        double clickY = event.getY();
        
        // For setup phase, prioritize settlement placement first, then roads
        // Try settlement placement first
        if (handleNodeBuilding(clickX, clickY, "settlement")) {
            return;
        }
        
        // If settlement placement failed, try road placement
        handleEdgeBuilding(clickX, clickY);
    }
}
