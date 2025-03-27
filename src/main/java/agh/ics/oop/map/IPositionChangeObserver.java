package agh.ics.oop.map;

import agh.ics.oop.map.element.Animal;
import agh.ics.oop.map.element.Vector2d;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Animal animal);

}
