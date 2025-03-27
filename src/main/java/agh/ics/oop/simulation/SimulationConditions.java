package agh.ics.oop.simulation;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationConditions {

    private final int moveDelay;
    private final AtomicBoolean isRunning;
    private final int startEnergy;
    private final int moveEnergy;
    private final int plantEnergy;
    private final int animalQuantity;

    public SimulationConditions(int moveDelay, boolean isRunning, int startEnergy, int moveEnergy, int plantEnergy, int animalQuantity) {
        this.moveDelay = moveDelay;
        this.isRunning= new AtomicBoolean(isRunning);
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.animalQuantity = animalQuantity;
    }

    public int getMoveDelay() {
        return moveDelay;
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning){
        this.isRunning.set(isRunning);
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public int getMoveEnergy() {
        return moveEnergy;
    }

    public int getPlantEnergy() {
        return plantEnergy;
    }

    public int getAnimalQuantity() {
        return animalQuantity;
    }
}
