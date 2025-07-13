package de.philx.catan.Components;

import de.philx.catan.Controllers.TradeController;
import de.philx.catan.Players.Player;
import de.philx.catan.Players.Player.ResourceType;
import de.philx.catan.Players.PlayerManager;
import de.philx.catan.Utils.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Trading interface for player-to-player and bank trading
 */
public class TradingInterface extends VBox {
    
    private final TradeController tradeController;
    private final PlayerManager playerManager;
    private final int currentPlayerId;
    private final Runnable onTradeCompleted;
    
    // UI Components
    private TabPane tabPane;
    private Tab playerTradeTab;
    private Tab bankTradeTab;
    
    // Player trade components
    private ComboBox<Player> targetPlayerCombo;
    private Map<ResourceType, Spinner<Integer>> offeredSpinners;
    private Map<ResourceType, Spinner<Integer>> requestedSpinners;
    private Button createOfferButton;
    private Button acceptOfferButton;
    private Button declineOfferButton;
    
    // Bank trade components
    private ComboBox<ResourceType> bankGiveCombo;
    private Spinner<Integer> bankGiveSpinner;
    private ComboBox<ResourceType> bankWantCombo;
    private Button executeBankTradeButton;
    
    // Display components
    private Label tradeStatusLabel;
    private Label currentOfferLabel;
    
    public TradingInterface(TradeController tradeController, PlayerManager playerManager, 
                           int currentPlayerId, Runnable onTradeCompleted) {
        this.tradeController = tradeController;
        this.playerManager = playerManager;
        this.currentPlayerId = currentPlayerId;
        this.onTradeCompleted = onTradeCompleted;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateDisplay();
        
        // Register for theme changes
        ThemeManager.getInstance().addThemeChangeListener(this::applyTheme);
        applyTheme();
    }
    
    private void initializeComponents() {
        // Tab pane
        tabPane = new TabPane();
        playerTradeTab = new Tab("Spieler-Handel");
        bankTradeTab = new Tab("Bank-Handel");
        
        playerTradeTab.setClosable(false);
        bankTradeTab.setClosable(false);
        
        // Player trade components
        targetPlayerCombo = new ComboBox<>();
        targetPlayerCombo.getItems().addAll(tradeController.getPossibleTradePartners(currentPlayerId));
        targetPlayerCombo.setConverter(new StringConverter<Player>() {
            @Override
            public String toString(Player player) {
                return player != null ? player.getName() : "";
            }
            
            @Override
            public Player fromString(String string) {
                return null; // Not needed for ComboBox
            }
        });
        
        // Initialize spinners for resources
        offeredSpinners = new HashMap<>();
        requestedSpinners = new HashMap<>();
        
        for (ResourceType resource : ResourceType.values()) {
            offeredSpinners.put(resource, createResourceSpinner());
            requestedSpinners.put(resource, createResourceSpinner());
        }
        
        // Buttons
        createOfferButton = new Button("Handelsangebot erstellen");
        acceptOfferButton = new Button("Angebot annehmen");
        declineOfferButton = new Button("Angebot ablehnen");
        
        // Bank trade components
        bankGiveCombo = new ComboBox<>();
        bankGiveCombo.getItems().addAll(ResourceType.values());
        
        bankWantCombo = new ComboBox<>();
        bankWantCombo.getItems().addAll(ResourceType.values());
        
        bankGiveSpinner = new Spinner<>(0, 20, 4);
        bankGiveSpinner.setEditable(true);
        
        executeBankTradeButton = new Button("Bank-Handel ausführen");
        
        // Display labels
        tradeStatusLabel = new Label();
        tradeStatusLabel.textProperty().bind(tradeController.tradeMessageProperty());
        tradeStatusLabel.setWrapText(true);
        
        currentOfferLabel = new Label();
        currentOfferLabel.setWrapText(true);
    }
    
    private Spinner<Integer> createResourceSpinner() {
        Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
        spinner.setEditable(true);
        spinner.setPrefWidth(80);
        return spinner;
    }
    
    private void setupLayout() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        
        // Status display
        VBox statusBox = new VBox(5);
        statusBox.getChildren().addAll(
            new Label("Handelsstatus:"),
            tradeStatusLabel,
            currentOfferLabel
        );
        
        // Player trade tab content
        VBox playerTradeContent = createPlayerTradeContent();
        playerTradeTab.setContent(playerTradeContent);
        
        // Bank trade tab content
        VBox bankTradeContent = createBankTradeContent();
        bankTradeTab.setContent(bankTradeContent);
        
        tabPane.getTabs().addAll(playerTradeTab, bankTradeTab);
        
        this.getChildren().addAll(statusBox, tabPane);
    }
    
    private VBox createPlayerTradeContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(10));
        
        // Target player selection
        HBox targetBox = new HBox(10);
        targetBox.setAlignment(Pos.CENTER_LEFT);
        targetBox.getChildren().addAll(
            new Label("Handel mit:"),
            targetPlayerCombo
        );
        
        // Offered resources section
        VBox offeredSection = createResourceSection("Angebotene Ressourcen:", offeredSpinners);
        
        // Requested resources section
        VBox requestedSection = createResourceSection("Gewünschte Ressourcen:", requestedSpinners);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(createOfferButton, acceptOfferButton, declineOfferButton);
        
        content.getChildren().addAll(targetBox, offeredSection, requestedSection, buttonBox);
        return content;
    }
    
    private VBox createBankTradeContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(10));
        
        // Instructions
        Label instructionLabel = new Label("Bank-Handel: Tausche 4 gleiche Ressourcen gegen 1 beliebige Ressource");
        instructionLabel.setWrapText(true);
        instructionLabel.setFont(Font.font(12));
        
        // Give section
        HBox giveBox = new HBox(10);
        giveBox.setAlignment(Pos.CENTER_LEFT);
        giveBox.getChildren().addAll(
            new Label("Abgeben:"),
            bankGiveSpinner,
            bankGiveCombo
        );
        
        // Want section
        HBox wantBox = new HBox(10);
        wantBox.setAlignment(Pos.CENTER_LEFT);
        wantBox.getChildren().addAll(
            new Label("Erhalten:"),
            new Label("1x"),
            bankWantCombo
        );
        
        // Trade ratio display
        Label ratioLabel = new Label("Tauschverhältnis: 4:1");
        ratioLabel.setFont(Font.font(12));
        
        // Execute button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(executeBankTradeButton);
        
        content.getChildren().addAll(instructionLabel, giveBox, wantBox, ratioLabel, buttonBox);
        return content;
    }
    
    private VBox createResourceSection(String title, Map<ResourceType, Spinner<Integer>> spinners) {
        VBox section = new VBox(5);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        
        int row = 0;
        for (ResourceType resource : ResourceType.values()) {
            Label resourceLabel = new Label(getResourceName(resource) + ":");
            Spinner<Integer> spinner = spinners.get(resource);
            
            grid.add(resourceLabel, 0, row);
            grid.add(spinner, 1, row);
            row++;
        }
        
        section.getChildren().addAll(titleLabel, grid);
        return section;
    }
    
    private void setupEventHandlers() {
        createOfferButton.setOnAction(e -> createTradeOffer());
        acceptOfferButton.setOnAction(e -> acceptCurrentOffer());
        declineOfferButton.setOnAction(e -> declineCurrentOffer());
        executeBankTradeButton.setOnAction(e -> executeBankTrade());
        
        // Update bank trade ratio display when resource selection changes
        bankGiveCombo.setOnAction(e -> updateBankTradeDisplay());
    }
    
    private void createTradeOffer() {
        Player targetPlayer = targetPlayerCombo.getSelectionModel().getSelectedItem();
        if (targetPlayer == null) {
            showAlert("Fehler", "Bitte wähle einen Spieler für den Handel aus.");
            return;
        }
        
        Map<ResourceType, Integer> offered = getResourceAmounts(offeredSpinners);
        Map<ResourceType, Integer> requested = getResourceAmounts(requestedSpinners);
        
        if (offered.isEmpty()) {
            showAlert("Fehler", "Du musst mindestens eine Ressource anbieten.");
            return;
        }
        
        if (requested.isEmpty()) {
            showAlert("Fehler", "Du musst mindestens eine Ressource anfordern.");
            return;
        }
        
        boolean success = tradeController.createTradeOffer(currentPlayerId, targetPlayer.getPlayerId(), offered, requested);
        if (success) {
            updateDisplay();
            if (onTradeCompleted != null) {
                onTradeCompleted.run();
            }
        }
    }
    
    private void acceptCurrentOffer() {
        boolean success = tradeController.acceptTrade(currentPlayerId);
        if (success) {
            updateDisplay();
            clearSpinners();
            if (onTradeCompleted != null) {
                onTradeCompleted.run();
            }
        }
    }
    
    private void declineCurrentOffer() {
        boolean success = tradeController.declineTrade(currentPlayerId);
        if (success) {
            updateDisplay();
            if (onTradeCompleted != null) {
                onTradeCompleted.run();
            }
        }
    }
    
    private void executeBankTrade() {
        ResourceType giveResource = bankGiveCombo.getSelectionModel().getSelectedItem();
        ResourceType wantResource = bankWantCombo.getSelectionModel().getSelectedItem();
        int giveAmount = bankGiveSpinner.getValue();
        
        if (giveResource == null || wantResource == null) {
            showAlert("Fehler", "Bitte wähle beide Ressourcentypen aus.");
            return;
        }
        
        if (giveResource == wantResource) {
            showAlert("Fehler", "Du kannst nicht die gleiche Ressource tauschen.");
            return;
        }
        
        boolean success = tradeController.executeBankTrade(currentPlayerId, giveResource, giveAmount, wantResource);
        if (success) {
            updateDisplay();
            if (onTradeCompleted != null) {
                onTradeCompleted.run();
            }
        }
    }
    
    private Map<ResourceType, Integer> getResourceAmounts(Map<ResourceType, Spinner<Integer>> spinners) {
        Map<ResourceType, Integer> amounts = new HashMap<>();
        for (Map.Entry<ResourceType, Spinner<Integer>> entry : spinners.entrySet()) {
            int amount = entry.getValue().getValue();
            if (amount > 0) {
                amounts.put(entry.getKey(), amount);
            }
        }
        return amounts;
    }
    
    private void clearSpinners() {
        for (Spinner<Integer> spinner : offeredSpinners.values()) {
            spinner.getValueFactory().setValue(0);
        }
        for (Spinner<Integer> spinner : requestedSpinners.values()) {
            spinner.getValueFactory().setValue(0);
        }
    }
    
    public void updateDisplay() {
        // Update current offer display
        TradeController.TradeOffer currentOffer = tradeController.getCurrentOffer();
        if (currentOffer != null) {
            Player offerer = playerManager.getPlayerById(currentOffer.getOffererPlayerId());
            Player target = playerManager.getPlayerById(currentOffer.getTargetPlayerId());
            
            String offerText = String.format("Aktuelles Angebot: %s bietet %s → %s an %s",
                offerer.getName(),
                formatResources(currentOffer.getOfferedResources()),
                formatResources(currentOffer.getRequestedResources()),
                target.getName());
            
            currentOfferLabel.setText(offerText);
            
            // Show/hide buttons based on current player
            boolean isTarget = currentOffer.getTargetPlayerId() == currentPlayerId;
            
            acceptOfferButton.setVisible(isTarget);
            declineOfferButton.setVisible(isTarget);
            createOfferButton.setDisable(currentOffer != null);
        } else {
            currentOfferLabel.setText("Kein aktives Handelsangebot");
            acceptOfferButton.setVisible(false);
            declineOfferButton.setVisible(false);
            createOfferButton.setDisable(false);
        }
        
        updateBankTradeDisplay();
    }
    
    private void updateBankTradeDisplay() {
        ResourceType selectedResource = bankGiveCombo.getSelectionModel().getSelectedItem();
        if (selectedResource != null) {
            int ratio = tradeController.getBankTradeRatio(selectedResource);
            bankGiveSpinner.getValueFactory().setValue(ratio);
            
            // Find and update ratio label
            VBox bankContent = (VBox) bankTradeTab.getContent();
            for (javafx.scene.Node node : bankContent.getChildren()) {
                if (node instanceof Label && ((Label) node).getText().startsWith("Tauschverhältnis:")) {
                    ((Label) node).setText("Tauschverhältnis: " + ratio + ":1");
                    break;
                }
            }
        }
    }
    
    private String formatResources(Map<ResourceType, Integer> resources) {
        if (resources.isEmpty()) {
            return "nichts";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getValue()).append("x ").append(getResourceName(entry.getKey()));
            first = false;
        }
        return sb.toString();
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
        
        // Apply theme to labels
        tradeStatusLabel.setStyle("-fx-text-fill: " + themeManager.getTextColor() + ";");
        currentOfferLabel.setStyle("-fx-text-fill: " + themeManager.getTextColor() + ";");
    }
    
    /**
     * Show trading interface in a new window
     */
    public static void showTradingWindow(TradeController tradeController, PlayerManager playerManager, 
                                       int currentPlayerId, Runnable onTradeCompleted) {
        Stage stage = new Stage();
        stage.setTitle("Handel");
        stage.initModality(Modality.APPLICATION_MODAL);
        
        TradingInterface tradingInterface = new TradingInterface(tradeController, playerManager, currentPlayerId, onTradeCompleted);
        
        Scene scene = new Scene(new ScrollPane(tradingInterface), 500, 600);
        ThemeManager.getInstance().applyTheme(scene);
        
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Create a compact horizontal layout for the trading interface
     */
    public void setupHorizontalLayout() {
        this.getChildren().clear();
        this.setSpacing(15);
        this.setPadding(new Insets(10));
        this.setMaxHeight(180);
        
        // Create horizontal main container
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        
        // Status section (compact)
        VBox statusSection = new VBox(5);
        statusSection.setPrefWidth(200);
        statusSection.setMaxWidth(200);
        
        Label statusTitle = new Label("📊 Status");
        statusTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusSection.getChildren().addAll(statusTitle, tradeStatusLabel, currentOfferLabel);
        
        // Trading content in horizontal tabs
        TabPane compactTabPane = new TabPane();
        compactTabPane.setPrefWidth(600);
        compactTabPane.setMaxHeight(150);
        
        // Compact player trade tab
        Tab compactPlayerTab = new Tab("👥 Spieler");
        compactPlayerTab.setClosable(false);
        compactPlayerTab.setContent(createCompactPlayerTradeContent());
        
        // Compact bank trade tab  
        Tab compactBankTab = new Tab("🏦 Bank");
        compactBankTab.setClosable(false);
        compactBankTab.setContent(createCompactBankTradeContent());
        
        compactTabPane.getTabs().addAll(compactPlayerTab, compactBankTab);
        
        // Action buttons section
        VBox actionsSection = new VBox(8);
        actionsSection.setPrefWidth(180);
        actionsSection.setAlignment(Pos.CENTER);
        
        Label actionsTitle = new Label("⚡ Aktionen");
        actionsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        
        VBox buttonBox = new VBox(5);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(createOfferButton, acceptOfferButton, declineOfferButton);
        
        actionsSection.getChildren().addAll(actionsTitle, buttonBox);
        
        mainContainer.getChildren().addAll(statusSection, compactTabPane, actionsSection);
        this.getChildren().add(mainContainer);
    }
    
    private VBox createCompactPlayerTradeContent() {
        VBox content = new VBox(8);
        content.setPadding(new Insets(5));
        
        // Target player selection (compact)
        HBox targetBox = new HBox(5);
        targetBox.setAlignment(Pos.CENTER_LEFT);
        targetBox.getChildren().addAll(
            new Label("Mit:"),
            targetPlayerCombo
        );
        targetPlayerCombo.setPrefWidth(120);
        
        // Resources in horizontal layout
        HBox resourcesBox = new HBox(15);
        resourcesBox.setAlignment(Pos.CENTER);
        
        // Offered resources (compact)
        VBox offeredBox = new VBox(5);
        Label offeredLabel = new Label("📤 Anbieten:");
        offeredLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        GridPane offeredGrid = createCompactResourceGrid(offeredSpinners);
        offeredBox.getChildren().addAll(offeredLabel, offeredGrid);
        
        // Requested resources (compact)
        VBox requestedBox = new VBox(5);
        Label requestedLabel = new Label("📥 Wünschen:");
        requestedLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        GridPane requestedGrid = createCompactResourceGrid(requestedSpinners);
        requestedBox.getChildren().addAll(requestedLabel, requestedGrid);
        
        resourcesBox.getChildren().addAll(offeredBox, requestedBox);
        
        content.getChildren().addAll(targetBox, resourcesBox);
        return content;
    }
    
    private VBox createCompactBankTradeContent() {
        VBox content = new VBox(8);
        content.setPadding(new Insets(5));
        content.setAlignment(Pos.CENTER);
        
        // Instruction (compact)
        Label instructionLabel = new Label("4:1 Tausch mit der Bank");
        instructionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        
        // Trade options in horizontal layout
        HBox tradeBox = new HBox(10);
        tradeBox.setAlignment(Pos.CENTER);
        
        // Give section
        VBox giveSection = new VBox(3);
        giveSection.setAlignment(Pos.CENTER);
        Label giveLabel = new Label("📤 Abgeben:");
        giveLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
        
        HBox giveControls = new HBox(5);
        giveControls.setAlignment(Pos.CENTER);
        bankGiveSpinner.setPrefWidth(60);
        bankGiveCombo.setPrefWidth(80);
        giveControls.getChildren().addAll(bankGiveSpinner, bankGiveCombo);
        
        giveSection.getChildren().addAll(giveLabel, giveControls);
        
        // Arrow
        Label arrowLabel = new Label("→");
        arrowLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        
        // Want section
        VBox wantSection = new VBox(3);
        wantSection.setAlignment(Pos.CENTER);
        Label wantLabel = new Label("📥 Erhalten:");
        wantLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
        
        HBox wantControls = new HBox(5);
        wantControls.setAlignment(Pos.CENTER);
        Label oneLabel = new Label("1x");
        bankWantCombo.setPrefWidth(80);
        wantControls.getChildren().addAll(oneLabel, bankWantCombo);
        
        wantSection.getChildren().addAll(wantLabel, wantControls);
        
        tradeBox.getChildren().addAll(giveSection, arrowLabel, wantSection);
        
        // Execute button
        executeBankTradeButton.setPrefWidth(120);
        
        content.getChildren().addAll(instructionLabel, tradeBox, executeBankTradeButton);
        return content;
    }
    
    private GridPane createCompactResourceGrid(Map<ResourceType, Spinner<Integer>> spinners) {
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);
        
        // Create a more compact 5x1 layout for resources
        int col = 0;
        for (ResourceType resource : ResourceType.values()) {
            VBox resourceBox = new VBox(2);
            resourceBox.setAlignment(Pos.CENTER);
            
            Label iconLabel = new Label(getResourceIcon(resource));
            iconLabel.setFont(Font.font(14));
            
            Spinner<Integer> spinner = spinners.get(resource);
            spinner.setPrefWidth(50);
            spinner.setPrefHeight(25);
            
            resourceBox.getChildren().addAll(iconLabel, spinner);
            grid.add(resourceBox, col, 0);
            col++;
        }
        
        return grid;
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
    
    /**
     * Reinitialize the trading interface, clearing all offers and selections
     */
    public void reinitialize() {
        targetPlayerCombo.getSelectionModel().clearSelection();
        clearSpinners();
        tradeController.clearCurrentOffer();
        updateDisplay();
    }
}
