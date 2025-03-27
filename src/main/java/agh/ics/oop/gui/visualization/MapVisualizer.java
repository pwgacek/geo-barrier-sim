package agh.ics.oop.gui.visualization;

import agh.ics.oop.map.element.IMapElement;
import agh.ics.oop.map.element.Vector2d;
import agh.ics.oop.map.WorldMap;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.concurrent.atomic.AtomicBoolean;


public class MapVisualizer {
    private long time = System.currentTimeMillis();
    private final WorldMap worldMap;
    private final GuiElementBox[][] guiElementBoxArray;





    MapVisualizer(GridPane gridPane, WorldMap worldMap, double cellSize, AtomicBoolean isRunning){

        for(int x = 0; x < worldMap.getSize(); x++){
            gridPane.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for(int y = 0; y < worldMap.getSize(); y++){
            gridPane.getRowConstraints().add(new RowConstraints(cellSize));
        }

        long startTime = System.currentTimeMillis();

        guiElementBoxArray = new GuiElementBox[worldMap.getSize()][worldMap.getSize()];
        for(int y = 0; y < worldMap.getSize(); y++){
            for(int x = 0; x < worldMap.getSize(); x++){
                guiElementBoxArray[x][y] = new GuiElementBox(cellSize,isRunning);
                gridPane.add(guiElementBoxArray[x][y],x, worldMap.getSize()- y - 1);
            }
        }
        System.out.println("time: " + (System.currentTimeMillis() - startTime));

        this.worldMap = worldMap;


    }



    public void positionChanged() {
        for(int y = 0; y < worldMap.getSize(); y++){
            for(int x = 0; x < worldMap.getSize(); x++){
                IMapElement element = (IMapElement) worldMap.objectAt(new Vector2d(x,y));

                if(element != null){
                    guiElementBoxArray[x][y].putIfChanged(element);
                }
                else{
                    guiElementBoxArray[x][y].clean();
                }

            }
        }
    }
}
