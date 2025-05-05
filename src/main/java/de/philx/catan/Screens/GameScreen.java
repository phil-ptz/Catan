package de.philx.catan.Screens;

import de.philx.catan.Components.PlayerInterface;
import de.philx.catan.GameField.GameField;
import javafx.scene.layout.VBox;

public class GameScreen extends VBox {

    private final GameField gameField = new GameField(50.0);
    private final PlayerInterface playerInterface = new PlayerInterface();

    public GameScreen(int width, int height) {
        this.setPrefSize(width, height);
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.getChildren().add(gameField.toGroup());
        this.getChildren().add(playerInterface);
        VBox.setVgrow(playerInterface, javafx.scene.layout.Priority.ALWAYS);
    }
}
