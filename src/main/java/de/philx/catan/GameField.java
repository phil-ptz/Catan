package de.philx.catan;

import javafx.scene.Group;
import javafx.scene.paint.Color;

import java.util.Arrays;

import static java.lang.Math.sqrt;

public class GameField {

    private final Hexagon[] hexagons;

    public GameField(double hexagonRadius) {

        hexagons = new Hexagon[19];

        int[] rowCounts = {3,4,5,4,3};

        int n = 0;
        for (int i = 0; i < rowCounts.length; i++) {
            for (int j = 0; j < rowCounts[i]; j++) {

                double apothem = (hexagonRadius * sqrt(3.0)) / 2.0;
                double offset = (i%2) * apothem;
                if (i==0 || i==rowCounts.length-1) {offset = apothem*2;};
                Hexagon hexagon = new Hexagon(apothem*j*2+offset, apothem*i*2+10, hexagonRadius, Color.RED, new int[]{i, j});
                hexagon.setStroke(Color.BLACK);
                hexagon.setStrokeWidth(10.0);
                hexagons[n++] = hexagon;

            }
        }
    }

    public Group toGroup() {
        Group group = new Group();
        group.getChildren().addAll(hexagons);
        return group;
    }
}
