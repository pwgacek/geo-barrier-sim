package agh.ics.oop.gui;

import agh.ics.oop.simulation.SimulationConditions;
import agh.ics.oop.gui.options.OptionsVBox;
import agh.ics.oop.gui.visualization.MapHandlerHBox;
import agh.ics.oop.map.WorldMap;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class  App extends Application  {


    @Override
    public void start(Stage primaryStage){
        // Create label and button
        OptionsVBox optionsGridPane = new OptionsVBox();

        Button startButton = new Button("Start simulation");



        // Layout with vertical centering
        VBox vbox = new VBox(50, optionsGridPane, startButton); // 10px spacing
        vbox.setAlignment(javafx.geometry.Pos.CENTER); // Center elements

        // Scene and Stage setup
        Scene scene = new Scene(vbox, 400, 450);
        primaryStage.setMinHeight(scene.getHeight());
        primaryStage.setMinWidth(scene.getWidth());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.setResizable(false);
        primaryStage.show();

        startButton.setOnAction(e -> {
            try{
                if((optionsGridPane.getSizeCondition()+1)*(optionsGridPane.getSizeCondition()+1) < optionsGridPane.getAnimalQuantityCondition())throw new IllegalArgumentException("Map doesn't have enough space for animals!");
                if(optionsGridPane.getMoveEnergyCondition()==0)throw new IllegalArgumentException("Move Energy should be above 0");
                if(optionsGridPane.getAnimalQuantityCondition()<10)throw new IllegalArgumentException("Minimum quantity of animals is 10");

                SimulationConditions simulationConditions = new SimulationConditions(50,false,optionsGridPane.getStartEnergyCondition(),optionsGridPane.getMoveEnergyCondition(),optionsGridPane.getPlantEnergyCondition(),optionsGridPane.getAnimalQuantityCondition());

                WorldMap worldMap = new WorldMap(optionsGridPane.getSizeCondition(), optionsGridPane.getPlantGrowChanceCondition());


                MapHandlerHBox mapHandlerHBox = new MapHandlerHBox(worldMap,simulationConditions);

                Scene simulationScene = new Scene(mapHandlerHBox);

                primaryStage.setFullScreen(true);
                primaryStage.setMinHeight(scene.getHeight());
                primaryStage.setMinWidth(scene.getWidth());
                primaryStage.setScene(simulationScene);
                primaryStage.setTitle("Simulation");
                primaryStage.setResizable(false);




                mapHandlerHBox.startSimulation();




                primaryStage.setOnCloseRequest(t -> {
                    if(simulationConditions.isRunning())
                    {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sth went wrong...");
                        alert.setHeaderText("Please, stop simulation before you close the window!");
                        alert.showAndWait();
                        t.consume();
                    }
                    else{
                        mapHandlerHBox.terminateSimulation();
                        Platform.exit();
                        System.exit(0);
                    }

                });




            }catch(Exception exception){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sth went wrong...");
                alert.setHeaderText(exception.getMessage());
                alert.setContentText("Please, insert correct data.");
                alert.showAndWait();
            }



        });


    }



}
