package de.philx.catan.Components;

import de.philx.catan.Controllers.GameController;
import de.philx.catan.Utils.StyledButton;
import de.philx.catan.Utils.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerInterface extends VBox {

    private final GameController gameController;
    private final Runnable onReturnToMenu;
<<<<<<< HEAD
    private final Runnable onGameFieldRefreshNeeded;
=======
    private final Runnable onGameStateChanged; // New callback for game state changes
>>>>>>> 8700dfc0315ca760c0c80ad728ffaa6e672109d4
    private final Label currentPlayerLabel;
    private final Label diceResultLabel;
    private final Label gameMessageLabel;
    private final Label resourcesLabel;
    private final Label buildingsLabel;
    private StyledButton diceButton;
    private StyledButton endTurnButton;
    private VBox playerInfoCard;
    private VBox gameStatusCard;
    private VBox controlsCard;
    
<<<<<<< HEAD
    public PlayerInterface(GameController gameController, Runnable onReturnToMenu) {
        this(gameController, onReturnToMenu, null);
    }
    
    public PlayerInterface(GameController gameController, Runnable onReturnToMenu, Runnable onGameFieldRefreshNeeded) {
        this.gameController = gameController;
        this.onReturnToMenu = onReturnToMenu;
        this.onGameFieldRefreshNeeded = onGameFieldRefreshNeeded;
=======
    public PlayerInterface(GameController gameController, Runnable onReturnToMenu, Runnable onGameStateChanged) {
        this.gameController = gameController;
        this.onReturnToMenu = onReturnToMenu;
        this.onGameStateChanged = onGameStateChanged;
>>>>>>> 8700dfc0315ca760c0c80ad728ffaa6e672109d4
        
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWidth(320);
        
        // Initialize labels
        currentPlayerLabel = new Label();
        currentPlayerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        currentPlayerLabel.textProperty().bind(gameController.currentPlayerProperty());
        
        diceResultLabel = new Label();
        diceResultLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        diceResultLabel.textProperty().bind(gameController.diceResultProperty());
        
        gameMessageLabel = new Label();
        gameMessageLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        gameMessageLabel.textProperty().bind(gameController.gameMessageProperty());
        gameMessageLabel.setWrapText(true);
        gameMessageLabel.setMaxWidth(280);
        
        resourcesLabel = new Label();
        resourcesLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        resourcesLabel.setWrapText(true);
        resourcesLabel.setMaxWidth(280);
        
        buildingsLabel = new Label();
        buildingsLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        buildingsLabel.setWrapText(true);
        buildingsLabel.setMaxWidth(280);
        
        // Create card components
        createPlayerInfoCard();
        createGameStatusCard();
        createControlsCard();
        
        // Add components with scroll support
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(15);
        content.getChildren().addAll(playerInfoCard, gameStatusCard, controlsCard);
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        this.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
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
    
    private void createPlayerInfoCard() {
        playerInfoCard = new VBox(12);
        
        // Card header
        Label cardTitle = new Label("👤 Spieler Information");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // Current player section
        VBox currentPlayerSection = new VBox(6);
        Label currentPlayerTitle = new Label("Aktueller Spieler:");
        currentPlayerTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        currentPlayerSection.getChildren().addAll(currentPlayerTitle, currentPlayerLabel);
        
        // Resources section
        VBox resourcesSection = new VBox(6);
        Label resourcesTitle = new Label("📦 Ressourcen:");
        resourcesTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        resourcesSection.getChildren().addAll(resourcesTitle, resourcesLabel);
        
        // Buildings section
        VBox buildingsSection = new VBox(6);
        Label buildingsTitle = new Label("🏠 Gebäude:");
        buildingsTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        buildingsSection.getChildren().addAll(buildingsTitle, buildingsLabel);
        
        playerInfoCard.getChildren().addAll(
            cardTitle, currentPlayerSection, resourcesSection, buildingsSection
        );
    }
    
    private void createGameStatusCard() {
        gameStatusCard = new VBox(12);
        
        // Card header
        Label cardTitle = new Label("🎮 Spielstatus");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // Dice result section
        VBox diceSection = new VBox(6);
        Label diceTitle = new Label("🎲 Letzter Würfelwurf:");
        diceTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        diceSection.getChildren().addAll(diceTitle, diceResultLabel);
        
        // Game messages section
        VBox messagesSection = new VBox(6);
        Label messagesTitle = new Label("📢 Nachrichten:");
        messagesTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        messagesSection.getChildren().addAll(messagesTitle, gameMessageLabel);
        
        gameStatusCard.getChildren().addAll(cardTitle, diceSection, messagesSection);
    }
    
    private void createControlsCard() {
        controlsCard = new VBox(15);
        
        // Card header
        Label cardTitle = new Label("⚙️ Spielsteuerung");
        cardTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // Primary actions
        VBox primaryActions = new VBox(10);
        
        diceButton = new StyledButton("🎲 Würfeln", StyledButton.ButtonType.PRIMARY);
        diceButton.setPrefWidth(250);
        diceButton.setOnAction(e -> handleDiceRoll());
        
        endTurnButton = new StyledButton("⏭️ Zug beenden", StyledButton.ButtonType.SUCCESS);
        endTurnButton.setPrefWidth(250);
        endTurnButton.setOnAction(e -> handleEndTurn());
        
        primaryActions.getChildren().addAll(diceButton, endTurnButton);
        
        // Secondary actions
        VBox secondaryActions = new VBox(8);
        
        StyledButton tradeButton = new StyledButton("💰 Handeln", StyledButton.ButtonType.SECONDARY);
        tradeButton.setPrefWidth(250);
        tradeButton.setOnAction(e -> handleTrade());
        
        StyledButton buildButton = new StyledButton("🏗️ Bauen", StyledButton.ButtonType.SECONDARY);
        buildButton.setPrefWidth(250);
        buildButton.setOnAction(e -> handleBuild());
        
        secondaryActions.getChildren().addAll(tradeButton, buildButton);
        
        // Navigation
        VBox navigationActions = new VBox(8);
        
        StyledButton quitButton = new StyledButton("🏠 Hauptmenü", StyledButton.ButtonType.DANGER);
        quitButton.setPrefWidth(250);
        quitButton.setOnAction(e -> handleQuit());
        
        navigationActions.getChildren().add(quitButton);
        
        controlsCard.getChildren().addAll(
            cardTitle, primaryActions, secondaryActions, navigationActions
        );
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
        // Show trading interface
        TradingInterface.showTradingWindow(
            gameController.getTradeController(), 
            gameController.getPlayerManager(), 
            gameController.getCurrentPlayer().getPlayerId(), 
            this::updateDisplays
        );
    }
    
    private void handleBuild() {
<<<<<<< HEAD
        // Show enhanced building interface with game field refresh callback
        Runnable refreshCallback = () -> {
            updateDisplays();
            if (onGameFieldRefreshNeeded != null) {
                onGameFieldRefreshNeeded.run();
            }
        };
        BuildingInterface.showBuildingWindow(gameController, refreshCallback);
=======
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
>>>>>>> 8700dfc0315ca760c0c80ad728ffaa6e672109d4
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
     * Apply the current theme to all components in the PlayerInterface
     */
    public void applyCurrentTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply card styling to each card
        if (playerInfoCard != null) {
            playerInfoCard.setStyle(themeManager.getCardStyle());
        }
        if (gameStatusCard != null) {
            gameStatusCard.setStyle(themeManager.getCardStyle());
        }
        if (controlsCard != null) {
            controlsCard.setStyle(themeManager.getCardStyle());
        }
        
        // Update text colors for dynamic labels
        String textColor = themeManager.getTextColor();
        String secondaryTextColor = themeManager.getSecondaryTextColor();
        
        currentPlayerLabel.setStyle("-fx-text-fill: " + textColor + ";");
        diceResultLabel.setStyle("-fx-text-fill: " + textColor + ";");
        gameMessageLabel.setStyle("-fx-text-fill: " + secondaryTextColor + ";");
        resourcesLabel.setStyle("-fx-text-fill: " + textColor + ";");
        buildingsLabel.setStyle("-fx-text-fill: " + textColor + ";");
        
        // Recursively update all labels in the cards
        updateLabelsInCard(playerInfoCard, textColor, secondaryTextColor);
        updateLabelsInCard(gameStatusCard, textColor, secondaryTextColor);
        updateLabelsInCard(controlsCard, textColor, secondaryTextColor);
    }
    
    private void updateLabelsInCard(VBox card, String textColor, String secondaryTextColor) {
        if (card == null) return;
        
        card.getChildren().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                String labelText = label.getText();
                
                // Apply different colors based on label type
                if (labelText.contains("👤") || labelText.contains("🎮") || labelText.contains("⚙️")) {
                    // Card titles - use accent color
                    label.setStyle("-fx-text-fill: " + ThemeManager.ACCENT_COLOR + ";");
                } else if (labelText.endsWith(":")) {
                    // Section titles - use secondary color
                    label.setStyle("-fx-text-fill: " + secondaryTextColor + ";");
                } else {
                    // Regular text - use primary text color
                    label.setStyle("-fx-text-fill: " + textColor + ";");
                }
            } else if (node instanceof VBox) {
                updateLabelsInCard((VBox) node, textColor, secondaryTextColor);
            }
        });
    }
}
