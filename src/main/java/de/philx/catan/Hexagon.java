package de.philx.catan;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Hexagon extends Polygon {

    private final int[] pos;

    Hexagon(double centerX, double centerY, double radius, Color color, int[] pos) {

        this.pos = pos;
        
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            this.getPoints().addAll(x, y);
        }
        this.setFill(color);
    }

    public int[] getPos() {
        return pos;
    }
}
