package Model;

import java.io.Serializable;

public class Player implements Serializable {

    private int colour;
    boolean human;
    private Player enemy;
    private int discs;


    // **********       INITIALIZER        ***********

    Player(int colour, boolean human){

        this.colour = colour;
        this.human = human;
        this.enemy = null;
    }



    // **********       GETTERS & SETTERS        ***********

    void setEnemy(Player enemy){
        this.enemy = enemy;
    }

    void setDiscs(int discs){
         this.discs = discs;
    }

    int getDiscs(){
        return discs;
    }

    public Player getEnemy(){
         return enemy;
    }

    public int getColour(){
         return colour;
    }

    public int getEnemyColour(){
        return enemy.getColour();
    }



    public Disc getDisc(Move move) {
        return new Disc(colour, move);
    }

    public boolean isHuman(){
        return human;
    }

    @Override
     public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass())
            return false;
        Player p =(Player) o;
        if(colour != p.colour) return false;
        return human == p.human;
    }

}
