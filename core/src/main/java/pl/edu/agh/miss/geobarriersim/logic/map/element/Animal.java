package pl.edu.agh.miss.geobarriersim.logic.map.element;
import com.badlogic.gdx.graphics.Color;
import pl.edu.agh.miss.geobarriersim.logic.map.IPositionChangeObserver;
import pl.edu.agh.miss.geobarriersim.logic.map.WorldMap;
import pl.edu.agh.miss.geobarriersim.logic.simulation.SimulationSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Animal implements IMapElement, Comparable<Animal> {

    private final Vector2d[] movementMask = {
            new Vector2d(0, 1),
            new Vector2d(1, 1),
            new Vector2d(1, 0),
            new Vector2d(1, -1),
            new Vector2d(0, -1),
            new Vector2d(-1, -1),
            new Vector2d(-1, 0),
            new Vector2d(-1, 1)
    };

    private static final Random RANDOM = new Random();

    private final WorldMap worldMap;
    private final ArrayList<IPositionChangeObserver> observerList;
    private final Genes genes;
    private final int energyLossPerMove;
    private final int maxEnergy;
    private final int breedingCooldown;
    private final int averageLifespan;

    private Vector2d position;
    private int energy;
    private int breedingCooldownLeft;
    private int lifespan;

    public Animal(WorldMap worldMap, Vector2d initialPosition, SimulationSettings simulationSettings) {
        this.worldMap = worldMap;
        observerList = new ArrayList<>();

        this.genes = Genes.getDefault();
        this.maxEnergy = simulationSettings.getMaxEnergy();
        this.energyLossPerMove = simulationSettings.getEnergyLossPerMove();
        this.breedingCooldown = simulationSettings.getBreedingCooldown();
        this.averageLifespan = simulationSettings.getAverageLifespan();

        this.position = initialPosition;
        this.energy = maxEnergy;
        this.breedingCooldownLeft = breedingCooldown;
        this.lifespan = averageLifespan + (int)(new Random().nextGaussian() * averageLifespan / 2);
    }

    private Animal(Pair<Animal> parents){
        this.worldMap = parents.first().worldMap;
        this.observerList = new ArrayList<>();

        this.genes = Genes.combine(parents.first().genes, parents.second().genes);
        this.maxEnergy = parents.first().maxEnergy;
        this.energyLossPerMove = parents.first().energyLossPerMove;
        this.breedingCooldown = parents.first().breedingCooldown;
        this.averageLifespan = parents.first().averageLifespan;

        this.position = parents.first().position;
        this.energy = parents.first().maxEnergy;
        this.breedingCooldownLeft = breedingCooldown;
        this.lifespan = averageLifespan + (int)(new Random().nextGaussian() * averageLifespan / 2);
    }

    public boolean isDead(){
        if (lifespan > 0) lifespan--;
        return lifespan == 0 || energy <= 0;
    }

    public boolean canBreed(){
        if (breedingCooldownLeft > 0) breedingCooldownLeft--;
        return breedingCooldownLeft == 0 && !needsToEat();
    }

    public static Animal breed(Pair<Animal> parents) {
        parents.first().breed();
        parents.second().breed();

        return new Animal(parents);

    }

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

    private void breed() {
        breedingCooldownLeft = breedingCooldown;
    }

    @Override
    public Color getColor() {
        int health = Math.max(0, Math.min(energy, maxEnergy));

        float other = health / 100f;
        float blue = 1f;

        return new Color(other, other, blue, 1f);
    }

    private double getEnergyPercentage() {
        return (double) energy / maxEnergy;
    }

    private boolean needsToEat() {
        return genes.hungerThreshold >= getEnergyPercentage();
    }

    private Focus[] getPriorities() {
        if (breedingCooldownLeft == 0 && !needsToEat()) {
            return new Focus[]{Focus.BREEDING, Focus.FOOD, Focus.NOTHING};
        } else if (needsToEat()) {
            return new Focus[]{Focus.FOOD, Focus.NOTHING};
        } else {
            return new Focus[]{Focus.NOTHING};
        }
    }


    private List<Vector2d> getPossiblePositions() {
        List<Vector2d> possiblePositions = Arrays.stream(movementMask)
            .map(it -> position.add(it))
            .filter(worldMap::canMoveTo)
            .collect(Collectors.toList());

        Collections.shuffle(possiblePositions);
        return possiblePositions;
    }

    private boolean wantsToRoam() {
        return RANDOM.nextDouble() < genes.roamTendency;
    }

    public void move(){
        looseEnergy(energyLossPerMove);
        if(RANDOM.nextDouble() < genes.speed) {
            Vector2d oldPosition = position;
            List<Vector2d> possibleNewPositions = getPossiblePositions();
            boolean[] moved = {false};
            for (Focus focus : getPriorities()) {
                if (focus == Focus.FOOD) {
                    if (worldMap.isPlantGrownAt(position)) {
                        break;
                    } else {
                        possibleNewPositions.stream()
                            .filter(worldMap::isPlantGrownAt)
                            .findAny()
                            .ifPresent(it -> performMove(it, oldPosition, moved));
                        if (moved[0]) break;
                    }
                }
                if (focus == Focus.BREEDING) {
                    if (worldMap.isOtherAnimalAt(this)) {
                        break;
                    } else {
                        possibleNewPositions.stream()
                            .filter(worldMap::isAnimalAt)
                            .findAny()
                            .ifPresent(it -> performMove(it, oldPosition, moved));
                        if (moved[0]) break;
                    }
                }
                if (focus == Focus.NOTHING) {
                    if (wantsToRoam()) {
                        possibleNewPositions.stream()
                            .findAny()
                            .ifPresent(it -> performMove(it, oldPosition, moved));
                    }
                }
            }
        }


    }

    private void performMove(Vector2d it, Vector2d oldPosition, boolean[] moved) {
        position = it;
        positionChanged(oldPosition);
        moved[0] = true;
        looseEnergy(energyLossPerMove);
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

    public Genes getGenes() {
        return genes;
    }

}
