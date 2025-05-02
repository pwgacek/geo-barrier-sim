package pl.edu.agh.miss.geobarriersim.map.element;


import com.badlogic.gdx.graphics.Color;

public class Plant implements IMapElement {

    private final Vector2d position;
    private boolean isGrown = false;
    private final float growthModifier;

    public Plant(Vector2d position, float growthModifier) {
        this.position = position;
        System.out.println(growthModifier);
        this.growthModifier = growthModifier;
    }

    public boolean isGrown() {
        return isGrown;
    }

    public void setGrown(boolean isGrown) {
        this.isGrown = isGrown;
    }

    public float getGrowthModifier() {
        return growthModifier;
    }

    @Override
    public Vector2d position() {
        return position;
    }

    @Override
    public Color getColor() {return isGrown ? Color.valueOf("#17611a") : Color.valueOf("#3b1f15");}

}
