package agh.ics.oop.simulation;

import agh.ics.oop.gui.visualization.MapHandlerHBox;
import agh.ics.oop.gui.visualization.MapVisualizer;
import agh.ics.oop.map.element.Animal;
import agh.ics.oop.map.element.Vector2d;
import agh.ics.oop.map.WorldMap;
import agh.ics.oop.statistics.Snapshot;
import agh.ics.oop.statistics.Statistician;
import agh.ics.oop.statistics.Statistics;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SimulationEngine  extends Thread{

    private final WorldMap worldMap;
    private int dayCounter;

    private final MapVisualizer observer;
    private final SimulationConditions conditions;
    private final Statistics statistics;
    private int deathCounter;
    private final Statistician statistician;
    private boolean isTerminated;
    private final MapHandlerHBox mapHandlerHBox;




    public SimulationEngine(MapHandlerHBox mapHandlerHBox, WorldMap worldMap, MapVisualizer mapVisualizer) {

        isTerminated=false;
        this.worldMap = worldMap;
        this.mapHandlerHBox = mapHandlerHBox;
        this.observer = mapVisualizer;
        this.conditions = mapHandlerHBox.getConditions();
        this.statistics = new Statistics();
        this.deathCounter = 0;
        this.dayCounter = 0;

        for(int i=0;i<conditions.getAnimalQuantity();i++){
            Random rand = new Random();
            Vector2d position = new Vector2d(rand.nextInt(worldMap.getSize()), rand.nextInt(worldMap.getSize()));
            worldMap.place(new Animal(worldMap.getSize(), position,conditions.getStartEnergy()));
        }

        statistician = new Statistician(mapHandlerHBox);

    }



    @Override
    public void run() {

        updateStatistics();
        statistician.addSnapshot(new Snapshot(dayCounter,statistics));
        statistician.updateDominantGenotypeLabel(statistics.getDominantGenotype());


        Platform.runLater(observer::positionChanged);
        waitForRunLater();

        while(!worldMap.getAnimalsList().isEmpty()){
            dayCounter++;
            if(!conditions.isRunning()){
                statistician.writeStatisticsHistoryToFile();
                suspendMe(conditions.getIsRunning());

                if(isTerminated){break;}

            }


            removeDeadAnimals();// usuwanie martwych zwierząt
            moveAnimals();// ruch albo skręt zwierzęcia
            feedAnimals();//jedzenie roślin
            breedAnimals();// rozmnażanie zwierząt
            addNewGrass();// dodanie nowych roślin

            long start = System.currentTimeMillis();

            Platform.runLater(observer::positionChanged);
            try {
                Thread.sleep(conditions.getMoveDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
            waitForRunLater();

            System.out.println(System.currentTimeMillis()-start);
            updateStatistics();
            statistician.addSnapshot(new Snapshot(dayCounter,statistics));
            statistician.updateDominantGenotypeLabel(statistics.getDominantGenotype());


        }

        if(!isTerminated){

            statistician.writeStatisticsHistoryToFile();
            conditions.setIsRunning(false);
            Platform.runLater(this.mapHandlerHBox::disableStopStartBtn);

        }


    }

    private void updateStatistics() {
        statistics.setAnimalQuantity(worldMap.getAnimalsList().size());
        statistics.setGrassQuantity(worldMap.getGrass().size());
        statistics.setDominantGenotype(worldMap.getAnimalsList());
        statistics.setAverageAnimalEnergy(worldMap.getAnimalsList());
        statistics.setAverageChildrenQuantity(worldMap.getAnimalsList());
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
                    strongerParent = candidates.get(0);
                    candidates = (ArrayList<Animal>) animals.get(position).stream().filter(a -> a.getEnergy() == secondMaxEnergy).collect(Collectors.toList());
                    weakerParent = drawParents(candidates,1).get(0);

                }

                if(weakerParent.getEnergy() >= (conditions.getStartEnergy()/2) && strongerParent.getEnergy()>0){
                    weakerParent.incrementChildrenCounter();
                    strongerParent.incrementChildrenCounter();
                    Animal child = new Animal(worldMap.getSize(), position,strongerParent,weakerParent,dayCounter,conditions.getStartEnergy());
                    worldMap.place(child);

                }

            }
        }
    }

    private void feedAnimals() {
        Map<Vector2d, ArrayList<Animal>> animals = worldMap.getAnimals();
        for(Vector2d position : animals.keySet()){
            if(worldMap.getGrass().containsKey(position)){

                int maxEnergy = animals.get(position).get(0).getEnergy();
                List<Animal> banqueters = animals.get(position).stream().filter(a -> a.getEnergy() == maxEnergy).collect(Collectors.toList());

                for(Animal animal:banqueters){
                    animal.addEnergy(conditions.getPlantEnergy()/banqueters.size());
                }
                 worldMap.removeGrassFromSavanna(position);

            }
        }
    }

    private void moveAnimals() {
        for(Animal animal : worldMap.getAnimalsList()){
            animal.move(animal.getRandomGen());

        }
        for(Animal animal : worldMap.getAnimalsList()){
            animal.addEnergy(-conditions.getMoveEnergy());
        }
    }

    private void removeDeadAnimals() {
        ArrayList<Animal> animalsToRemove = new ArrayList<>();
        for(Animal animal : worldMap.getAnimalsList()){
            if(animal.getEnergy()-conditions.getMoveEnergy() < 0){
                animalsToRemove.add(animal);
                statistics.setAverageLifeSpan(deathCounter,dayCounter,animal);
                deathCounter++;
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

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
        conditions.setIsRunning(true);
        resumeMe();
    }

    synchronized protected void suspendMe(AtomicBoolean isRunning){
        try {
            while(!isRunning.get()){
                wait();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void resumeMe(){
        notifyAll();
    }

    public static void waitForRunLater()  {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
