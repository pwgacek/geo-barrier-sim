package pl.edu.agh.miss.geobarriersim.simulation;

public class SimulationSettings {
    private int mapSize;
    private int startEnergy;
    private int energyLossPerMove;
    private int energyFromPlant;
    private int initialAnimalCount;
    private int plantGrowthChancePer1000;

    public SimulationSettings() {
        this.mapSize = 100;
        this.startEnergy = 100;
        this.energyLossPerMove = 1;
        this.energyFromPlant = 20;
        this.initialAnimalCount = 20;
        this.plantGrowthChancePer1000 = 1;
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

    public int getPlantGrowthChancePer1000() {
        return plantGrowthChancePer1000;
    }

    public void setPlantGrowthChancePer1000(int plantGrowthChancePer1000) {
        this.plantGrowthChancePer1000 = plantGrowthChancePer1000;
    }
}
