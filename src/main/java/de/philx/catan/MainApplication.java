package de.philx.catan;

import de.philx.catan.Screens.GameScreen;
import de.philx.catan.Screens.SettingsScreen;
import de.philx.catan.Screens.StartScreen;
import de.philx.catan.Utils.ThemeManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Catan");
        
        // Set window to fill entire screen (windowed fullscreen)
        Screen screen = Screen.getPrimary();
        this.stage.setX(screen.getVisualBounds().getMinX());
        this.stage.setY(screen.getVisualBounds().getMinY());
        this.stage.setWidth(screen.getVisualBounds().getWidth());
        this.stage.setHeight(screen.getVisualBounds().getHeight());
        
        // Prevent resizing to maintain windowed fullscreen
        this.stage.setResizable(false);

        startMenu();
        this.stage.show();
    }

    private void startMenu() {
        Screen screen = Screen.getPrimary();
        Scene scene = new Scene(new StartScreen(this::startGame, this::startSettings), 
                               screen.getVisualBounds().getWidth(), 
                               screen.getVisualBounds().getHeight());
        ThemeManager.getInstance().applyTheme(scene);
        this.stage.setScene(scene);
    }

    private void startGame() {
        try {
            Screen screen = Screen.getPrimary();
            Scene scene = new Scene(new GameScreen((int) screen.getVisualBounds().getWidth(), 
                                                  (int) screen.getVisualBounds().getHeight(), 
                                                  this::startMenu), 
                                   screen.getVisualBounds().getWidth(), 
                                   screen.getVisualBounds().getHeight());
            ThemeManager.getInstance().applyTheme(scene);
            this.stage.setScene(scene);
            System.out.println("Game screen loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading game screen: " + e.getMessage());
            e.printStackTrace();
            // Fallback to start menu if game fails to load
            startMenu();
        }
    }

    private void startSettings() {
        try {
            Screen screen = Screen.getPrimary();
            Scene scene = new Scene(new SettingsScreen(this::startMenu), 
                                   screen.getVisualBounds().getWidth(), 
                                   screen.getVisualBounds().getHeight());
            ThemeManager.getInstance().applyTheme(scene);
            this.stage.setScene(scene);
            System.out.println("Settings screen loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading settings screen: " + e.getMessage());
            e.printStackTrace();
            // Fallback to start menu if settings fail to load
            startMenu();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}