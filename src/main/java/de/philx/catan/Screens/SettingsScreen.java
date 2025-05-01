package de.philx.catan.Screens;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SettingsScreen extends StackPane{

    public SettingsScreen(Runnable onClose) {
        Text title = new Text("Einstellungen");
        Button button1 = new Button("Platzhalter 1");
        Button button2 = new Button("Platzhalter 2");
        Button quitButton = new Button("Zurück");

        VBox vbox = new VBox(20);

        button1.setOnAction(event -> {});
        button2.setOnAction(event -> {});
        quitButton.setOnAction(event -> {onClose.run();});

        vbox.getChildren().addAll(title, button1, button2, quitButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        this.getChildren().addAll(vbox);
    }

}
