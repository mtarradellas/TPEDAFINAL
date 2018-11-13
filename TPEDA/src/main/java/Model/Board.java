package Model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Board implements Serializable {

    public Model.Disc[][] matrix;
    int size;
    Player activePlayer;
    Integer discsP1; //Colour 0
    Integer discsP2; //Colour 1




    // **********       INITIALIZERS        ***********

    // Initial constructor : assigns the first two discs for each player in the middle of the board
    Board(int size, Player activePlayer) {
        this.size = size;
        matrix = new Disc[size][size];
        matrix[(size/2)-1][(size/2)-1]=new Disc(0,new Move((size/2)-1,(size/2)-1));
        matrix[(size/2)][(size/2)]=new Disc(0,new Move((size/2),(size/2)));
        matrix[(size/2)][(size/2)-1]=new Disc(1, new Move((size/2),(size/2)-1));
        matrix[(size/2)-1][(size/2)]=new Disc(1,new Move((size/2)-1,(size/2)));
        discsP2 = 2;
        discsP1 = 2;
        this.activePlayer = activePlayer;

    }

    // Secondary "constructor" : creates a board parting from the previous board and the disc that differentiates them
    protected Board putDisc(Disc disc) {
        Board newBoard = new Board(size, activePlayer.getEnemy());
        cloneBoard(newBoard);
        newBoard.matrix[disc.getRow()][ disc.getCol()] = disc;
        if (disc.colour == 0) {
            newBoard.discsP1++;
        }
        else newBoard.discsP2++;
        newBoard.flipDirections(disc);
        return newBoard;
    }

    // Used in the secondary constructor
    void cloneBoard(Board newBoard) {
        newBoard.matrix = new Disc[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                Disc d = matrix[i][j];
                if(d != null)
                    newBoard.matrix[i][j]= d.cloneDisc();

            }

        }
        int dp1 = discsP1;
        int dp2 = discsP2;
        newBoard.discsP1 = dp1;
        newBoard.discsP2 = dp2;
        newBoard.size = size;
    }

    // Used in the secondary constructor
    private void flipDirections(Disc disc) {
        for (int i = disc.getRow() - 1; i < disc.getRow() + 2; i++) {
            for (int j = disc.getCol() - 1; j < disc.getCol() + 2; j++) {
                if (i >= 0 && j >= 0 && i < size && j < size && ( i!=disc.getRow() || j!=disc.getCol() )) {
                    search(disc, i - disc.getRow(), j - disc.getCol());
                }
            }
        }
    }

    // Used in the secondary constructor
    private void search(Disc disc, int x, int y) {
        int i = disc.getRow() + x;
        int j = disc.getCol() + y;

        boolean foundEnemy = false;
        for ( ; i < size && j < size && i >= 0 && j >= 0; i += x, j += y){
            if (matrix[i][j] == null){
                return;
            }
            else if (matrix[i][j].colour == disc.colour && !foundEnemy){
                return;
            }
            else if (matrix[i][j].colour == disc.colour && foundEnemy) {
                flip(disc, matrix[i][j]);
                return;
            }
            else {
                foundEnemy = true;
            }
        }
    }

    // Used in the secondary constructor
    private void flip(Disc from, Disc to) {
        int x = Integer.compare(to.getRow(), from.getRow());
        int y = Integer.compare(to.getCol(), from.getCol());
        int i = from.getRow() + x;
        int j = from.getCol() + y;
        while(i != to.getRow() || j != to.getCol()) {
            matrix[i][j].swap();
            i += x;
            j += y;
            countDiscs(from);
        }
    }




    // **********       METHODS        ***********

    void countDiscs(Disc disc) {
        if(disc.colour == 0) {
            discsP1++;
            discsP2--;
        }
        else {
            discsP2++;
            discsP1--;
        }
    }

    // Used in heuristics for minimax
    public int cornersConquered(Player player){
        int sum = 0;
        int colour = player.getColour();
        for (int i = 0; i < size; i+=size-1) {
            for (int j = 0; j<size; j+=size-1) {
                if (matrix[i][j]!=null) {
                    if (matrix[i][j].colour == colour) sum++;
                }
            }
        }
        return sum;
    }

    // Used in heuristics for minimax ( x are critical spaces which would give the opponent access to the corners)
    public int xConquered(Player player){
        int sum = 0;
        int colour = player.getColour();
        for (int i = 1; i < size; i+=size-2) {
            for (int j = 1; j<size; j+=size-2) {
                if (matrix[i][j]!=null) {
                    if (matrix[i][j].colour == colour) sum++;
                }
            }
        }
        return sum;
    }

    // Checks if the disc is a valid move in the board
    public boolean validPosition(Disc disc){
        for (int i = disc.getRow() - 1; i < disc.getRow() + 2; i++) {
            for (int j = disc.getCol() - 1; j < disc.getCol() + 2; j++) {
                if (i >= 0 && j >= 0 && i < size && j < size && !(i == disc.getRow() && j == disc.getCol())) {
                    if (checkPos(disc, i - disc.getRow(), j - disc.getCol())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Used in validPosition
    private boolean checkPos(Disc disc, int x, int y) {
        int i = disc.getRow() + x;
        int j = disc.getCol() + y;
        boolean foundEnemy = false;

        for ( ; i < size && j < size && i >= 0 && j >= 0; i += x, j += y) {
            if (matrix[i][j] == null) {
                return false;
            } else if (matrix[i][j].colour == disc.colour && !foundEnemy) {
                return false;
            } else if (matrix[i][j].colour == disc.colour && foundEnemy) {
                return true;
            } else {
                foundEnemy = true;
            }
        }
        return false;
    }

    // Returns true if neither of the players have moves left
    public boolean isComplete(){
        if(getAvailable(activePlayer).isEmpty() && getAvailable(activePlayer.getEnemy()).isEmpty())
            return true;
        return false;
    }

    public void updateDiscs() {
        int dP1 = 0;
        int dP2 = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] != null) {
                    if (matrix[i][j].colour == 0) {
                        dP1++;
                    }else dP2++;
                }
            }
        }
        discsP1 = dP1;
        discsP2 = dP2;
    }

    //given a player, returns its score in the board
    public int getDiscs(Player player){
        if(player.getColour()==0)
            return discsP1;
        return discsP2;
    }

    //given a player, returns its available moves in the board
    public Set<Disc> getAvailable(Player player){
        Set<Disc> ret = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(matrix[i][j] == null){
                    Disc d = new Disc(player.getColour(), new Move(i, j));
                    if(validPosition(d))
                        ret.add(d);
                }
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass())
            return false;
        Board b = (Board) o;
        if( matrix != b.matrix) return false;
        if(! activePlayer.equals(b.activePlayer)) return false;
        return size == b.size;
    }

    public void printBoard(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j] == null) {
                    System.out.print("- ");
                }
                else {
                    if (matrix[i][j].getColour() == 0) {
                        System.out.print("O ");
                    }
                    else {System.out.print("X ");}
                }
            }

            System.out.println();
        }
        System.out.println();
    }


}
