package pl.edu.agh.miss.geobarriersim.map;


import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Plant;
import pl.edu.agh.miss.geobarriersim.map.element.IMapElement;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

import java.util.ArrayList;
import java.util.Collection;
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
    private final int plantGrowChancePer10000;
    private final Map<Vector2d, ArrayList<Animal>> animals;


    private final Map<Vector2d, Plant> plants;

    private final Random random = new Random();


    public WorldMap(int size, int plantGrowChancePer100000) {
        this.animals = new HashMap<>();

        this.width = size * 4 / 3;
        this.height = size;
        this.plantGrowChancePer10000 = plantGrowChancePer100000;

        this.plants = new HashMap<>();

        for(int y = 0; y < this.height; y++){
            for(int x = 0; x < this.width; x++){
                Vector2d vector = new Vector2d(x,y);
                Plant plant = new Plant(vector);
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
            .filter(it -> !it.isGrown() && animals.get(it.position()).isEmpty()  && random.nextInt(10000) < plantGrowChancePer10000)
            .forEach(it -> {it.setGrown(true);});
    }


    public void addPlant(Vector2d position) {
        plants.get(position).setGrown(true);
    }

    public void removePlant(Vector2d position){
        plants.get(position).setGrown(false);

    }

    public void place(Animal animal) {
        animals.get(animal.position()).add(animal);

        if(animals.get(animal.position()).size() > 1){
            Collections.sort(animals.get(animal.position()));
        }

        animal.addObserver(this);
    }

    public Optional<IMapElement> objectAt(Vector2d position) {
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

    public void removeAnimal(Animal animal){
        animals.get(animal.position()).remove(animal);
    }


    public List<Animal> getAnimalsList() {
        return animals.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Map<Vector2d, List<Animal>> getAnimals() {
        return animals.entrySet().stream().filter(it -> !it.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Vector2d, Plant> getPlants() {
        return plants;
    }

    public boolean isPlantGrownAt(Vector2d position) {
        return plants.get(position).isGrown();
    }


}
