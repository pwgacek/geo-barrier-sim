package pl.edu.agh.miss.geobarriersim.logic.simulation;

public class SimulationSettings {
    private int mapSize;
    private int maxEnergy;
    private int energyLossPerMove;
    private int energyFromPlant;
    private int breedingCooldown;
    private int averageLifespan;
    private int initialAnimalCount;
    private int initialPlantPercentage;
    private int plantGrowthChancePer10000;

    public SimulationSettings() {
        this.mapSize = 100;
        this.maxEnergy = 1000;
        this.energyLossPerMove = 5;
        this.energyFromPlant = 200;
        this.breedingCooldown = 100;
        this.averageLifespan = 5000;
        this.initialAnimalCount = 50;
        this.initialPlantPercentage = 50;
        this.plantGrowthChancePer10000 = 3;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getEnergyLossPerMove() {
        return energyLossPerMove;
    }

    public void setEnergyLossPerMove(int energyLossPerMove) {
        this.energyLossPerMove = energyLossPerMove;
    }

    public int getEnergyFromPlant() {
        return energyFromPlant;
    }

    public void setEnergyFromPlant(int energyFromPlant) {
        this.energyFromPlant = energyFromPlant;
    }

    public int getBreedingCooldown() {
        return breedingCooldown;
    }

    public void setBreedingCooldown(int breedingCooldown) {
        this.breedingCooldown = breedingCooldown;
    }

    public int getAverageLifespan() {
        return averageLifespan;
    }

    public void setAverageLifespan(int averageLifespan) {
        this.averageLifespan = averageLifespan;
    }

    public int getInitialAnimalCount() {
        return initialAnimalCount;
    }

    public void setInitialAnimalCount(int initialAnimalCount) {
        this.initialAnimalCount = initialAnimalCount;
    }

    public int getPlantGrowthChancePer10000() {
        return plantGrowthChancePer10000;
    }

    public void setPlantGrowthChancePer10000(int plantGrowthChancePer10000) {
        this.plantGrowthChancePer10000 = plantGrowthChancePer10000;
    }

    public int getInitialPlantPercentage() {
        return initialPlantPercentage;
    }

    public void setInitialPlantPercentage(int initialPlantPercentage) {
        this.initialPlantPercentage = initialPlantPercentage;
    }
}
