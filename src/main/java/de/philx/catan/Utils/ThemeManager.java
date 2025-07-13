package de.philx.catan.Utils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkMode = false;
    private List<Runnable> themeChangeListeners = new ArrayList<>();
    
    // Color constants for consistent theming
    public static final String LIGHT_BACKGROUND = "#f8f9fa";
    public static final String LIGHT_CARD_BACKGROUND = "#ffffff";
    public static final String LIGHT_BORDER = "#dee2e6";
    public static final String LIGHT_TEXT = "#212529";
    public static final String LIGHT_SECONDARY_TEXT = "#6c757d";
    
    public static final String DARK_BACKGROUND = "#1a1a1a";
    public static final String DARK_CARD_BACKGROUND = "#2d2d2d";
    public static final String DARK_BORDER = "#404040";
    public static final String DARK_TEXT = "#ffffff";
    public static final String DARK_SECONDARY_TEXT = "#b0b0b0";
    
    public static final String ACCENT_COLOR = "#007bff";
    public static final String SUCCESS_COLOR = "#28a745";
    public static final String WARNING_COLOR = "#ffc107";
    public static final String DANGER_COLOR = "#dc3545";
    
    private ThemeManager() {}
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
    
    public void toggleTheme() {
        isDarkMode = !isDarkMode;
        notifyThemeChangeListeners();
    }
    
    public void setDarkMode(boolean darkMode) {
        if (this.isDarkMode != darkMode) {
            this.isDarkMode = darkMode;
            notifyThemeChangeListeners();
        }
    }
    
    public void addThemeChangeListener(Runnable listener) {
        themeChangeListeners.add(listener);
    }
    
    public void removeThemeChangeListener(Runnable listener) {
        themeChangeListeners.remove(listener);
    }
    
    private void notifyThemeChangeListeners() {
        for (Runnable listener : themeChangeListeners) {
            listener.run();
        }
    }
    
    public void applyTheme(Scene scene) {
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Apply comprehensive styling
        String css = generateCSS();
        scene.getRoot().setStyle(css);
    }
    
    private String generateCSS() {
        if (isDarkMode) {
            return String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                """, DARK_BACKGROUND, DARK_TEXT);
        } else {
            return String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                """, LIGHT_BACKGROUND, LIGHT_TEXT);
        }
    }
    
    public String getCurrentThemeName() {
        return isDarkMode ? "Dunkler Modus" : "Heller Modus";
    }
    
    // Helper methods for getting theme-appropriate colors
    public String getBackgroundColor() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }
    
    public String getCardBackgroundColor() {
        return isDarkMode ? DARK_CARD_BACKGROUND : LIGHT_CARD_BACKGROUND;
    }
    
    public String getBorderColor() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }
    
    public String getTextColor() {
        return isDarkMode ? DARK_TEXT : LIGHT_TEXT;
    }
    
    public String getSecondaryTextColor() {
        return isDarkMode ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT;
    }
    
    public String getButtonStyle() {
        String backgroundColor = isDarkMode ? DARK_CARD_BACKGROUND : LIGHT_CARD_BACKGROUND;
        String textColor = getTextColor();
        String borderColor = getBorderColor();
        
        return String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-border-color: %s;
            -fx-border-width: 1px;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-padding: 12px 24px;
            -fx-font-size: 14px;
            -fx-font-weight: 500;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);
            """, backgroundColor, textColor, borderColor);
    }
    
    public String getButtonHoverStyle() {
        String textColor = getTextColor();
        String borderColor = getBorderColor();
        String hoverColor = isDarkMode ? "#404040" : "#e9ecef";
        
        return String.format("""
            -fx-background-color: %s;
            -fx-text-fill: %s;
            -fx-border-color: %s;
            -fx-border-width: 1px;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-padding: 12px 24px;
            -fx-font-size: 14px;
            -fx-font-weight: 500;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 3);
            """, hoverColor, textColor, borderColor);
    }
    
    public String getPrimaryButtonStyle() {
        return String.format("""
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
            """, ACCENT_COLOR, ACCENT_COLOR);
    }
    
    public String getCardStyle() {
        return String.format("""
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-border-width: 1px;
            -fx-border-radius: 12px;
            -fx-background-radius: 12px;
            -fx-padding: 16px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);
            """, getCardBackgroundColor(), getBorderColor());
    }
}
