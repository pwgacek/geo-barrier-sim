package pl.edu.agh.miss.geobarriersim.map;


import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Grass;
import pl.edu.agh.miss.geobarriersim.map.element.IMapElement;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class WorldMap implements IPositionChangeObserver {
    private final int width;
    private final int height;
    private final int grassGrowChancePerThousand;
    private final Map<Vector2d, ArrayList<Animal>> animals;


    private final Map<Vector2d, Grass> grass;
    private final Map<Vector2d, Grass> noGrass;


    public WorldMap(int size, int grassGrowChancePerThousand) {
        this.animals = new HashMap<>();

        this.width = size * 4 / 3;
        this.height = size;
        this.grassGrowChancePerThousand = grassGrowChancePerThousand;

        this.grass = new HashMap<>();
        this.noGrass = new HashMap<>();

        for(int y = 0; y<this.height; y++){
            for(int x = 0; x<this.width; x++){
                Vector2d vector = new Vector2d(x,y);
                Grass grass = new Grass(vector);
                noGrass.put(vector,grass);
                animals.put(vector,new ArrayList<>());
            }
        }
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() { return width;}

    public void growGrass() {
        Iterator<Map.Entry<Vector2d, Grass>> noGrassIterator = noGrass.entrySet().iterator();
        while (noGrassIterator.hasNext()) {
            Map.Entry<Vector2d, Grass> noGrassEntry = noGrassIterator.next();
            if(animals.get(noGrassEntry.getKey()).isEmpty()){
                if (new Random().nextInt(1000) < grassGrowChancePerThousand){
                    noGrassIterator.remove();
                    grass.put(noGrassEntry.getKey(),noGrassEntry.getValue());
                }
            }
        }


    }

    public void removeGrassFromSavanna(Vector2d position){
        Grass grass = this.grass.get(position);
        noGrass.put(position,grass);
        this.grass.remove(position);
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
        if(grass.containsKey(position)) return Optional.of(grass.get(position));
        return Optional.empty();
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

    public Map<Vector2d, ArrayList<Animal>> getAnimals() {
        return animals.entrySet().stream().filter(it -> !it.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<Vector2d, Grass> getGrass() {
        return grass;
    }


}
