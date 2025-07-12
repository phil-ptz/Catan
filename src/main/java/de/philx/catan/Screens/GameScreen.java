package de.philx.catan.Screens;

import de.philx.catan.Components.GameLegend;
import de.philx.catan.Components.PlayerInterface;
import de.philx.catan.Controllers.GameController;
import de.philx.catan.GameField.GameField;
import de.philx.catan.Utils.ThemeManager;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GameScreen extends HBox {

    private final GameController gameController;
    private Group gameFieldGroup;
    private final PlayerInterface playerInterface;
    private final GameLegend gameLegend;
    private VBox leftPanel;

    public GameScreen(int width, int height, Runnable onReturnToMenu) {
        this.gameController = new GameController();
        this.gameFieldGroup = gameController.getGameField().toGroup();
        this.playerInterface = new PlayerInterface(gameController, onReturnToMenu, this::refreshGameFieldDisplay);
        this.gameLegend = new GameLegend();
        
        this.setPrefSize(width, height);
        this.setSpacing(10);
        
        // Add click handler for robber placement
        setupGameFieldClickHandler();
        
        // Left side: Game board and player interface
        leftPanel = new VBox(10);
        leftPanel.setAlignment(javafx.geometry.Pos.CENTER);
        leftPanel.getChildren().add(gameFieldGroup);
        leftPanel.getChildren().add(playerInterface);
        VBox.setVgrow(playerInterface, Priority.ALWAYS);
        
        // Right side: Legend
        VBox rightPanel = new VBox();
        rightPanel.getChildren().add(gameLegend);
        rightPanel.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        
        // Add both panels to the main HBox
        this.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        
        // Apply current theme when screen is created
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                ThemeManager.getInstance().applyTheme(newScene);
            }
        });
    }
    
    /**
     * Setup click handler for hexagon selection (robber placement)
     */
    private void setupGameFieldClickHandler() {
        gameFieldGroup.setOnMouseClicked(this::handleGameFieldClick);
    }
    
    /**
     * Handle clicks on the game field
     * @param event The mouse click event
     */
    private void handleGameFieldClick(MouseEvent event) {
        // Check for robber placement first
        if (gameController.isWaitingForRobberPlacement()) {
            handleRobberPlacement(event);
            return;
        }
        
        // Check for building placement
        if (gameController.isBuildingModeActive()) {
            handleBuildingPlacement(event);
            return;
        }
    }
    
    /**
     * Handle robber placement clicks
     */
    private void handleRobberPlacement(MouseEvent event) {
        // Find which hexagon was clicked
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
     * Handle building placement clicks
     */
    private void handleBuildingPlacement(MouseEvent event) {
        // Check if any clickable element was clicked
        if (event.getTarget() instanceof javafx.scene.Node) {
            javafx.scene.Node target = (javafx.scene.Node) event.getTarget();
            Object userData = target.getUserData();
            
            if (userData instanceof String) {
                String elementId = (String) userData;
                if (elementId.startsWith("node_") || elementId.startsWith("edge_")) {
                    boolean success = gameController.handleBuildingPlacement(elementId);
                    if (success) {
                        refreshGameFieldDisplay();
                    }
                }
            }
        }
    }
    
    /**
     * Refresh the game field display to show updated state
     */
    private void refreshGameFieldDisplay() {
        // Remove old game field
        leftPanel.getChildren().remove(gameFieldGroup);
        
        // Create new game field with updated state and building mode
        boolean showPlacementOptions = gameController.isBuildingModeActive();
        String buildingType = gameController.getCurrentBuildingMode();
        gameFieldGroup = gameController.getGameField().toGroup(showPlacementOptions, buildingType);
        setupGameFieldClickHandler();
        
        // Add updated game field back
        leftPanel.getChildren().add(0, gameFieldGroup);
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
}
