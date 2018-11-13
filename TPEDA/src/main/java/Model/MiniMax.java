package Model;


public class MiniMax {

    //values used for comparison
    private static int MIN = Integer.MIN_VALUE;
    private static int MAX = Integer.MAX_VALUE;


    public static Board depthMM(Board currentBoard, Player ai, boolean prune, int k, long timeMax, Dot dot) {
        Board newBoard = new Board(currentBoard.size, currentBoard.activePlayer);
        currentBoard.cloneBoard(newBoard);

        Node current = new Node(newBoard,null,  ai, true);
        int best = MIN;
        Node bestBranch = null;

        int alpha = MIN;
        int beta = MAX;

        current.markAsUsed();
        current.drawDescendants();

        if(newBoard.isComplete()) return null;
        for (Node son : current.getDescendants()) {
            int branchBest = depthMM(son, k, prune, false, ai, alpha, beta, dot);

            if (branchBest > best) {
                best = branchBest;
                bestBranch = son;
            }
            //if called from timeMM
            if(timeMax > 0 && timeMax <= System.currentTimeMillis()/1000){
                bestBranch.placeDisc();
                return bestBranch.getBoard();
            }
        }
        bestBranch.placeDisc();
        bestBranch.markAsSelected();
        current.markAsSelected();
        current.setScore(best);
        dot.drawTree(current);
        current.setScore(best);
        current.getBoard().updateDiscs();
        return bestBranch.getBoard();
    }

    // Recursive for minimax by depth
    private static int depthMM(Node node, int depth, boolean prune, boolean playsMax, Player ai, int alpha, int beta, Dot dot){
        node.placeDisc();
        node.markAsUsed();

        if (depth == 0){
            int score = node.heuristic(ai);
            node.setScore(score);
            node.removeDisc();
            return score;
        }
        node.drawDescendants();
        if(node.getDescendants().isEmpty()){
            node.setScore(node.heuristic(ai));
            node.removeDisc();
            return node.heuristic(ai);
        }
        if (playsMax) {
            Node maxNode = null;
            int max = MIN;

            node.drawDescendants();
            for (Node son : node.getDescendants()) {
                int best = depthMM(son, depth - 1, prune, !playsMax, ai, alpha, beta, dot);
                if(best > max){
                    max = best;
                    maxNode = son;
                }
                alpha = Math.max(alpha, max);
                if (prune && beta <= alpha) {
                    //el podado no lleva score
                    node.removeDisc();
                    return max;
                }
            }
            node.removeDisc();
            node.setScore(max);
            maxNode.markAsSelected();
            return max;

        }
        else {
            Node minNode = null;
            int min = MAX;

            node.drawDescendants();
            for (Node son : node.getDescendants()) {
                int worst = depthMM(son, depth - 1, prune, playsMax, ai, alpha, beta, dot);
                if(worst < min){
                    min = worst;
                    minNode = son;

                }
                beta = Math.min(beta, min);
                if (prune && beta <= alpha) {
                    node.removeDisc();
                    return min;
                }
            }
            node.removeDisc();
            node.setScore(min);
            minNode.markAsSelected();
            return min;
        }

    }

    public static Board timeMM(Board currentBoard, Player ai, boolean prune, long time, Dot dot) {
        Board best = null;
        int i = 1;
        while(System.currentTimeMillis()/1000 < time){
            best = depthMM(currentBoard, ai, prune, i, time, dot);
            i++;
        }
        return best;
    }




}
