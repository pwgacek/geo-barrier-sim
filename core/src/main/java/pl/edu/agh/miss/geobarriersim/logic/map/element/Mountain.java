package pl.edu.agh.miss.geobarriersim.logic.map.element;

import com.badlogic.gdx.graphics.Color;

public record Mountain(Vector2d position) implements IMapElement {

    @Override
    public Color getColor() {
        return Color.GRAY;
    }
}
