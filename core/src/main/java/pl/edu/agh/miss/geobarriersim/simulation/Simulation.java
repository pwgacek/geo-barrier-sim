package pl.edu.agh.miss.geobarriersim.simulation;

import pl.edu.agh.miss.geobarriersim.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Pair;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Simulation {
    private int dayCounter = 0;
    private final WorldMap worldMap;
    private final SimulationSettings settings;

    public Simulation(SimulationSettings settings) {
        this.worldMap = new WorldMap(settings.getMapSize(), settings.getPlantGrowthChancePer10000());
        this.settings = settings;

        Random rand = new Random();

        for(int i=0;i<settings.getInitialAnimalCount();i++){
            Vector2d position = new Vector2d(rand.nextInt(worldMap.getWidth()), rand.nextInt(worldMap.getHeight()));
            worldMap.place(new Animal(worldMap, position, settings.getStartEnergy(), settings.getEnergyLossPerMove()));
        }
        int cellsCount = worldMap.getWidth() * worldMap.getHeight();
        int plantCount = (int) (cellsCount * (settings.getInitialPlantPercentage() / 100f));

        List<Integer> plantIndices = IntStream.range(0, cellsCount)
            .boxed()
            .collect(Collectors.toList());

        Collections.shuffle(plantIndices);

        plantIndices = plantIndices.subList(0, plantCount);
        plantIndices.forEach(i -> worldMap.addPlant(new Vector2d(i / worldMap.getHeight(), i % worldMap.getHeight())));
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void simulateOneDay() {
        removeDeadAnimals();// usuwanie martwych zwierząt
        moveAnimals();// ruch albo skręt zwierzęcia
        feedAnimals();//jedzenie roślin
        breedAnimals();// rozmnażanie zwierząt
        growPlants();// dodanie nowych roślin

        dayCounter++;


    }

    private void growPlants() {
        worldMap.growPlants();
    }

    private void breedAnimals() {
        worldMap.getAnimals().values().stream().parallel().forEach(animals -> {
            long possibleParentsCount = animals.stream()
                .takeWhile(it -> it.getEnergy() >= settings.getStartEnergy())
                .count();

            if(possibleParentsCount > 1){
                Pair<Animal> parents = getParents(animals);

                Animal child = Animal.breed(parents, dayCounter);
                worldMap.place(child);
            }
        });

    }



    private Pair<Animal> getParents(List<Animal> animals) {

        int maxEnergy = animals.getFirst().getEnergy();
        List<Animal> possibleParents = animals.stream()
            .filter(it -> it.getEnergy() == maxEnergy)
            .collect(Collectors.toList());

        if (possibleParents.size() >= 2){
            Collections.shuffle(possibleParents);
            return Pair.of(possibleParents.get(0), possibleParents.get(1));
        }
        else{
            Animal strongerParent = possibleParents.getFirst();
            int secondMaxEnergy = animals.get(1).getEnergy();
            possibleParents = animals.stream()
                .filter(it -> it.getEnergy() == secondMaxEnergy)
                .collect(Collectors.toList());
            Collections.shuffle(possibleParents);
            return Pair.of(strongerParent, possibleParents.getFirst());
        }


    }

    private void feedAnimals() {
        worldMap.getAnimals().entrySet().stream().parallel().forEach((entry) -> {
            Vector2d position = entry.getKey();
            List<Animal> animals = entry.getValue();

            if (worldMap.isPlantGrownAt(position)) {
                worldMap.removePlant(position);

                int maxEnergy = animals.getFirst().getEnergy();
                List<Animal> plantEaters = animals.stream()
                    .filter(it -> it.getEnergy() == maxEnergy)
                    .toList();

                int energyPerAnimal = settings.getEnergyFromPlant() / plantEaters.size();
                plantEaters.forEach(it -> it.gainEnergy(energyPerAnimal));
            }
        });

    }

    private void moveAnimals() {
        worldMap.getAnimals().values().stream()
            .flatMap(it -> new ArrayList<>(it).stream())
            .forEach(Animal::move);
    }

    private void removeDeadAnimals() {
        worldMap.getAnimals().values().stream()
            .parallel()
            .flatMap(it -> new ArrayList<>(it).stream())
            .filter(it -> it.getEnergy() < settings.getEnergyLossPerMove())
            .forEach(worldMap::removeAnimal);

    }

    public int getDayCounter() {
        return dayCounter;
    }

}
