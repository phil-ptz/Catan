package de.philx.catan;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.util.Arrays;

import static java.lang.Math.sqrt;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {

        VBox root = new VBox();

        GameField gameField = new GameField(50.0);
        root.getChildren().addAll(gameField.toGroup());

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Catan");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}