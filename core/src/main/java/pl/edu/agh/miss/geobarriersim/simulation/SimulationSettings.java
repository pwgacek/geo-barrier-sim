package pl.edu.agh.miss.geobarriersim.simulation;

public class SimulationSettings {
    private int mapSize;
    private int startEnergy;
    private int energyLossPerMove;
    private int energyFromPlant;
    private int initialAnimalCount;
    private int initialGrassPercentage;
    private int plantGrowthChancePer10000;

    public SimulationSettings() {
        this.mapSize = 100;
        this.startEnergy = 100;
        this.energyLossPerMove = 1;
        this.energyFromPlant = 50;
        this.initialAnimalCount = 50;
        this.initialGrassPercentage = 75;
        this.plantGrowthChancePer10000 = 5;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public int getStartEnergy() {
        return startEnergy;
    }

    public void setStartEnergy(int startEnergy) {
        this.startEnergy = startEnergy;
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

    public int getInitialGrassPercentage() {
        return initialGrassPercentage;
    }

    public void setInitialGrassPercentage(int initialGrassPercentage) {
        this.initialGrassPercentage = initialGrassPercentage;
    }
}
