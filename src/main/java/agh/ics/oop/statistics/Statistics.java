package agh.ics.oop.statistics;

import agh.ics.oop.map.element.Animal;

import java.util.*;

public class Statistics {
    private int animalQuantity;
    private int grassQuantity;
    private ArrayList<Integer> dominantGenotype;
    private double averageAnimalEnergy;
    private double averageLifeSpan = 0;
    private double averageChildrenQuantity;



    public int getAnimalQuantity() {
        return animalQuantity;
    }

    public void setAnimalQuantity(int animalQuantity) {
        this.animalQuantity = animalQuantity;
    }

    public int getGrassQuantity() {
        return grassQuantity;
    }

    public void setGrassQuantity(int grassQuantity) {
        this.grassQuantity = grassQuantity;
    }

    public ArrayList<Integer> getDominantGenotype() {
        return dominantGenotype;
    }

    public void setDominantGenotype(List<Animal> animals) {
        Map<ArrayList<Integer>, Integer> genotypeCounter = new HashMap<>();
        for(Animal animal : animals){

            if(genotypeCounter.containsKey(animal.getGenotype())){
                int num = genotypeCounter.get(animal.getGenotype());
                genotypeCounter.put(animal.getGenotype(),num+1);
            }
            else{
                genotypeCounter.put(animal.getGenotype(),1);
            }
        }

        HashMap.Entry<ArrayList<Integer>,Integer> maxEntry = null;

        for (Map.Entry<ArrayList<Integer>,Integer> entry : genotypeCounter.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        if(maxEntry!=null)dominantGenotype = maxEntry.getKey();
        else{

            dominantGenotype = null;
        }

    }

    public double getAverageAnimalEnergy() {
        return averageAnimalEnergy;
    }

    public void setAverageAnimalEnergy(List<Animal> animals) {
        if(animals.size()>0){
            int energySum = 0;
            for(Animal animal :animals){
                energySum+=animal.getEnergy();
            }
            double result = (double)energySum/animals.size();
            averageAnimalEnergy = Math.round(result*100.0)/100.0;
        }
        else{
            averageAnimalEnergy = 0;
        }

    }

    public double getAverageLifeSpan() {
        return averageLifeSpan;
    }

    public void setAverageLifeSpan(int deathCounter, int date, Animal animal) {
        int lifeSpan = date-animal.getDateOfBirth();
        double tmp = deathCounter* getAverageLifeSpan() + lifeSpan;
        double result = tmp/(deathCounter+1);
        averageLifeSpan = Math.round(result*100.0)/100.0;
    }

    public double getAverageChildrenQuantity() {
        return averageChildrenQuantity;
    }

    public void setAverageChildrenQuantity(List<Animal> animals) {
        if(animals.size() > 0){
            int sumOfChildren = 0;
            for(Animal animal:animals){
                sumOfChildren+=animal.getChildrenCounter();
            }
            double result = (double)sumOfChildren/animals.size();
            averageChildrenQuantity =Math.round(result*100.0)/100.0;
        }
        else{
            averageChildrenQuantity = 0;
        }
    }

}
