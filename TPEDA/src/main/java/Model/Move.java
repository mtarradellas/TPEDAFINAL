package Model;

import java.io.Serializable;

public class Move implements Serializable {

    private int row;
    private int col;


    // **********       INITIALIZER        ***********

    public Move(int row, int col){
        this.row = row;
        this.col = col;
    }


    // **********       GETTERS & SETTERS        ***********

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }




    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass())
            return false;
        Move move = (Move) o;

        if(row != move.row) return false;
        return (col == move.col);

    }

    @Override
    public int hashCode(){
        return row * 31 + col;
    }

    @Override
    public String toString(){
        String ret = "(" + row + " , " + col + ") " ;
        return ret;
    }
}
