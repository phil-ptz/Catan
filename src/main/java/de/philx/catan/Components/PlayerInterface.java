package de.philx.catan.Components;

import de.philx.catan.Controllers.GameController;
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
    private final Label currentPlayerLabel;
    private final Label diceResultLabel;
    private final Label gameMessageLabel;
    private final Label resourcesLabel;
    private final Label buildingsLabel;
    private Button diceButton;
    private Button endTurnButton;
    
    public PlayerInterface(GameController gameController, Runnable onReturnToMenu) {
        this.gameController = gameController;
        this.onReturnToMenu = onReturnToMenu;
        
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
        // Placeholder for building functionality
        System.out.println("Bauen - noch nicht implementiert");
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
    }
}
