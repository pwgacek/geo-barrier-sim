package agh.ics.oop.gui.options;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OptionsVBox extends VBox {

    private final OptionsElementHBox sizeVBox;
    private final OptionsElementHBox startEnergyVBox;
    private final OptionsElementHBox moveEnergyVBox;
    private final OptionsElementHBox plantEnergyVBox;
    private final OptionsElementHBox animalQuantityVBox;
    private final OptionsElementHBox plantGrowChanceVBox;

    public OptionsVBox(){
        this.setSpacing(15);
        this.setAlignment(javafx.geometry.Pos.CENTER);

        Label settingsLabel = new Label("Settings");
        settingsLabel.setFont(new Font(24));
        settingsLabel.setAlignment(javafx.geometry.Pos.CENTER);
        this.getChildren().add(settingsLabel);


        sizeVBox = new OptionsElementHBox(new Label("width:"),new TextField("10"),true);
        this.getChildren().add(sizeVBox);
        startEnergyVBox = new OptionsElementHBox(new Label("start energy:"),new TextField("100"),true);
        this.getChildren().add(startEnergyVBox);
        moveEnergyVBox = new OptionsElementHBox(new Label("move energy:"),new TextField("1"),true);
        this.getChildren().add(moveEnergyVBox);
        plantEnergyVBox = new OptionsElementHBox(new Label("plant energy:"),new TextField("10"),true);
        this.getChildren().add(plantEnergyVBox);
        animalQuantityVBox = new OptionsElementHBox(new Label("animal quantity:"),new TextField("20"),true);
        this.getChildren().add(animalQuantityVBox);
        plantGrowChanceVBox = new OptionsElementHBox(new Label("plant grow chance (‰):"),new TextField("10"),true);
        this.getChildren().add(plantGrowChanceVBox);

    }
    public int getSizeCondition(){

        return Integer.parseInt(this.sizeVBox.getValue());
    }


    public int getStartEnergyCondition(){

        return Integer.parseInt(this.startEnergyVBox.getValue());
    }

    public int getMoveEnergyCondition(){

        return Integer.parseInt(this.moveEnergyVBox.getValue());
    }

    public int getPlantEnergyCondition(){

        return Integer.parseInt(this.plantEnergyVBox.getValue());
    }

    public int getAnimalQuantityCondition(){
        return Integer.parseInt(this.animalQuantityVBox.getValue());
    }

    public int getPlantGrowChanceCondition(){
        return Integer.parseInt(this.plantGrowChanceVBox.getValue());
    }





}


