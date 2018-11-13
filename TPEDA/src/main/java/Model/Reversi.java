package Model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;

public class Reversi implements Serializable {

    private int size;
    private int k;
    private boolean prune;
    private int ai;
    private boolean depth;

    private Board board;
    private Player player1;
    private Player player2;
    private Stack<Board> previousBoards = new Stack<>();
    private Dot dot;
    private boolean dotTree;

    public Player activePlayer;



    // **********       INITIALIZER        ***********

    /*
        size:       size of board
        ai:         mode 0 -> two humans, 1 -> human vs ai(moves first), 2 -> human(moves first) vs ai
        prune:      activates or deactivates alpha/beta prune
        depth:      if true will run minimax by depth, if not will run minimax by time.
        k:          value that indicates for how long or how far the minimax algorithm runs
    */
    public Reversi(int size, int ai, int k, boolean prune, boolean depth){
        this.size = size;
        this.k = k;
        this.prune = prune;
        this.ai = ai;
        this.depth = depth;

        if(ai == 0){ // 2 human players
            player1=new Player(0, true);
            player2=new Player(1, true);
        }
        else if(ai == 1){ //ai moves first
            player1=new Player(0, false);
            player2=new Player(1, true);
        }
        else{ // ai moves second
            player1=new Player(0, true);
            player2=new Player(1, false);
        }
        player1.setEnemy(player2);
        player2.setEnemy(player1);
        activePlayer = player1;
        this.dot = new Dot();
        this.dotTree = false;

        board = new Board(size, activePlayer);
    }



    // **********       GETTERS & SETTERS        ***********

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Stack<Board> getPreviousBoards() {
        return previousBoards;
    }

    public Board getCurrentBoard(){ return board; }

    public int getSize() { return size; }




    // **********       METHODS FOR FRONT       ***********

    // Inserts a disc from the active player in the current board, in the specified coordinates
    public void putDisc(Disc disc) {
        if(disc == null){
            System.out.println("null disc");
        }
        previousBoards.push(board);
        board = board.putDisc(disc);
        changePlayer();
        updateScores();
    }

    // Returns all status to the previous move
    public Board undo() {
        if (previousBoards.isEmpty()) return null;
        board = previousBoards.pop();
        changePlayer();
        updateScores();
        return board;
    }

    // Switches the active player
    private void changePlayer() {
        if(activePlayer == player1) activePlayer = player2;
        else activePlayer = player1;
    }

    // Obtains the best board according to minimax for an ai player and uses as its move
    public boolean moveAI(){
        dot.resetString();
        Board newBoard;
        if(depth)
            newBoard = MiniMax.depthMM(board, activePlayer, prune, k-1, -1, dot);
        else
            newBoard = MiniMax.timeMM(board, activePlayer, prune, System.currentTimeMillis()/1000 + k, dot);
        if(newBoard == null) {
            return false;
        }
        dotTree = true;
        previousBoards.push(board);
        board = newBoard;
        changePlayer();
        updateScores();
        return true;
    }

    // Used in frontend for when a player runs out of moves but the board is not completed
    public void passTurn(){
        changePlayer();
        board.activePlayer = board.activePlayer.getEnemy();
    }

    // Used when the game is initialized with a load from a previous game but with a change in parameters
    public void resetParameters(int ai, int k, boolean prune, boolean depth) {
        this.k = k;
        this.prune = prune;
        this.ai = ai;
        if(ai == 0){ // 2 human players
            player1.human = true;
            player2.human = true;
        }
        else if(ai == 1){ //ai moves first
            player1.human = false;
            player2.human = true;
        }
        else{ // ai moves second
            player1.human = true;
            player2.human = false;
        }
        this.depth = depth;
    }


    // Players scores depend on the current board, this method gets the number of discs of each player from the board and asigns them to the players.
    private void updateScores(){
        player1.setDiscs(board.getDiscs(player1));
        player2.setDiscs(board.getDiscs(player2));
    }



    // **********       GETTERS & SETTERS        ***********

    public String getAITree(){
        return dot.getTree();
    }

    public int getMode(){
        return ai;
    }

    public boolean dotTree(){
        return dotTree;
    }

}
