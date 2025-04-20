package pl.edu.agh.miss.geobarriersim.map;


import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Animal animal);

}
