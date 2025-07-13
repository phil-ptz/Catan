package de.philx.catan.Screens;

import de.philx.catan.Utils.StyledButton;
import de.philx.catan.Utils.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class StartScreen extends StackPane {

    private VBox mainContainer;
    private VBox buttonContainer;
    private Label titleLine1;
    private Label titleLine2;
    private Label subtitleLabel;

    public StartScreen(Runnable onStart, Runnable onSettings) {
        setupLayout();
        setupTitle();
        setupButtons(onStart, onSettings);
        setupAnimations();
        applyTheme();
        
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
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));
        
        // Main container with card-like styling
        mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setMaxWidth(500);
        mainContainer.setPadding(new Insets(60, 80, 60, 80));
        
        // Button container
        buttonContainer = new VBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPrefWidth(300);
        
        this.getChildren().add(mainContainer);
    }
    
    private void setupTitle() {
        // Split the title into two lines for better fit
        titleLine1 = new Label("Die Siedler");
        titleLine1.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        titleLine1.setAlignment(Pos.CENTER);
        
        titleLine2 = new Label("von Catan");
        titleLine2.setFont(Font.font("Segoe UI", FontWeight.BOLD, 48));
        titleLine2.setAlignment(Pos.CENTER);
        
        // Subtitle
        subtitleLabel = new Label("Willkommen zum digitalen Brettspiel-Erlebnis");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitleLabel.setAlignment(Pos.CENTER);
        
        // Add gradient text effect to both title lines
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, null,
            new Stop(0, Color.web("#007bff")),
            new Stop(1, Color.web("#0056b3"))
        );
        titleLine1.setTextFill(gradient);
        titleLine2.setTextFill(gradient);
        
        // Add drop shadow effect to both title lines
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(8.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.3));
        titleLine1.setEffect(dropShadow);
        titleLine2.setEffect(dropShadow);
        
        VBox titleContainer = new VBox(5); // Reduced spacing between title lines
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getChildren().addAll(titleLine1, titleLine2, subtitleLabel);
        
        mainContainer.getChildren().add(titleContainer);
    }
    
    private void setupButtons(Runnable onStart, Runnable onSettings) {
        // Create styled buttons
        StyledButton startButton = new StyledButton("Neues Spiel starten", StyledButton.ButtonType.PRIMARY);
        StyledButton settingsButton = new StyledButton("Einstellungen", StyledButton.ButtonType.SECONDARY);
        StyledButton quitButton = new StyledButton("Spiel beenden", StyledButton.ButtonType.DANGER);
        
        // Make buttons uniform width
        startButton.setPrefWidth(280);
        settingsButton.setPrefWidth(280);
        quitButton.setPrefWidth(280);
        
        // Set button actions
        startButton.setOnAction(event -> onStart.run());
        settingsButton.setOnAction(event -> onSettings.run());
        quitButton.setOnAction(event -> {
            // Add confirmation dialog or smooth exit
            Platform.exit();
        });
        
        buttonContainer.getChildren().addAll(startButton, settingsButton, quitButton);
        mainContainer.getChildren().add(buttonContainer);
    }
    
    private void setupAnimations() {
        // Fade in animation for the entire container
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Slide up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(600), mainContainer);
        slideUp.setFromY(50);
        slideUp.setToY(0);
        
        // Play animations when screen loads
        Platform.runLater(() -> {
            fadeIn.play();
            slideUp.play();
        });
        
        // Add staggered animations for buttons
        for (int i = 0; i < buttonContainer.getChildren().size(); i++) {
            var button = buttonContainer.getChildren().get(i);
            FadeTransition buttonFade = new FadeTransition(Duration.millis(400), button);
            buttonFade.setFromValue(0.0);
            buttonFade.setToValue(1.0);
            buttonFade.setDelay(Duration.millis(300 + (i * 150)));
            
            TranslateTransition buttonSlide = new TranslateTransition(Duration.millis(400), button);
            buttonSlide.setFromY(30);
            buttonSlide.setToY(0);
            buttonSlide.setDelay(Duration.millis(300 + (i * 150)));
            
            Platform.runLater(() -> {
                buttonFade.play();
                buttonSlide.play();
            });
        }
    }
    
    private void applyTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply card styling to main container
        mainContainer.setStyle(themeManager.getCardStyle());
        
        // Update subtitle color based on theme
        subtitleLabel.setTextFill(Color.web(themeManager.getSecondaryTextColor()));
        
        // Apply background gradient
        String backgroundColor = themeManager.getBackgroundColor();
        if (themeManager.isDarkMode()) {
            this.setStyle(String.format("""
                -fx-background-color: linear-gradient(to bottom, %s, #0d1117);
                """, backgroundColor));
        } else {
            this.setStyle(String.format("""
                -fx-background-color: linear-gradient(to bottom, %s, #e9ecef);
                """, backgroundColor));
        }
    }
}
