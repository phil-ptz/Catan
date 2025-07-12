package de.philx.catan.Components;

import de.philx.catan.Controllers.GameController;
import de.philx.catan.Utils.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerInterface extends VBox {

    private final GameController gameController;
    private final Runnable onReturnToMenu;
    private final Runnable onGameStateChanged; // New callback for game state changes
    private final Label currentPlayerLabel;
    private final Label diceResultLabel;
    private final Label gameMessageLabel;
    private final Label resourcesLabel;
    private final Label buildingsLabel;
    private Button diceButton;
    private Button endTurnButton;
    
    public PlayerInterface(GameController gameController, Runnable onReturnToMenu, Runnable onGameStateChanged) {
        this.gameController = gameController;
        this.onReturnToMenu = onReturnToMenu;
        this.onGameStateChanged = onGameStateChanged;
        
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPrefWidth(300);
        
        // Current player display
        currentPlayerLabel = new Label();
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        currentPlayerLabel.textProperty().bind(gameController.currentPlayerProperty());
        
        // Dice result display
        diceResultLabel = new Label();
        diceResultLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        diceResultLabel.textProperty().bind(gameController.diceResultProperty());
        
        // Game message display
        gameMessageLabel = new Label();
        gameMessageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        gameMessageLabel.textProperty().bind(gameController.gameMessageProperty());
        gameMessageLabel.setWrapText(true);
        gameMessageLabel.setMaxWidth(280);
        
        // Resources display
        resourcesLabel = new Label();
        resourcesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        resourcesLabel.setWrapText(true);
        resourcesLabel.setMaxWidth(280);
        
        // Buildings display
        buildingsLabel = new Label();
        buildingsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        buildingsLabel.setWrapText(true);
        buildingsLabel.setMaxWidth(280);
        
        // Create button grid
        GridPane buttonGrid = createButtonGrid();
        
        // Add all components
        this.getChildren().addAll(
            new Label("Aktueller Spieler:"),
            currentPlayerLabel,
            new Label("Würfelergebnis:"),
            diceResultLabel,
            new Label("Spielnachrichten:"),
            gameMessageLabel,
            new Label("Ressourcen:"),
            resourcesLabel,
            new Label("Gebäude:"),
            buildingsLabel,
            buttonGrid
        );
        
        // Apply current theme when interface is created
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyCurrentTheme();
            }
        });
        
        // Register for theme change notifications
        ThemeManager.getInstance().addThemeChangeListener(this::applyCurrentTheme);
        
        // Start updating displays
        startPeriodicUpdates();
    }
    
    private GridPane createButtonGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        
        // Column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        
        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        row2.setVgrow(Priority.ALWAYS);
        row3.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().addAll(row1, row2, row3);
        
        // Dice roll button
        diceButton = new Button("Würfeln");
        diceButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        diceButton.setOnAction(e -> handleDiceRoll());
        grid.add(diceButton, 0, 0);
        
        // End turn button
        endTurnButton = new Button("Zug beenden");
        endTurnButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        endTurnButton.setOnAction(e -> handleEndTurn());
        grid.add(endTurnButton, 1, 0);
        
        // Trade button
        Button tradeButton = new Button("Handeln");
        tradeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tradeButton.setOnAction(e -> handleTrade());
        grid.add(tradeButton, 0, 1);
        
        // Build button
        Button buildButton = new Button("Bauen");
        buildButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buildButton.setOnAction(e -> handleBuild());
        grid.add(buildButton, 1, 1);
        
        // Quit button
        Button quitButton = new Button("Hauptmenü");
        quitButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        quitButton.setOnAction(e -> handleQuit());
        grid.add(quitButton, 0, 2, 2, 1); // Span 2 columns
        
        return grid;
    }
    
    private void handleDiceRoll() {
        int result = gameController.rollDice();
        if (result > 0) {
            updateDisplays();
        }
    }
    
    private void handleEndTurn() {
        gameController.endTurn();
        updateDisplays();
    }
    
    private void handleTrade() {
        // Placeholder for trading functionality
        System.out.println("Handel - noch nicht implementiert");
    }
    
    private void handleBuild() {
        // Show building options dialog or cycle through building types
        showBuildingOptionsDialog();
    }
    
    /**
     * Show building options to the player
     */
    private void showBuildingOptionsDialog() {
        // Create a simple selection dialog for building types
        javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>("Straße", "Straße", "Siedlung", "Stadt");
        dialog.setTitle("Gebäude bauen");
        dialog.setHeaderText("Wähle was du bauen möchtest:");
        dialog.setContentText("Gebäudetyp:");
        
        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(buildingType -> {
            String mode = null;
            switch (buildingType) {
                case "Straße": mode = "road"; break;
                case "Siedlung": mode = "settlement"; break;
                case "Stadt": mode = "city"; break;
            }
            
            if (mode != null) {
                gameController.startBuildingMode(mode);
                // Trigger refresh of game field
                if (onGameStateChanged != null) {
                    onGameStateChanged.run();
                }
                updateDisplays();
            }
        });
    }
    
    private void handleQuit() {
        // Return to main menu
        if (onReturnToMenu != null) {
            onReturnToMenu.run();
        }
    }
    
    private void startPeriodicUpdates() {
        // Create a simple timer to update displays periodically
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> updateDisplays())
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void updateDisplays() {
        resourcesLabel.setText(gameController.getCurrentPlayerResources());
        buildingsLabel.setText(gameController.getCurrentPlayerBuildings());
        
        // Update button states based on game state
        diceButton.setDisable(!gameController.canCurrentPlayerRollDice());
        endTurnButton.setDisable(!gameController.getPlayerManager().isGameStarted());
    }
    
    /**
     * Apply the current theme to all labels in the PlayerInterface
     */
    public void applyCurrentTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        String textColor = themeManager.isDarkMode() ? "#ffffff" : "#000000";
        
        // Apply text color to all labels
        currentPlayerLabel.setStyle("-fx-text-fill: " + textColor + ";");
        diceResultLabel.setStyle("-fx-text-fill: " + textColor + ";");
        gameMessageLabel.setStyle("-fx-text-fill: " + textColor + ";");
        resourcesLabel.setStyle("-fx-text-fill: " + textColor + ";");
        buildingsLabel.setStyle("-fx-text-fill: " + textColor + ";");
        
        // Apply text color to all static labels
        this.getChildren().forEach(node -> {
            if (node instanceof Label && 
                (((Label) node).getText().equals("Aktueller Spieler:") ||
                 ((Label) node).getText().equals("Würfelergebnis:") ||
                 ((Label) node).getText().equals("Spielnachrichten:") ||
                 ((Label) node).getText().equals("Ressourcen:") ||
                 ((Label) node).getText().equals("Gebäude:"))) {
                node.setStyle("-fx-text-fill: " + textColor + ";");
            }
        });
    }
}
