package pl.edu.agh.miss.geobarriersim.logic.map;


import pl.edu.agh.miss.geobarriersim.logic.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Mountain;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Plant;
import pl.edu.agh.miss.geobarriersim.logic.map.element.IMapElement;
import pl.edu.agh.miss.geobarriersim.logic.map.element.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class WorldMap implements IPositionChangeObserver {
    private final int width;
    private final int height;
    private final double plantGrowChance;
    private final Map<Vector2d, ArrayList<Animal>> animals;


    private final Map<Vector2d, Plant> plants;
    private final Map<Vector2d, Mountain> mountains;
    private final Random random = new Random();


    public WorldMap(int size, int plantGrowChancePer100000) {
        this.animals = new HashMap<>();

        this.width = size * 4 / 3;
        this.height = size;
        this.plantGrowChance = plantGrowChancePer100000 / 10000f;

        this.plants = new HashMap<>();
        this.mountains = new HashMap<>();

        for(int y = 0; y < this.height; y++){
            for(int x = 0; x < this.width; x++){
                Vector2d vector = new Vector2d(x,y);
                Plant plant = new Plant(vector, 2 - (2f * y / height));
                plants.put(vector,plant);
                animals.put(vector,new ArrayList<>());
            }
        }
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() { return width;}

    public void growPlants() {
        plants.values().stream()
            .filter(it -> !it.isGrown() && animals.get(it.position()).isEmpty()  && random.nextDouble() < (plantGrowChance * it.getGrowthModifier()))
            .forEach(it -> it.setGrown(true));
    }


    public void growPlantsWithProbability(float probability) {
        plants.values().stream()
            .filter(it -> random.nextDouble() < probability * it.getGrowthModifier())
            .forEach(it -> it.setGrown(true));
    }

    public void removePlant(Vector2d position){
        plants.get(position).setGrown(false);

    }

    public void addMountain(Vector2d position) {
        if (mountains.containsKey(position)) return;
        removeAllAnimals(position);
        mountains.put(position, new Mountain(position));
    }

    public void removeMountain(Vector2d position) {
        if (!mountains.containsKey(position)) return;
        plants.get(position).setGrown(false);
        mountains.remove(position);
    }

    public boolean canMoveTo(Vector2d oldPosition, Vector2d newPosition) {
        return newPosition.x() < width && newPosition.x() >= 0
                && newPosition.y() < height && newPosition.y() >= 0
                && !mountainHopping(oldPosition, newPosition);
    }

    public boolean mountainHopping(Vector2d oldPosition, Vector2d newPosition) {
        return mountains.containsKey(newPosition) ||
            (isDiagonalMove(oldPosition, newPosition) && crossesMountains(oldPosition, newPosition));

    }

    public boolean crossesMountains(Vector2d oldPosition, Vector2d newPosition) {
        Vector2d unitVector = newPosition.subtract(oldPosition);
        Vector2d newPositionByX = oldPosition.addX(unitVector.x());
        Vector2d newPositionByY = oldPosition.addY(unitVector.y());
        return mountains.containsKey(newPositionByX) && mountains.containsKey(newPositionByY);
    }

    public boolean isDiagonalMove(Vector2d oldPosition, Vector2d newPosition) {
        return oldPosition.x() != newPosition.x() && oldPosition.y() != newPosition.y();
    }

    public void place(Animal animal) {
        animals.get(animal.position()).add(animal);

        if(animals.get(animal.position()).size() > 1){
            Collections.sort(animals.get(animal.position()));
        }

        animal.addObserver(this);
    }

    public Optional<IMapElement> objectAt(Vector2d position) {
        if(mountains.containsKey(position)) return Optional.of(mountains.get(position));
        if(!animals.get(position).isEmpty()) return Optional.of(animals.get(position).getFirst()); //todo tutaj był index out of bound 0 of 0
        Plant plant = plants.get(position);
        return plant.isGrown() ? Optional.of(plant) : Optional.empty();
    }


    @Override
    public void positionChanged(Vector2d oldPosition, Animal animal){
        animals.get(oldPosition).remove(animal);

        animals.get(animal.position()).add(animal);
        if(animals.size() > 1){
            Collections.sort(animals.get(animal.position()));
        }

    }
    public void removeAllAnimals(Vector2d position) {
        animals.get(position).forEach(it -> it.removeObserver(this));
        animals.get(position).clear();
    }
    public void removeAnimal(Animal animal){
        animal.removeObserver(this);
        animals.get(animal.position()).remove(animal);
    }

    public Map<Vector2d, List<Animal>> getAnimals() {
        return animals.entrySet().stream().filter(it -> !it.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isPlantGrownAt(Vector2d position) {
        return plants.get(position).isGrown();
    }


}
