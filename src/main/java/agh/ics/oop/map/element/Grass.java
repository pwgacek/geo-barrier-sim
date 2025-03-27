package agh.ics.oop.map.element;

import javafx.scene.paint.Color;

public record Grass(Vector2d position) implements IMapElement {

    @Override
    public Color getColor() {return Color.DARKGREEN;}

}
