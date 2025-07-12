package de.philx.catan;

import de.philx.catan.Screens.GameScreen;
import de.philx.catan.Screens.SettingsScreen;
import de.philx.catan.Screens.StartScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private Stage stage;
    private final double width;
    private final double height;

    public MainApplication() {
        // Get screen dimensions for better sizing
        Screen screen = Screen.getPrimary();
        width = screen.getVisualBounds().getWidth() * 0.9; // Use 90% of screen width
        height = screen.getVisualBounds().getHeight() * 0.9; // Use 90% of screen height
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Catan");
        
        // Set window to be maximized by default
        this.stage.setMaximized(true);
        
        // Optional: Enable fullscreen mode (uncomment if you want true fullscreen)
        // this.stage.setFullScreen(true);
        
        // Set minimum window size to ensure UI elements are always visible
        this.stage.setMinWidth(1200);
        this.stage.setMinHeight(800);

        startMenu();
        this.stage.show();
    }

    private void startMenu() {
        Scene scene = new Scene(new StartScreen(this::startGame, this::startSettings), (int) width, (int) height);
        this.stage.setScene(scene);
    }

    private void startGame() {
        Scene scene = new Scene(new GameScreen((int) width, (int) height, this::startMenu), (int) width, (int) height);
        this.stage.setScene(scene);
    }

    private void startSettings() {
        Scene scene = new Scene(new SettingsScreen(this::startMenu), (int) width, (int) height);
        this.stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}