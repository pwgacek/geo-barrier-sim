package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.util.Map;

public class MapVisualizer {
    private GridPane gridPane;
    private final AbstractWorldMap map;
    private final Stage primaryStage;
    private final GuiElementBox[][] guiElementBoxArray;





    MapVisualizer(GridPane gridPane, AbstractWorldMap map, Stage primaryStage){
        this.gridPane = gridPane;
//        gridPane.setMinHeight(360);
//        gridPane.setMaxHeight(360);
//        gridPane.setMinWidth(360);
//        gridPane.setMaxWidth(360);
        for(int x=0;x<=map.getWidth()+1;x++){
            gridPane.getColumnConstraints().add(new ColumnConstraints(30));
        }
        for(int y=0;y<=map.getHeight()+1;y++){
            gridPane.getRowConstraints().add(new RowConstraints(30));
        }
        guiElementBoxArray = new GuiElementBox[(map.getWidth()+1)][(map.getHeight()+1)];
        Label label = new Label("y/x");
        gridPane.add(label,0,0);
        GridPane.setHalignment(label, HPos.CENTER);

        for(int x=0;x<=map.getWidth();x++){
            label = new Label(String.valueOf(x));
            gridPane.add(label,x+1,0);
            GridPane.setHalignment(label, HPos.CENTER);

        }
        for(int y=0;y<=map.getHeight();y++){
            label = new Label(String.valueOf(map.getHeight()-y));
            gridPane.add(label,0,y+1);
            GridPane.setHalignment(label, HPos.CENTER);


        }

        for(int y=0;y<=map.getHeight();y++){
            for(int x=0;x<=map.getWidth();x++){
                boolean isJungle = y>3 && y<7 && x>3 && x<7;
                guiElementBoxArray[x][y] = new GuiElementBox(isJungle);
                gridPane.add(guiElementBoxArray[x][y].getVerticalBox(),x+1,map.getHeight()-y +1);
                GridPane.setHalignment(guiElementBoxArray[x][y].getVerticalBox(), HPos.CENTER);
            }
        }


        this.map = map;
        this.primaryStage = primaryStage;
        this.primaryStage.show();

    }



    public void positionChanged() {
        gridPane.setGridLinesVisible(false);
        //gridPane.getChildren().clear();
        gridPane.setGridLinesVisible(true);



        for(int y = 0; y<=map.getHeight();y++){
            for(int x = 0;x <=map.getWidth();x++){
                IMapElement element = (IMapElement) map.objectAt(new Vector2d(x,y));


                if(element !=null){

                    guiElementBoxArray[x][y].setImageView(element);

                }
                else{
                    guiElementBoxArray[x][y].getVerticalBox().getChildren().clear();

                }

            }
        }

        //this.primaryStage.setScene(new Scene(this.getGridPane(),this.getGridPane().getColumnCount()*30,this.getGridPane().getRowCount()*30));
        //this.primaryStage.show();
    }
}
