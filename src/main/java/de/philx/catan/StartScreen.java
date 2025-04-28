package de.philx.catan;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StartScreen extends StackPane {

    public StartScreen() {
        Text title = new Text("Die Siedler von Casastan");
        Button startButton = new Button("Neues Spiel");
        Button settingsButton = new Button("Einstellungen");
        Button quitButton = new Button("Beenden");

        VBox vbox = new VBox(20);

        startButton.setOnAction(event -> {});
        settingsButton.setOnAction(event -> {});
        quitButton.setOnAction(event -> Platform.exit());

        vbox.getChildren().addAll(title, startButton, settingsButton, quitButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        this.getChildren().addAll(vbox);
    }
}
