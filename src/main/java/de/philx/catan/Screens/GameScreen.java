package de.philx.catan.Screens;

import de.philx.catan.GameField.GameField;
import javafx.scene.layout.Pane;

public class GameScreen extends Pane {

    private final GameField gameField = new GameField(50.0);

    public GameScreen() {
        this.getChildren().add(gameField.toGroup());
    }
}
