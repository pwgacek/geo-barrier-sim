package pl.edu.agh.miss.geobarriersim.map.element;
import com.badlogic.gdx.graphics.Color;
import pl.edu.agh.miss.geobarriersim.map.IPositionChangeObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Animal implements IMapElement, Comparable<Animal> {

    private MapDirection direction ;
    private Vector2d position;
    private final int worldWidth;
    private final int worldHeight;
    private final ArrayList<IPositionChangeObserver> observerList;
    private int energy;
    private final ArrayList<Integer> genotype;
    private final int startEnergy;
    private final int dateOfBirth;
    private int childrenCounter;


    public Animal(int worldWidth, int worldHeight, Vector2d initialPosition, int startEnergy){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.position = initialPosition;
        this.direction = MapDirection.NORTH.generateMapDirection();
        observerList = new ArrayList<>();
        energy =startEnergy;
        genotype = generateGenotype();
        dateOfBirth =0;
        this.startEnergy = startEnergy;
        childrenCounter = 0;
    }

    public Animal(int worldWidth,int worldHeight, Vector2d initialPosition, Animal strongerParent, Animal weakerParent, int dateOfBirth, int startEnergy){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.position = initialPosition;
        this.direction = MapDirection.NORTH.generateMapDirection();
        observerList = new ArrayList<>();
        energy = getChildrenEnergy(strongerParent,weakerParent);
        strongerParent.loseEnergy((strongerParent.getEnergy()/4));
        weakerParent.loseEnergy((weakerParent.getEnergy()/4));
        genotype = getChildrenGenotype(strongerParent,weakerParent);
        this.dateOfBirth = dateOfBirth;
        this.startEnergy = startEnergy;
        childrenCounter = 0;
    }




    public int getDateOfBirth() {
        return dateOfBirth;
    }

    public int getChildrenCounter() {
        return childrenCounter;
    }

    public void incrementChildrenCounter() {
        this.childrenCounter+=1;
    }

    public MapDirection getDirection() { return direction; }
    public Vector2d position() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public void gainEnergy(int energy) {
        this.energy += energy;
    }

    public void loseEnergy(int energy) {
        this.energy -= energy;
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

    public int getRandomGen(){
        int randomIndex = new Random().nextInt(32);
        return genotype.get(randomIndex);
    }


    @Override
    public Color getColor() {
        int health = Math.max(0, Math.min(energy, startEnergy));

        float other = health / 100f;
        float blue = 1f;

        return new Color(other, other, blue, 1f);
    }

    private boolean canMoveTo(Vector2d position) {
        return position.x() < worldWidth && position.x() >= 0
                && position.y() < worldHeight && position.y() >= 0;
    }


    public void move(int rotation){
        int x = position.x();
        int y = position.y();

        switch (rotation) {
            case 0 -> {
                if (canMoveTo(position.add(direction.toUnitVector())) )
                {
                    position = position.add(direction.toUnitVector());
                    positionChanged(new Vector2d(x,y));

                }


            }
            case 4 -> {
                if (canMoveTo(position.subtract(direction.toUnitVector()))) {
                    position = position.subtract(direction.toUnitVector());
                    positionChanged(new Vector2d(x,y));
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

    private ArrayList<Integer> getChildrenGenotype(Animal strongerParent, Animal weakerParent){


        ArrayList<Integer> childrenGenotype = new ArrayList<>();

        int energySum = weakerParent.getEnergy() + strongerParent.getEnergy();
        int gensTakenFromStronger = (32*strongerParent.getEnergy()/energySum);

        boolean takeLeftSideFromStronger = new Random().nextBoolean();

        if(takeLeftSideFromStronger){
            for(int i=0;i<32;i++){
                if(i<gensTakenFromStronger){
                    childrenGenotype.add(strongerParent.genotype.get(i));
                }
                else{
                    childrenGenotype.add(weakerParent.genotype.get(i));
                }

            }
        }
        else{
            for(int i=0;i<32;i++){
                if(i<32-gensTakenFromStronger){
                    childrenGenotype.add(weakerParent.genotype.get(i));
                }
                else{
                    childrenGenotype.add(strongerParent.genotype.get(i));
                }

            }
        }
        Collections.sort(childrenGenotype);
        return childrenGenotype;
    }

    private int getChildrenEnergy(Animal mother, Animal father){
        return ((mother.getEnergy() + father.getEnergy())/4);
    }

    public ArrayList<Integer> getGenotype() {
        return genotype;
    }
}
