package pl.edu.agh.miss.geobarriersim.simulation;

import pl.edu.agh.miss.geobarriersim.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.map.element.Animal;
import pl.edu.agh.miss.geobarriersim.map.element.Vector2d;

import java.util.ArrayList;
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
            worldMap.place(new Animal(worldMap.getWidth(), worldMap.getHeight(), position,settings.getStartEnergy()));
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
        Map<Vector2d, ArrayList<Animal>> animals = worldMap.getAnimals();
        for(Vector2d position : animals.keySet()){
            if(animals.get(position).size() > 1){
                Animal strongerParent,weakerParent;
                int maxEnergy = animals.get(position).get(0).getEnergy();
                ArrayList<Animal> candidates = (ArrayList<Animal>) animals.get(position).stream().filter(a -> a.getEnergy() == maxEnergy).collect(Collectors.toList());
                if (candidates.size() >= 2){
                    ArrayList<Animal> parents = drawParents(candidates,2);
                    strongerParent = parents.get(0);
                    weakerParent = parents.get(1);

                }
                else{
                    int secondMaxEnergy = animals.get(position).get(1).getEnergy();
                    strongerParent = candidates.getFirst();
                    candidates = (ArrayList<Animal>) animals.get(position).stream().filter(a -> a.getEnergy() == secondMaxEnergy).collect(Collectors.toList());
                    weakerParent = drawParents(candidates,1).getFirst();

                }

                if(weakerParent.getEnergy() >= (settings.getStartEnergy()/2) && strongerParent.getEnergy()>0){
                    weakerParent.incrementChildrenCounter();
                    strongerParent.incrementChildrenCounter();
                    Animal child = new Animal(worldMap.getWidth(), worldMap.getHeight(), position, strongerParent, weakerParent, dayCounter, settings.getStartEnergy());
                    worldMap.place(child);

                }

            }
        }
    }

    private void feedAnimals() {
        Map<Vector2d, ArrayList<Animal>> animals = worldMap.getAnimals();
        for(Vector2d position : animals.keySet()){
            if(worldMap.getGrass().containsKey(position)){

                int maxEnergy = animals.get(position).getFirst().getEnergy();
                List<Animal> banqueters = animals.get(position).stream().filter(a -> a.getEnergy() == maxEnergy).toList();

                for(Animal animal:banqueters){
                    animal.gainEnergy(settings.getEnergyFromPlant()/banqueters.size());
                }
                worldMap.removeGrass(position);

            }
        }
    }

    private void moveAnimals() {
        for(Animal animal : worldMap.getAnimalsList()){
            animal.move(animal.getRandomGen());

        }
        for(Animal animal : worldMap.getAnimalsList()){
            animal.loseEnergy(settings.getEnergyLossPerMove());
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

    private ArrayList<Animal> drawParents(ArrayList <Animal> candidates,int quantity){
        Random rd = new Random();
        ArrayList <Animal> result = new ArrayList<>();
        Animal chosenOne;
        for(int i =0;i<quantity;i++){
            chosenOne= candidates.remove(rd.nextInt(candidates.size()));
            result.add(chosenOne);
        }
        return result;
    }

}
