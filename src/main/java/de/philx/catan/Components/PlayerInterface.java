package de.philx.catan.Components;

import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class PlayerInterface extends GridPane {

    public PlayerInterface() {

        // Zeilen und Spalten dynamisch festlegen
        int columns = 2;
        for (int i = 0; i < columns; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.ALWAYS);
            this.getColumnConstraints().add(col);
        }
        int rows = 3;
        for (int i = 0; i < rows; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            this.getRowConstraints().add(row);
        }

        // Rohstoffe würfeln button
        Button diceButton = new Button("Rohstoffe Würfeln");
        diceButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(diceButton, Priority.ALWAYS);
        GridPane.setVgrow(diceButton, Priority.ALWAYS);
        this.add(diceButton, 0, 0);

        // Handeln button
        Button tradeButton = new Button("Handelsangebot");
        tradeButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(tradeButton, Priority.ALWAYS);
        GridPane.setVgrow(tradeButton, Priority.ALWAYS);
        this.add(tradeButton, 1, 0);

        // Bauen button
        Button buildButton = new Button("Bauen");
        buildButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(buildButton, Priority.ALWAYS);
        GridPane.setVgrow(buildButton, Priority.ALWAYS);
        this.add(buildButton, 0, 1);

        // Sonderkarten button
        Button specialButton = new Button("Sonderkarten");
        specialButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(specialButton, Priority.ALWAYS);
        GridPane.setVgrow(specialButton, Priority.ALWAYS);
        this.add(specialButton, 1, 1);

        // Verlassen button
        Button quitButton = new Button("Zum Hauptmenü");
        quitButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(quitButton, Priority.ALWAYS);
        GridPane.setVgrow(quitButton, Priority.ALWAYS);
        this.add(quitButton, 1, 2);

        // zeige mitspieler button
        Button showInfoButton = new Button("Zeige Mitspieler");
        showInfoButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(showInfoButton, Priority.ALWAYS);
        GridPane.setVgrow(showInfoButton, Priority.ALWAYS);
        this.add(showInfoButton, 1, 2);
    }
}
