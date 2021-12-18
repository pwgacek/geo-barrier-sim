package agh.ics.oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap {



    public GrassField(int grassQuantity,int width,int height) {
        this.grassQuantity = grassQuantity;
        animals = new ArrayList<>();
        grassList = new ArrayList<>();
        this.animalsMap = new HashMap<>();
        this.grassMap = new HashMap<>();
        this.height = height;
        this.width = width;

        addGrass(grassQuantity);

    }





    @Override
    public boolean canMoveTo(Vector2d position) {
        if (position.x <= width && position.x >=0 && position.y <= height && position.y >=0)
        {
            return !isOccupied(position);
        }
        return false;
    }

    @Override
    public Object objectAt(Vector2d position) {
        Object animal = super.objectAt(position);
        if(animal != null) return animal;
        if(grassMap.containsKey(position)) return grassMap.get(position);
        return null;
    }
}
