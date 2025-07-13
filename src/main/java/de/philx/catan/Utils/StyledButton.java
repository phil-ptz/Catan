package de.philx.catan.Utils;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Enhanced Button component with modern styling and animations
 */
public class StyledButton extends Button {
    
    public enum ButtonType {
        PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER
    }
    
    private ButtonType buttonType;
    private ScaleTransition scaleAnimation;
    
    public StyledButton(String text) {
        this(text, ButtonType.SECONDARY);
    }
    
    public StyledButton(String text, ButtonType type) {
        super(text);
        this.buttonType = type;
        
        setupStyling();
        setupAnimations();
        setupEventHandlers();
    }
    
    private void setupStyling() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        // Apply initial styling based on button type
        applyButtonStyle();
        
        // Listen for theme changes
        themeManager.addThemeChangeListener(this::applyButtonStyle);
        
        // Make button focusable and responsive
        this.setFocusTraversable(true);
        this.setPrefHeight(44);
        this.setMinWidth(120);
    }
    
    private void applyButtonStyle() {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        String style = switch (buttonType) {
            case PRIMARY -> String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-border-color: %s;
                -fx-border-width: 1px;
                -fx-border-radius: 8px;
                -fx-background-radius: 8px;
                -fx-padding: 12px 24px;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(0,123,255,0.3), 4, 0, 0, 2);
                """, ThemeManager.ACCENT_COLOR, ThemeManager.ACCENT_COLOR);
                
            case SUCCESS -> String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-border-color: %s;
                -fx-border-width: 1px;
                -fx-border-radius: 8px;
                -fx-background-radius: 8px;
                -fx-padding: 12px 24px;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(40,167,69,0.3), 4, 0, 0, 2);
                """, ThemeManager.SUCCESS_COLOR, ThemeManager.SUCCESS_COLOR);
                
            case WARNING -> String.format("""
                -fx-background-color: %s;
                -fx-text-fill: #212529;
                -fx-border-color: %s;
                -fx-border-width: 1px;
                -fx-border-radius: 8px;
                -fx-background-radius: 8px;
                -fx-padding: 12px 24px;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(255,193,7,0.3), 4, 0, 0, 2);
                """, ThemeManager.WARNING_COLOR, ThemeManager.WARNING_COLOR);
                
            case DANGER -> String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-border-color: %s;
                -fx-border-width: 1px;
                -fx-border-radius: 8px;
                -fx-background-radius: 8px;
                -fx-padding: 12px 24px;
                -fx-font-size: 14px;
                -fx-font-weight: 600;
                -fx-cursor: hand;
                -fx-effect: dropshadow(gaussian, rgba(220,53,69,0.3), 4, 0, 0, 2);
                """, ThemeManager.DANGER_COLOR, ThemeManager.DANGER_COLOR);
                
            default -> themeManager.getButtonStyle();
        };
        
        this.setStyle(style);
    }
    
    private void setupAnimations() {
        // Scale animation for button press effect
        scaleAnimation = new ScaleTransition(Duration.millis(100), this);
        scaleAnimation.setFromX(1.0);
        scaleAnimation.setFromY(1.0);
        scaleAnimation.setToX(0.95);
        scaleAnimation.setToY(0.95);
        scaleAnimation.setCycleCount(2);
        scaleAnimation.setAutoReverse(true);
    }
    
    private void setupEventHandlers() {
        // Hover effects
        this.setOnMouseEntered(e -> {
            this.setStyle(this.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);");
            this.setScaleX(1.02);
            this.setScaleY(1.02);
        });
        
        this.setOnMouseExited(e -> {
            applyButtonStyle();
            this.setScaleX(1.0);
            this.setScaleY(1.0);
        });
        
        // Click animation
        this.setOnMousePressed(e -> scaleAnimation.play());
        
        // Keyboard accessibility
        this.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("SPACE") || e.getCode().toString().equals("ENTER")) {
                scaleAnimation.play();
            }
        });
    }
    
    public void setButtonType(ButtonType type) {
        this.buttonType = type;
        applyButtonStyle();
    }
    
    public ButtonType getButtonType() {
        return buttonType;
    }
}
