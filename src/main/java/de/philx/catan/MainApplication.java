package de.philx.catan;

import de.philx.catan.Screens.GameScreen;
import de.philx.catan.Screens.SettingsScreen;
import de.philx.catan.Screens.StartScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private Stage stage;
    private final int width = 800;
    private final int height = 600;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Catan");

        startMenu();
        this.stage.show();
    }

    private void startMenu() {
        Scene scene = new Scene(new StartScreen(this::startGame, this::startSettings), width, height);
        this.stage.setScene(scene);
    }

    private void startGame() {
        Scene scene = new Scene(new GameScreen(width, height), width, height);
        this.stage.setScene(scene);
    }

    private void startSettings() {
        Scene scene = new Scene(new SettingsScreen(this::startMenu), width, height);
        this.stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}