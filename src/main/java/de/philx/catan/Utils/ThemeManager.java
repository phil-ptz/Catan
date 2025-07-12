package de.philx.catan.Utils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkMode = false;
    private List<Runnable> themeChangeListeners = new ArrayList<>();
    
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
        
        // Apply basic styling using setStyle on root
        if (isDarkMode) {
            scene.getRoot().setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: #ffffff;");
        } else {
            scene.getRoot().setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #000000;");
        }
    }
    
    public String getCurrentThemeName() {
        return isDarkMode ? "Dunkler Modus" : "Heller Modus";
    }
}
