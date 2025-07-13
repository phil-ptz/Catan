package de.philx.catan.Screens;

import de.philx.catan.Utils.StyledButton;
import de.philx.catan.Utils.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class SettingsScreen extends StackPane {

    private VBox mainContainer;
    private VBox settingsContainer;
    private Label titleLabel;
    private ToggleButton themeToggle;
    private Slider volumeSlider;
    private Slider soundEffectsSlider;

    public SettingsScreen(Runnable onClose) {
        setupLayout();
        setupTitle();
        setupSettings(onClose);
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
        mainContainer.setMaxWidth(600);
        mainContainer.setPadding(new Insets(60, 80, 60, 80));
        
        // Settings container
        settingsContainer = new VBox(25);
        settingsContainer.setAlignment(Pos.CENTER_LEFT);
        settingsContainer.setPrefWidth(450);
        
        this.getChildren().add(mainContainer);
    }
    
    private void setupTitle() {
        // Main title
        titleLabel = new Label("⚙️ Einstellungen");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setTextFill(Color.web(ThemeManager.ACCENT_COLOR));
        
        // Add drop shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(6.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.3));
        titleLabel.setEffect(dropShadow);
        
        mainContainer.getChildren().add(titleLabel);
    }
    
    private void setupSettings(Runnable onClose) {
        // Theme Settings Section
        VBox themeSection = createThemeSection();
        
        // Audio Settings Section
        VBox audioSection = createAudioSection();
        
        // Game Settings Section
        VBox gameSection = createGameSection();
        
        // Navigation Section
        VBox navigationSection = createNavigationSection(onClose);
        
        settingsContainer.getChildren().addAll(
            themeSection,
            createSeparator(),
            audioSection,
            createSeparator(),
            gameSection,
            createSeparator(),
            navigationSection
        );
        
        mainContainer.getChildren().add(settingsContainer);
    }
    
    private VBox createThemeSection() {
        ThemeManager themeManager = ThemeManager.getInstance();
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("🎨 Design & Erscheinungsbild");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        // Theme toggle
        HBox themeBox = new HBox(15);
        themeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label themeLabel = new Label("Dunkler Modus:");
        themeLabel.setFont(Font.font("Segoe UI", 14));
        themeLabel.setPrefWidth(150);
        
        themeToggle = new ToggleButton();
        themeToggle.setText(themeManager.isDarkMode() ? "AN" : "AUS");
        themeToggle.setSelected(themeManager.isDarkMode());
        themeToggle.setPrefWidth(80);
        themeToggle.setOnAction(e -> {
            themeManager.toggleTheme();
            themeToggle.setText(themeManager.isDarkMode() ? "AN" : "AUS");
            applyTheme();
        });
        
        // Style the toggle button
        styleToggleButton(themeToggle);
        
        themeBox.getChildren().addAll(themeLabel, themeToggle);
        section.getChildren().addAll(sectionTitle, themeBox);
        
        return section;
    }
    
    private VBox createAudioSection() {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("🔊 Audio-Einstellungen");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        // Master volume
        VBox volumeBox = createSliderSetting("Lautstärke:", 70);
        volumeSlider = (Slider) ((HBox) volumeBox.getChildren().get(0)).getChildren().get(1);
        
        // Sound effects volume
        VBox soundBox = createSliderSetting("Soundeffekte:", 85);
        soundEffectsSlider = (Slider) ((HBox) soundBox.getChildren().get(0)).getChildren().get(1);
        
        section.getChildren().addAll(sectionTitle, volumeBox, soundBox);
        return section;
    }
    
    private VBox createGameSection() {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("🎮 Spiel-Einstellungen");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        // Animation speed
        VBox animationBox = createSliderSetting("Animationsgeschwindigkeit:", 60);
        
        // Auto-save toggle
        HBox autoSaveBox = new HBox(15);
        autoSaveBox.setAlignment(Pos.CENTER_LEFT);
        
        Label autoSaveLabel = new Label("Automatisch speichern:");
        autoSaveLabel.setFont(Font.font("Segoe UI", 14));
        autoSaveLabel.setPrefWidth(180);
        
        ToggleButton autoSaveToggle = new ToggleButton("AN");
        autoSaveToggle.setSelected(true);
        autoSaveToggle.setPrefWidth(80);
        autoSaveToggle.setOnAction(e -> {
            autoSaveToggle.setText(autoSaveToggle.isSelected() ? "AN" : "AUS");
        });
        
        styleToggleButton(autoSaveToggle);
        
        autoSaveBox.getChildren().addAll(autoSaveLabel, autoSaveToggle);
        section.getChildren().addAll(sectionTitle, animationBox, autoSaveBox);
        
        return section;
    }
    
    private VBox createNavigationSection(Runnable onClose) {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("📋 Aktionen");
        sectionTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        StyledButton saveButton = new StyledButton("💾 Einstellungen speichern", StyledButton.ButtonType.SUCCESS);
        saveButton.setPrefWidth(200);
        saveButton.setOnAction(e -> {
            // Save settings (placeholder)
            System.out.println("Einstellungen gespeichert");
        });
        
        StyledButton resetButton = new StyledButton("🔄 Zurücksetzen", StyledButton.ButtonType.WARNING);
        resetButton.setPrefWidth(150);
        resetButton.setOnAction(e -> {
            // Reset settings (placeholder)
            resetToDefaults();
        });
        
        StyledButton backButton = new StyledButton("⬅️ Zurück zum Menü", StyledButton.ButtonType.PRIMARY);
        backButton.setPrefWidth(180);
        backButton.setOnAction(e -> onClose.run());
        
        buttonBox.getChildren().addAll(backButton, resetButton, saveButton);
        section.getChildren().addAll(sectionTitle, buttonBox);
        
        return section;
    }
    
    private VBox createSliderSetting(String labelText, double defaultValue) {
        VBox container = new VBox(8);
        
        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", 14));
        
        HBox sliderBox = new HBox(15);
        sliderBox.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(labelText.replace(":", ""));
        valueLabel.setFont(Font.font("Segoe UI", 12));
        valueLabel.setPrefWidth(140);
        
        Slider slider = new Slider(0, 100, defaultValue);
        slider.setPrefWidth(200);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(4);
        slider.setSnapToTicks(false);
        
        Label percentLabel = new Label(String.format("%.0f%%", defaultValue));
        percentLabel.setFont(Font.font("Segoe UI", 12));
        percentLabel.setPrefWidth(50);
        
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            percentLabel.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
        
        sliderBox.getChildren().addAll(valueLabel, slider, percentLabel);
        container.getChildren().addAll(sliderBox);
        
        return container;
    }
    
    private void styleToggleButton(ToggleButton button) {
        button.setStyle(ThemeManager.getInstance().getButtonStyle());
        
        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                button.setStyle(String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-border-color: %s;
                    -fx-border-width: 1px;
                    -fx-border-radius: 6px;
                    -fx-background-radius: 6px;
                    -fx-padding: 8px 16px;
                    -fx-font-size: 12px;
                    -fx-font-weight: 600;
                    """, ThemeManager.SUCCESS_COLOR, ThemeManager.SUCCESS_COLOR));
            } else {
                button.setStyle(ThemeManager.getInstance().getButtonStyle());
            }
        });
    }
    
    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxHeight(1);
        ThemeManager themeManager = ThemeManager.getInstance();
        separator.setStyle("-fx-background-color: " + themeManager.getBorderColor() + "; -fx-opacity: 0.3;");
        return separator;
    }
    
    private void resetToDefaults() {
        volumeSlider.setValue(70);
        soundEffectsSlider.setValue(85);
        System.out.println("Einstellungen auf Standardwerte zurückgesetzt");
    }
    
    private void setupAnimations() {
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), mainContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Slide animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), mainContainer);
        slideIn.setFromY(30);
        slideIn.setToY(0);
        
        fadeIn.play();
        slideIn.play();
    }
    
    private void applyTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply card styling to main container
        mainContainer.setStyle(themeManager.getCardStyle());
        
        // Apply background
        if (themeManager.isDarkMode()) {
            this.setStyle(String.format("""
                -fx-background-color: linear-gradient(to bottom, %s, #0d1117);
                """, themeManager.getBackgroundColor()));
        } else {
            this.setStyle(String.format("""
                -fx-background-color: linear-gradient(to bottom, %s, #e9ecef);
                """, themeManager.getBackgroundColor()));
        }
        
        // Update all labels with appropriate colors
        updateLabelsRecursively(settingsContainer);
    }
    
    private void updateLabelsRecursively(javafx.scene.Parent parent) {
        if (parent == null) return;
        
        ThemeManager themeManager = ThemeManager.getInstance();
        String textColor = themeManager.getTextColor();
        String secondaryTextColor = themeManager.getSecondaryTextColor();
        
        parent.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof Label) {
                Label label = (Label) node;
                String text = label.getText();
                
                if (text.contains("🎨") || text.contains("🔊") || text.contains("🎮") || text.contains("📋")) {
                    // Section headers
                    label.setStyle("-fx-text-fill: " + ThemeManager.ACCENT_COLOR + ";");
                } else if (text.endsWith(":") || text.endsWith("%")) {
                    // Field labels and values
                    label.setStyle("-fx-text-fill: " + textColor + ";");
                } else {
                    // Regular text
                    label.setStyle("-fx-text-fill: " + secondaryTextColor + ";");
                }
            } else if (node instanceof javafx.scene.Parent) {
                updateLabelsRecursively((javafx.scene.Parent) node);
            }
        });
    }
}
