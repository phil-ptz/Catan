package de.philx.catan.Screens;

import de.philx.catan.Utils.ThemeManager;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SettingsScreen extends StackPane{

    public SettingsScreen(Runnable onClose) {
        ThemeManager themeManager = ThemeManager.getInstance();
        
        Text title = new Text("Einstellungen");
        Button themeButton = new Button("Theme: " + themeManager.getCurrentThemeName());
        Button button2 = new Button("Platzhalter 2");
        Button quitButton = new Button("Zurück");

        VBox vbox = new VBox(20);

        themeButton.setOnAction(event -> {
            themeManager.toggleTheme();
            themeButton.setText("Theme: " + themeManager.getCurrentThemeName());
            // Apply theme to current scene
            themeManager.applyTheme(this.getScene());
        });
        button2.setOnAction(event -> {});
        quitButton.setOnAction(event -> {onClose.run();});

        vbox.getChildren().addAll(title, themeButton, button2, quitButton);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        this.getChildren().addAll(vbox);
        
        // Apply current theme when screen is created
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                themeManager.applyTheme(newScene);
            }
        });
    }

}
