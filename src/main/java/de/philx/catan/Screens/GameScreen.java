package de.philx.catan.Screens;

import de.philx.catan.Components.GameLegend;
import de.philx.catan.Components.PlayerInterface;
import de.philx.catan.GameField.GameField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

public class GameScreen extends HBox {

    private final GameField gameField = new GameField(50.0);
    private final PlayerInterface playerInterface = new PlayerInterface();
    private final GameLegend gameLegend = new GameLegend();

    public GameScreen(int width, int height) {
        this.setPrefSize(width, height);
        this.setSpacing(10);
        
        // Left side: Game board and player interface
        VBox leftPanel = new VBox(10);
        leftPanel.setAlignment(javafx.geometry.Pos.CENTER);
        leftPanel.getChildren().add(gameField.toGroup());
        leftPanel.getChildren().add(playerInterface);
        VBox.setVgrow(playerInterface, Priority.ALWAYS);
        
        // Right side: Legend
        VBox rightPanel = new VBox();
        rightPanel.getChildren().add(gameLegend);
        rightPanel.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        
        // Add both panels to the main HBox
        this.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
    }
}
