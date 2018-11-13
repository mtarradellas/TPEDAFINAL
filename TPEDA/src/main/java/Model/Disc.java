package Model;

import java.io.Serializable;

public class Disc implements Serializable {


    protected int colour;
    protected Move move;



    // **********       INITIALIZER        ***********

    Disc(int colour, Move move) {
        this.colour = colour;
        this.move = move;
    }



    // **********       GETTERS & SETTERS        ***********

    public int getColour() {
        return colour;
    }

    public int getRow() {
        return move.getRow();
    }

    public int getCol() {
        return move.getCol();
    }




    public Disc cloneDisc() {
        return new Disc(colour, new Move(move.getRow(), move.getCol()));
    }

    protected void swap() {
        colour = Math.abs(colour-1);
    }

    @Override
    public int hashCode() {
        return move.hashCode() * 31 + colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Disc disc = (Disc) o;
        if (colour != disc.colour) return false;
        return move.equals(disc.move);
    }



}

