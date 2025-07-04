package pl.edu.agh.miss.geobarriersim.logic.map;


import pl.edu.agh.miss.geobarriersim.logic.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Vector2d;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Animal animal);

}
