package Controller;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.Serializable;

import static Controller.Controller.TILE_SIZE;

public class Piece extends StackPane {

    private Color colour;



    // **********       INITIALIZER        ***********

    public Piece(Color colour, int row, int col) {

        this.colour=colour;

        relocate(row * TILE_SIZE, col * TILE_SIZE);
        Circle shape = new Circle(TILE_SIZE * 0.4);

        shape.setFill(colour);

        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(TILE_SIZE * 0.03);

        shape.setTranslateX(TILE_SIZE - TILE_SIZE * 0.925);
        shape.setTranslateY(TILE_SIZE - TILE_SIZE * 0.925);

        getChildren().add(shape);


    }


    // **********       GETTERS & SETTERS        ***********

    public Color getColour() {
        return colour;
    }
}