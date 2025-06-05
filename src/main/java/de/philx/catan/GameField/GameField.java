package de.philx.catan.GameField;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import static java.lang.Math.sqrt;

public class GameField{

    private final Hexagon[] hexagons;

    public GameField(double hexagonRadius) {

        hexagons = new Hexagon[19];

        double r = hexagonRadius; // Radius
        double dx = 1.9 * r; // horizontaler Abstand zwischen Mittelpunkten
        double dy = sqrt(3) * r; // vertikaler Abstand zwischen Reihen

        int[] rowCounts = {3, 4, 5, 4, 3};
        int n = 0;
        for (int i = 0; i < rowCounts.length; i++) {
            int count = rowCounts[i];
            double y = i * dy + 10;
            double offsetX = (5 - count) * dx / 2; // zentrieren

            for (int j = 0; j < count; j++) {
                double x = j * dx + offsetX;
                Hexagon hex = new Hexagon(x, y, r, Color.RED, new int[]{i, j});
                hex.setStroke(Color.BLACK);
                hex.setStrokeWidth(2.0);
                hexagons[n++] = hex;
            }
        }

    }

    public Group toGroup() {
        Group group = new Group();
        group.getChildren().addAll(hexagons);
        return group;
    }
}
