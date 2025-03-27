package agh.ics.oop.gui.visualization;
import agh.ics.oop.map.element.Animal;
import agh.ics.oop.map.element.Grass;
import agh.ics.oop.map.element.IMapElement;
import javafx.geometry.Insets;

import javafx.scene.control.*;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;


public class GuiElementBox extends VBox {

    private static final Tooltip tooltip = new Tooltip();

    static{
        tooltip.setShowDelay(new Duration(0));
    }

    private IMapElement element;
    private Color color;
    private final double cellSize;


    public GuiElementBox(double cellSize, AtomicBoolean isRunning){
        this.cellSize = cellSize;

        BackgroundFill fill = new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY);

        this.setBackground(new Background(fill));


        this.setOnMouseEntered((event)->{
            if(!isRunning.get()){
                if(element!=null){
                    if(this.element.getClass() == Animal.class && !tooltip.isShowing()){

                        tooltip.setText("genotype: "+((Animal) element).getGenotype().toString());
                        Tooltip.install(this,tooltip);
                    }
                }
            }
        });

        this.setOnMouseExited(event -> {
            Tooltip.uninstall(this,tooltip);
        });
    }


    public void putIfChanged(IMapElement element){
        if (element.equals(this.element)){

            if (element instanceof Grass || (element instanceof Animal animal && color == animal.getColor())){
                return;
            }
        }
        clean();
        this.element = element;
        this.color = element.getColor();
        if (this.element instanceof Animal){
            Circle circle = new Circle();
            circle.setRadius(cellSize/2);
            circle.setFill(color);
            this.getChildren().add(circle);
        }

        if (this.element instanceof Grass){
            Rectangle rectangle = new Rectangle(cellSize, cellSize);
            rectangle.setFill(color);
            this.getChildren().add(rectangle);
        }

    }

    public void clean(){
        this.element = null;
        this.color = null;
        this.getChildren().clear();
    }




}
