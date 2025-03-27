package agh.ics.oop.gui.options;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;

import java.util.function.UnaryOperator;

public class OptionsElementHBox extends HBox {

    private final TextField textField;

    public OptionsElementHBox(Label name, TextField textField, boolean isInt) {
        super(name,textField);
        name.setMinWidth(150);
        textField.setMinWidth(150);
        this.setMaxWidth(300);

        this.textField = textField;





        UnaryOperator<TextFormatter.Change> intFilter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]?")) {
                return change;
            }

            return null;
        };

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String text = change.getText();

            if (text.matches("[0-9.]?")) {

                return change;
            }


            return null;
        };




        if(isInt) textField.setTextFormatter(new TextFormatter<String>(intFilter));
        else textField.setTextFormatter(new TextFormatter<String>(doubleFilter));
    }

    public String getValue() {
        return this.textField.getText();
    }
}
