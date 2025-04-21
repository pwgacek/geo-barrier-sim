package pl.edu.agh.miss.geobarriersim.map.element;


import com.badlogic.gdx.graphics.Color;

public class Plant implements IMapElement {

    private final Vector2d position;
    private boolean isGrown = false;

    public Plant(Vector2d position) {
        this.position = position;
    }

    public boolean isGrown() {
        return isGrown;
    }

    public void setGrown(boolean isGrown) {
        this.isGrown = isGrown;
    }

    @Override
    public Vector2d position() {
        return position;
    }

    @Override
    public Color getColor() {return isGrown ? Color.valueOf("#17611a") : Color.valueOf("#3b1f15");}

}
