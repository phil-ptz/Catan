package de.philx.catan;

import de.philx.catan.GameField.GameField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {

        VBox root = new VBox();

        GameField gameField = new GameField(50.0);
        root.getChildren().addAll(gameField.toGroup());

        Scene scene = new Scene(root, 800, 600);
        Scene startScene = new Scene(new StartScreen(), 800, 600);

        stage.setTitle("Catan");
        stage.setScene(startScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}