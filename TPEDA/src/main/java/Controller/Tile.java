package Controller;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.Stack;

import static Controller.Controller.TILE_SIZE;


public class Tile extends Rectangle {

    private Stack<Piece> pieceHistory;
    private Piece piece;
    private boolean mark;
    private int x;
    private int y;



    // **********       INITIALIZER        ***********

    public Tile(boolean light,boolean mark, int x, int y)
    {
        this.pieceHistory = new Stack<Piece>();
        this.piece = null;
        this.x=x;
        this.y=y;
        this.mark=mark;

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(x * TILE_SIZE, y * TILE_SIZE);
        if(mark)
            setFill(Color.LIGHTCYAN);
        else setFill(light ? Color.SANDYBROWN : Color.SADDLEBROWN);
    }


    // **********       METHODS        ***********

    public boolean hasPiece() {
        return piece != null;
    }

    public boolean colorHasToChange(Color color) { return (color!=this.piece.getColour());}

    public void removePiece(){
        if(pieceHistory.isEmpty())
        {
            piece=null;
            return;
        }
        this.piece=pieceHistory.pop();

    }

    public void setPiece(Color c) {
        pieceHistory.add(piece);
        this.piece = new Piece(c,x,y);
    }


    // **********       GETTERS & SETTERS        ***********

    public Piece getPiece() {
        return piece;
    }

    public int getCol() {
        return x;
    }

    public int getRow() {
        return y;
    }

    public boolean isMark() { return mark; }
}