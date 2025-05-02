package pl.edu.agh.miss.geobarriersim.logic.map.element;
import com.badlogic.gdx.graphics.Color;
import pl.edu.agh.miss.geobarriersim.logic.map.IPositionChangeObserver;
import pl.edu.agh.miss.geobarriersim.logic.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.logic.simulation.SimulationSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Animal implements IMapElement, Comparable<Animal> {

    private MapDirection direction ;
    private Vector2d position;
    private final WorldMap worldMap;
    private final ArrayList<IPositionChangeObserver> observerList;
    private int energy;
    private final ArrayList<Integer> genotype;
    private final int maxEnergy;
    private final int energyLossPerMove;
    private final int dateOfBirth;
    private int childrenCounter;
    private int breedingCooldown;
    private final int averageLifespan;
    private int lifespan;

    public Animal(WorldMap worldMap, Vector2d initialPosition, SimulationSettings simulationSettings) {
        this.worldMap = worldMap;
        this.position = initialPosition;
        this.direction = MapDirection.NORTH.generateMapDirection();
        observerList = new ArrayList<>();
        genotype = generateGenotype();
        dateOfBirth =0;
        this.maxEnergy = simulationSettings.getMaxEnergy();
        energy = maxEnergy;
        this.energyLossPerMove = simulationSettings.getEnergyLossPerMove();
        this.breedingCooldown = simulationSettings.getBreedingCooldown();
        this.averageLifespan = simulationSettings.getAverageLifespan();
        this.lifespan = averageLifespan + (int)(new Random().nextGaussian() * averageLifespan / 2);
        childrenCounter = 0;
    }

    private Animal(Pair<Animal> parents, int dateOfBirth, int startEnergy){
        this.worldMap = parents.first().worldMap;
        this.position = parents.first().position;
        this.direction = MapDirection.NORTH.generateMapDirection();
        observerList = new ArrayList<>();
        this.energy = startEnergy;
        genotype = getChildrenGenotype(parents);
        this.dateOfBirth = dateOfBirth;
        this.maxEnergy = parents.first().maxEnergy;
        this.energyLossPerMove = parents.first().energyLossPerMove;
        this.breedingCooldown = parents.first().breedingCooldown;
        this.averageLifespan = parents.first().averageLifespan;
        this.lifespan = averageLifespan + (int)(new Random().nextGaussian() * averageLifespan / 2);

        childrenCounter = 0;
    }

    public boolean isDead(){
        if (lifespan > 0) lifespan--;
        return lifespan == 0 || energy <= 0;
    }

    public boolean canBreed(){
        if (breedingCooldown > 0) breedingCooldown--;
        return breedingCooldown == 0 && energy >= (int)(0.8 * maxEnergy);
    }

    public static Animal breed(Pair<Animal> parents, int dayOfBirth) {

        int childEnergy = parents.first().breed() + parents.second().breed();

        return new Animal(parents, dayOfBirth, childEnergy);

    }




    public int getDateOfBirth() {
        return dateOfBirth;
    }

    public int getChildrenCounter() {
        return childrenCounter;
    }

    private void incrementChildrenCounter() {
        this.childrenCounter+=1;
    }

    public MapDirection getDirection() { return direction; }
    public Vector2d position() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public void eat(int energy) {
        this.energy  = Math.min(this.energy + energy, maxEnergy);
    }

    private void looseEnergy(int energy) {
        this.energy -= energy;
    }

    private int breed() {
        incrementChildrenCounter();
        int looseEnergy = energy / 4;
        looseEnergy(looseEnergy);
        breedingCooldown = 100;
        return looseEnergy;
    }

    private ArrayList<Integer> generateGenotype(){
        Random rd = new Random();
        ArrayList<Integer> genotype = new ArrayList<>();
        for(int i=0;i<32;i++){
            genotype.add(rd.nextInt(8));
        }
        Collections.sort(genotype);
        return genotype;
    }

    private int getRandomGen(){
        int randomIndex = new Random().nextInt(32);
        return genotype.get(randomIndex);
    }


    @Override
    public Color getColor() {
        int health = Math.max(0, Math.min(energy, maxEnergy));

        float other = health / 100f;
        float blue = 1f;

        return new Color(other, other, blue, 1f);
    }


    public void move(){
        looseEnergy(energyLossPerMove);

        int rotation = getRandomGen();

        Vector2d oldPosition = position;
        switch (rotation) {
            case 0 -> {
                if (worldMap.canMoveTo(oldPosition, position.add(direction.toUnitVector())) )
                {
                    position = position.add(direction.toUnitVector());
                    positionChanged(oldPosition);
                }
            }
            case 4 -> {
                if (worldMap.canMoveTo(oldPosition, position.subtract(direction.toUnitVector()))) {
                    position = position.subtract(direction.toUnitVector());
                    positionChanged(oldPosition);
                }
            }

            default ->direction = direction.rotate(rotation);
        }

    }

    public void addObserver(IPositionChangeObserver observer){
        observerList.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        observerList.remove(observer);
    }
    public void positionChanged(Vector2d oldPosition){
        for(IPositionChangeObserver observer:observerList){
            observer.positionChanged(oldPosition,this);
        }

    }

    @Override
    public int compareTo(Animal other) {
        return other.energy - this.energy;
    }

    private ArrayList<Integer> getChildrenGenotype(Pair<Animal> parents){


        ArrayList<Integer> childrenGenotype = new ArrayList<>();

        int energySum = parents.first().getEnergy() + parents.second().getEnergy();
        int gensFromFirstParent = (32*parents.first().getEnergy() / energySum);

        boolean takeLeftSideFromFirstParent = new Random().nextBoolean();

        if(takeLeftSideFromFirstParent){
            for(int i=0;i<32;i++){
                if(i<gensFromFirstParent){
                    childrenGenotype.add(parents.first().genotype.get(i));
                }
                else{
                    childrenGenotype.add(parents.second().genotype.get(i));
                }

            }
        }
        else{
            for(int i=0;i<32;i++){
                if(i<32-gensFromFirstParent){
                    childrenGenotype.add(parents.second().genotype.get(i));
                }
                else{
                    childrenGenotype.add(parents.first().genotype.get(i));
                }

            }
        }
        Collections.sort(childrenGenotype);
        return childrenGenotype;
    }

    public ArrayList<Integer> getGenotype() {
        return genotype;
    }
}
