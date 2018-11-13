package Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Node implements Serializable {


    private static int ID = 0;

    private Board board;
    private List<Disc> flipped;

    private List<Node> descendants;
    Player player;

    private Disc discPlaced;

    private boolean ai; // used to obtain the shape of the node for the .dot File
    private int id; // unique identifier for the node (used in the creation of the .dot File)
    private boolean pruned;// values in true will be the ones not considered (if a node is visited and used, then minimax will mark it as false)(used in the creation of the .dot File)
    private int score; // heuristic (used in the creation of the .dot File)
    private boolean selected; //the node was selected by the minimax algorithm (used in the creation of the .dot File)




    // **********       INITIALIZER        ***********

    public Node(Board board, Disc discPlaced, Player player, boolean ai) {
        this.board = board;
        this.player = player;
        this.descendants = new ArrayList<>();

        ID++;
        this.id = ID;
        this.discPlaced = discPlaced;
        this.selected = false;
        this.ai = ai;
        this.pruned = true;
        this.score = 0;
        this.descendants = new ArrayList<>();
        this.flipped = new ArrayList<>();
    }





    // **********       METHODS        ***********

    void placeDisc() {
        if (discPlaced==null) return;
        board.matrix[discPlaced.getRow()] [discPlaced.getCol()] = discPlaced;
        if (discPlaced.colour == 0) {
            board.discsP1++;
        }else   board.discsP2++;
        flipDirections(discPlaced);
    }

    void removeDisc() {
        if (discPlaced==null) return;
        for (Disc disc : flipped) {
            disc.swap();
            board.countDiscs(disc);
        }
        board.matrix[discPlaced.getRow()] [discPlaced.getCol()] = null;
        if (discPlaced.colour == 0) {
            board.discsP1--;
        }else {
            board.discsP2--;
        }
    }

    // Used for placeDisc
    private void flipDirections(Disc disc) {
        for (int i = disc.getRow() - 1; i < disc.getRow() + 2; i++) {
            for (int j = disc.getCol() - 1; j < disc.getCol() + 2; j++) {
                if (i >= 0 && j >= 0 && i < board.size && j < board.size && ( i!=disc.getRow() || j!=disc.getCol() )) {
                    search(disc, i - disc.getRow(), j - disc.getCol());
                }
            }
        }
    }

    // Used for placeDisc
    private void search(Disc disc, int x, int y) {
        int i = disc.getRow() + x;
        int j = disc.getCol() + y;

        boolean foundEnemy = false;
        for ( ; i < board.size && j < board.size && i >= 0 && j >= 0; i += x, j += y){
            if (board.matrix[i][j] == null){
                return;
            }
            else if (board.matrix[i][j].colour == disc.colour && !foundEnemy){
                return;
            }
            else if (board.matrix[i][j].colour == disc.colour && foundEnemy) {
                flip(disc, board.matrix[i][j]);
                return;
            }
            else {
                foundEnemy = true;
            }
        }
    }

    // Used for placeDisc
    private void flip(Disc from, Disc to) {
        int x = Integer.compare(to.getRow(), from.getRow());
        int y = Integer.compare(to.getCol(), from.getCol());
        int i = from.getRow() + x;
        int j = from.getCol() + y;
        while(i != to.getRow() || j != to.getCol()) {
            Disc disc = board.matrix[i][j];
            disc.swap();
            flipped.add(disc);
            i += x;
            j += y;
            board.countDiscs(from);
        }
    }

    // this method is not called from the constructor in order to avoid creating the descendants for the nodes that represent leafs in the minimax
    public void drawDescendants(){
        if (!board.isComplete()) {
            Set<Disc> available = board.getAvailable(player);
            if (available.isEmpty()) {
                descendants.add(new Node(board, null, player.getEnemy(),!ai));
            } else {
                for (Disc d : available) {
                    Node n = new Node(board, d, player.getEnemy(), !ai);
                    descendants.add(n);
                }
            }
        }
    }





    // **********       HEURISTICS        ***********

    public int heuristic(Player max) {
        return (discParity(max) + cornersConquered(max) + xConquered(max));
    }


    //  Used in heuristics
    private int discParity(Player max){
        int maxDiscs = 0;
        int minDiscs = 0;
        if (max.getColour() == 0) {
            maxDiscs = board.discsP1;
            minDiscs = board.discsP2;
        }
        else {
            maxDiscs = board.discsP2;
            minDiscs = board.discsP1;
        }
        return  100 * (maxDiscs - minDiscs)/(maxDiscs + minDiscs);

    }

    //  Used in heuristics
    private int cornersConquered(Player max){
        int maxCorners = board.cornersConquered(max);
        int minCorners = board.cornersConquered(max.getEnemy());
        if ( maxCorners + minCorners != 0)
            return (100 * (maxCorners - minCorners) / (maxCorners + minCorners));
        else
            return 0;
    }

    //  Used in heuristics
    //(x are critical spaces which would give the opponent access to the corners -> they have negative value)
    private int xConquered(Player max){
        int maxX = board.xConquered(max);
        int minX = board.xConquered(max.getEnemy());
        if ( maxX + minX != 0)
            return (-20 * (maxX - minX) / (maxX + minX));
        else
            return 0;
    }




    // **********       GETTERS & SETTERS        ***********

    public int getId(){
        return id;
    }

    public int getScore(){
        return score;
    }

    public boolean isSelected(){
        return selected;
    }

    public Disc getDiscPlaced(){
        return discPlaced;
    }

    public boolean isPruned(){
        return pruned;
    }

    public List<Node> getDescendants() {
        return descendants;
    }

    public Board getBoard() {
        return board;
    }

    public void markAsUsed() {
        this.pruned = false;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getShape() {
        return ai ? "rectangle" : "ellipse";
    }

    public void markAsSelected(){
        this.selected = true;
    }



    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass())
            return false;
        Node n = (Node) o;

        if(! board.equals(n.board))return false;
        if(! player.equals(n.player)) return false;
        return pruned == n.pruned;
    }
}
