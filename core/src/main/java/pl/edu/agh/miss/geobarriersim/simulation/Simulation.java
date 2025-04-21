package pl.edu.agh.miss.geobarriersim.simulation;

import pl.edu.agh.miss.geobarriersim.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Pair;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
            worldMap.place(new Animal(worldMap.getWidth(), worldMap.getHeight(), position, settings.getStartEnergy(), settings.getEnergyLossPerMove()));
        }
        int cellsCount = worldMap.getWidth() * worldMap.getHeight();
        int grassCount = (int) (cellsCount * (settings.getInitialGrassPercentage() / 100f));

        List<Vector2d> grassPositions = IntStream.range(0, cellsCount)
            .mapToObj(i -> new Vector2d(i / worldMap.getHeight(), i % worldMap.getHeight()))
            .collect(Collectors.toList());

        for (int i = 0; i < grassCount; i++) {
            worldMap.addGrass(grassPositions.remove(rand.nextInt(grassPositions.size())));
        }
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void simulateOneDay() {

        dayCounter++;

        removeDeadAnimals();// usuwanie martwych zwierząt
        moveAnimals();// ruch albo skręt zwierzęcia
        feedAnimals();//jedzenie roślin
        breedAnimals();// rozmnażanie zwierząt
        addNewGrass();// dodanie nowych roślin


    }

    private void addNewGrass() {
        worldMap.growGrass();
    }

    private void breedAnimals() {
        for(List<Animal> animals : worldMap.getAnimals().values()){
            long possibleParentsCount = animals.stream()
                .takeWhile(it -> it.getEnergy() >= settings.getStartEnergy())
                .count();

            if(possibleParentsCount > 1){
                Pair<Animal> parents = getParents(animals);

                Animal child = Animal.breed(parents, dayCounter);
                worldMap.place(child);
            }
        }
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
        for(Map.Entry<Vector2d, List<Animal>> entry : worldMap.getAnimals().entrySet()){
            Vector2d position = entry.getKey();
            List<Animal> animals = entry.getValue();

            if(worldMap.getGrass().containsKey(position)){
                worldMap.removeGrass(position);

                int maxEnergy = animals.getFirst().getEnergy();
                List<Animal> grassEaters = animals.stream()
                    .filter(it -> it.getEnergy() == maxEnergy).
                    toList();

                grassEaters.forEach(it -> it.gainEnergy(settings.getEnergyFromPlant() / grassEaters.size()));
            }
        }
    }

    private void moveAnimals() {
        for(Animal animal : worldMap.getAnimalsList()){
            animal.move();
        }
    }

    private void removeDeadAnimals() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        for(Animal animal : worldMap.getAnimalsList()){
            if(animal.getEnergy()-settings.getEnergyLossPerMove() < 0){
                animalsToRemove.add(animal);
            }
        }
        for(Animal deadAnimal : animalsToRemove){
            deadAnimal.removeObserver(this.worldMap);
            worldMap.removeAnimal(deadAnimal);
        }
    }


}
