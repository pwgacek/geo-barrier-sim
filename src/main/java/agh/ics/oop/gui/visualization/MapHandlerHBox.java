package agh.ics.oop.gui.visualization;

import agh.ics.oop.simulation.SimulationConditions;
import agh.ics.oop.map.WorldMap;
import agh.ics.oop.simulation.SimulationEngine;
import agh.ics.oop.statistics.Snapshot;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MapHandlerHBox extends HBox {

    private final SimulationEngine engineThread;
    private final Button stopStartBtn;
    private final Label dominantGenotypeLabel;
//    private final DoubleStatsChart animalAndGrassChart;
//    private final StatsChart energyChart;
//    private final StatsChart lifeSpanChart;
//    private final StatsChart childrenQuantityChart;
    private final SimulationConditions conditions;

    public MapHandlerHBox(WorldMap worldMap, SimulationConditions conditions){

        this.conditions = conditions;
        double cellSize = Math.min(900/(worldMap.getSize()),900/(worldMap.getSize()));
        AtomicBoolean isRunning = this.conditions.getIsRunning();

        GridPane mapGridPane = new GridPane();
        mapGridPane.setAlignment(Pos.CENTER);
        mapGridPane.setPadding(new Insets(20,20,20,20));


        stopStartBtn = new Button("START");
        stopStartBtn.setPrefWidth(70);
        StackPane.setMargin(stopStartBtn, new Insets(20, 30, 20, 30));

        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().add(stopStartBtn);
        dominantGenotypeLabel = new Label();
        dominantGenotypeLabel.setFont(new Font(12));



//        animalAndGrassChart = new DoubleStatsChart("quantity of animals(red) & grass(orange)");
//        energyChart = new StatsChart("average animal energy");
//        lifeSpanChart = new StatsChart("average life span");
//        childrenQuantityChart = new StatsChart("average children quantity");
//
//        ChartsHolder chartsHolder = new ChartsHolder(animalAndGrassChart,energyChart,lifeSpanChart,childrenQuantityChart);

//        GridPane.setConstraints(chartsHolder,0,2,2,1);

        this.getChildren().add(mapGridPane);
        this.getChildren().add(root);

//        this.getChildren().add(chartsHolder);



        MapVisualizer mapVisualizer = new MapVisualizer(mapGridPane, worldMap,cellSize,isRunning);
        engineThread =  new SimulationEngine(this, worldMap, mapVisualizer);


        stopStartBtn.setOnAction(e2 -> {
            if(conditions.isRunning()) {
                conditions.setIsRunning(false);
                stopStartBtn.setText("START");
            }
            else{
                conditions.setIsRunning(true);
                engineThread.resumeMe();
                stopStartBtn.setText("STOP");
            }

        });

    }

    public void startSimulation(){
        this.engineThread.start();
    }

   public void terminateSimulation(){
        if(this.engineThread.isAlive()) this.engineThread.setTerminated(true);
        else System.out.println("wątek juz nie zyje");
   }

    public SimulationConditions getConditions() {
        return conditions;
    }

    public void disableStopStartBtn(){
        stopStartBtn.setText("STOP");
        stopStartBtn.setDisable(true);
    }

    public void updateCharts(Snapshot snapshot){

//        this.animalAndGrassChart.update(snapshot.getAnimalQuantity(),snapshot.getGrassQuantity());
//        this.energyChart.update(snapshot.getAverageAnimalEnergy());
//        this.lifeSpanChart.update(snapshot.getAverageLifeSpan());
//        this.childrenQuantityChart.update(snapshot.getAverageChildrenQuantity());

    }

    public void updateDominantGenotypeLabel(ArrayList<Integer> dominantGenotype){
        if(dominantGenotype!=null)this.dominantGenotypeLabel.setText("dominant genotype: "+dominantGenotype);
        else this.dominantGenotypeLabel.setText("dominant genotype: none");
    }

}
