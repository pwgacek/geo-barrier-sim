package agh.ics.oop;

import agh.ics.oop.map.element.Animal;
import agh.ics.oop.map.element.Vector2d;
import agh.ics.oop.map.WorldMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    @Test
    public void breedingTest() {
        WorldMap worldMap = new WorldMap(new MapConditions( 10, 0.5));
        Vector2d position = new Vector2d(0,0);
        Animal a1 = new Animal(worldMap,position, 24);
        Animal a2 = new Animal(worldMap, position, 8);

        Animal a3 = new Animal(worldMap,position,a1,a2,0,16);
        ArrayList<Integer> g1 = new ArrayList<>();
        ArrayList<Integer> g2 = new ArrayList<>();
        for(int i = 0;i<32;i++){
            if(i<24){
                g1.add(a1.getGenotype().get(i));
            }
            else{
                g1.add(a2.getGenotype().get(i));
            }
            if(i<8){
                g2.add(a2.getGenotype().get(i));
            }
            else{
                g2.add(a1.getGenotype().get(i));
            }
        }
        Collections.sort(g1);
        Collections.sort(g2);
        assertTrue(a3.getGenotype().equals(g1)||a3.getGenotype().equals(g2));
    }

    @Test
    public void moveTest(){
        AbstractWorldMap map = new WorldMap(new MapConditions(10, 10, 0.5));

        Vector2d position = new Vector2d(4,4);
        Animal a0 = new Animal(map,position, 24);


        map.place(a0);
        a0.move(0);

        switch(a0.getDirection()){
            case NORTH -> assertEquals(a0.position(),new Vector2d(4,5));
            case NORTH_EAST -> assertEquals(a0.position(),new Vector2d(5,5));
            case EAST -> assertEquals(a0.position(),new Vector2d(5,4));
            case SOUTH_EAST -> assertEquals(a0.position(),new Vector2d(5,3));
            case SOUTH -> assertEquals(a0.position(),new Vector2d(4,3));
            case SOUTH_WEST -> assertEquals(a0.position(),new Vector2d(3,3));
            case WEST -> assertEquals(a0.position(),new Vector2d(3,4));
            case NORTH_WEST -> assertEquals(a0.position(),new Vector2d(3,5));
        }

    }
    @Test
    public void boundaryTest(){
        AbstractWorldMap map = new WorldMap(new MapConditions(1, 1, 0.5));
        //dla wysokosci i szerokosci 1 mapa 4 pola; dla wysokosci i szerokosci 0 mapa zawiera 1 pole
        Vector2d position = new Vector2d(0,0);
        Animal a0 = new Animal(map,position, 24);
        map.place(a0);

        a0.move(0);


        switch(a0.getDirection()){
            case NORTH -> assertEquals(a0.position(),new Vector2d(0,1));
            case NORTH_EAST -> assertEquals(a0.position(),new Vector2d(1,1));
            case EAST -> assertEquals(a0.position(),new Vector2d(1,0));
            case SOUTH_EAST -> assertEquals(a0.position(),new Vector2d(0,0));
            case SOUTH -> assertEquals(a0.position(),new Vector2d(0,0));
            case SOUTH_WEST -> assertEquals(a0.position(),new Vector2d(0,0));
            case WEST -> assertEquals(a0.position(),new Vector2d(0,0));
            case NORTH_WEST -> assertEquals(a0.position(),new Vector2d(0,0));
        }

    }


}
