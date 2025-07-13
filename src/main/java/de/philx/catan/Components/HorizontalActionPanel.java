package de.philx.catan.Components;

import de.philx.catan.Controllers.GameController;
import de.philx.catan.Utils.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Horizontal action panel that appears at the bottom of the screen
 * Contains collapsible cards for trading and building interfaces
 */
public class HorizontalActionPanel extends VBox {
    
    private final GameController gameController;
    private final Runnable onActionCompleted;
    
    // Main components
    private HBox mainPanel;
    private VBox currentCard;
    private Label titleLabel;
    
    // Interface components
    private TradingInterface tradingInterface;
    private BuildingInterface buildingInterface;
    
    // Animation components
    private FadeTransition fadeTransition;
    private ScaleTransition scaleTransition;
    
    public HorizontalActionPanel(GameController gameController, Runnable onActionCompleted) {
        this.gameController = gameController;
        this.onActionCompleted = onActionCompleted;
        
        initializeComponents();
        setupLayout();
        setupAnimations();
        applyTheme();
        
        // Register for theme changes
        ThemeManager.getInstance().addThemeChangeListener(this::applyTheme);
    }
    
    private void initializeComponents() {
        mainPanel = new HBox(15);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(15));
        
        titleLabel = new Label();
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titleLabel.setVisible(false);
    }
    
    private void setupLayout() {
        this.setAlignment(Pos.CENTER);
        this.setPrefHeight(200); // Compact height for horizontal layout
        this.setMaxHeight(300);
        this.setVisible(false); // Initially hidden
        
        // Main panel will contain the active interface
        this.getChildren().addAll(titleLabel, mainPanel);
    }
    
    private void setupAnimations() {
        // Fade transition for showing/hiding
        fadeTransition = new FadeTransition(Duration.millis(300), this);
        
        // Scale transition for smooth appearance
        scaleTransition = new ScaleTransition(Duration.millis(200), this);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToY(1.0);
    }
    
    /**
     * Show the trading interface
     */
    public void showTradingInterface() {
        if (gameController.getCurrentPlayer() == null) {
            return;
        }
        
        // Create trading interface if needed
        if (tradingInterface == null) {
            tradingInterface = new TradingInterface(
                gameController.getTradeController(),
                gameController.getPlayerManager(),
                gameController.getCurrentPlayer().getPlayerId(),
                this::hidePanel
            );
            // Adapt for horizontal layout
            adaptTradingInterfaceForHorizontalLayout();
        }
        
        showInterface(tradingInterface, "💰 Handel");
    }
    
    /**
     * Show the building interface
     */
    public void showBuildingInterface() {
        if (gameController.getCurrentPlayer() == null) {
            return;
        }
        
        // Create building interface if needed
        if (buildingInterface == null) {
            buildingInterface = new BuildingInterface(gameController, this::hidePanel);
            // Adapt for horizontal layout
            adaptBuildingInterfaceForHorizontalLayout();
        }
        
        showInterface(buildingInterface, "🔨 Bauen");
    }
    
    /**
     * Hide the current panel
     */
    public void hidePanel() {
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(e -> {
            this.setVisible(false);
            mainPanel.getChildren().clear();
            currentCard = null;
            titleLabel.setVisible(false);
            if (onActionCompleted != null) {
                onActionCompleted.run();
            }
        });
        fadeTransition.play();
    }
    
    /**
     * Show a specific interface
     */
    private void showInterface(VBox interfaceComponent, String title) {
        // Hide current interface if any
        if (currentCard != null) {
            mainPanel.getChildren().clear();
        }
        
        currentCard = interfaceComponent;
        titleLabel.setText(title);
        titleLabel.setVisible(true);
        
        // Add interface to main panel
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(currentCard);
        
        // Show panel with animation
        this.setVisible(true);
        this.setOpacity(0.0);
        
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setOnFinished(null);
        
        fadeTransition.play();
        scaleTransition.play();
    }
    
    /**
     * Adapt trading interface for horizontal layout
     */
    private void adaptTradingInterfaceForHorizontalLayout() {
        tradingInterface.setupHorizontalLayout();
        tradingInterface.setPrefWidth(1000);
        tradingInterface.setMaxWidth(1200);
        tradingInterface.setPrefHeight(150);
    }
    
    /**
     * Adapt building interface for horizontal layout
     */
    private void adaptBuildingInterfaceForHorizontalLayout() {
        buildingInterface.setupHorizontalLayout();
        buildingInterface.setPrefWidth(800);
        buildingInterface.setMaxWidth(1000);
        buildingInterface.setPrefHeight(150);
    }
    
    /**
     * Check if panel is currently visible
     */
    public boolean isPanelVisible() {
        return this.isVisible();
    }
    
    /**
     * Refresh the current interface
     */
    public void refreshCurrentInterface() {
        if (currentCard == tradingInterface && tradingInterface != null) {
            // Update trading interface display
            tradingInterface.updateDisplay();
        } else if (currentCard == buildingInterface && buildingInterface != null) {
            // Update building interface display
            buildingInterface.refresh();
        }
    }
    
    private void applyTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply theme to main panel
        this.setStyle(themeManager.getCardStyle() + 
                     "-fx-border-color: #007ACC;" +
                     "-fx-border-width: 2px;" +
                     "-fx-border-radius: 10px;" +
                     "-fx-background-radius: 10px;");
        
        // Apply theme to title
        if (titleLabel != null) {
            titleLabel.setStyle("-fx-text-fill: " + themeManager.getTextColor() + ";");
        }
        
        // Apply theme to main panel
        if (mainPanel != null) {
            mainPanel.setStyle("-fx-background-color: transparent;");
        }
    }
}
