package Controller;

import Model.Board;
import Model.Disc;
import Model.Player;
import Model.Reversi;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Set;

public class Controller {
    public static final int TILE_SIZE = 50;
    private static final Color COLOUR_PLAYER_1=Color.BLACK;
    private static final Color COLOUR_PLAYER_2=Color.WHITE;

    private Reversi game;

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private Tile[][] boardImage;
    private Tile[][] boardShadow;
    private Player activePlayer;
    private Button undoButton;
    private HBox box;
    private Label label;
    private Label label2;
    private HBox box2;
    private Label labelPlayer1;
    private Label getLabelPlayer12;
    private int size;

    // **********       INITIALIZER        ***********

    public Controller(Reversi g) {
        game = g;
        size = g.getSize();
        boardShadow = new Tile[size][size];
        boardImage = new Tile[size][size];
        activePlayer = game.activePlayer;
    }




    // **********       LOAD AND CREATE CONTENT FOR GRAPHIC INTERFACE        ***********

    public Parent createContent() {
        Pane root = createRoot();
        Pane board = createBoard();
        VBox box = createButtons(root,board);
        HBox panels = createPanels();

        root.getChildren().add(panels);
        panels.relocate(TILE_SIZE,TILE_SIZE*size+TILE_SIZE*1.25);
        board.relocate(TILE_SIZE, TILE_SIZE);
        box.relocate((size+2)*TILE_SIZE,TILE_SIZE*(size/3));

        this.updateBoard(game.getCurrentBoard(),false);

        return root;
    }


    private Pane createRoot() {
        Pane root = new Pane();
        root.setPrefSize((size+4) * TILE_SIZE, (size+2) * TILE_SIZE);
        BackgroundFill background = new BackgroundFill(Paint.valueOf("white"), CornerRadii.EMPTY, Insets.EMPTY);
        root.setBackground(new Background(background));

        Pane rootBorder = new Pane();
        rootBorder.setPrefSize(size * TILE_SIZE+10, size * TILE_SIZE+10);
        rootBorder.relocate(TILE_SIZE-5, TILE_SIZE-5);
        BackgroundFill border = new BackgroundFill(Paint.valueOf("black"), CornerRadii.EMPTY, Insets.EMPTY);
        rootBorder.setBackground(new Background(border));
        root.getChildren().add(rootBorder);
        return root;
    }

    private Pane createBoard() {
        Pane board=new Pane();

        board.setPrefSize(size * TILE_SIZE, size * TILE_SIZE);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Tile tile = new Tile(((x + y) % 2 == 0),false, x, y);
                boardImage[y][x] = tile;
                tileGroup.getChildren().add(tile);
            }
        }
        return board;
    }

    private VBox createButtons(Pane root,Pane board)
    {
        VBox verticalPanel = new VBox(TILE_SIZE/3);
        root.getChildren().add(verticalPanel);

        undoButton = new Button("  Undo  ");
        undoButton.setOnMouseClicked(new UndoButtonHandler());
        undoButton.setMouseTransparent(game.getPreviousBoards().isEmpty());

        Button treeButton = new Button("Get Tree");
        treeButton.setOnMouseClicked(new TreeHandler());

        Button aiMoveButton = new Button("Move AI");
        aiMoveButton.setOnMouseClicked(new MoveAiHandler());

        Button save = new Button("  Save  ");
        save.setOnMouseClicked(new SaveHandler());

        verticalPanel.getChildren().addAll(undoButton,treeButton,aiMoveButton ,save);

        root.getChildren().add(board);
        board.getChildren().addAll(tileGroup, pieceGroup);

        return verticalPanel;
    }

    private HBox createPanels()
    {
        HBox panels = new HBox(TILE_SIZE);

        this.box = new HBox(3);
        Circle white = new Circle(10);
        label = new Label(Integer.toString(game.getCurrentBoard().getDiscs(game.getPlayer2())));

        white.setFill(COLOUR_PLAYER_2);

        white.setStroke(Color.BLACK);
        white.setStrokeWidth(TILE_SIZE * 0.03);

        this.box2 = new HBox(3);
        Circle black = new Circle(10);
        labelPlayer1 = new Label(Integer.toString(game.getCurrentBoard().getDiscs(game.getPlayer1())));

        black.setFill(COLOUR_PLAYER_1);

        white.setStroke(Color.BLACK);
        white.setStrokeWidth(TILE_SIZE * 0.03);

        this.box.getChildren().addAll(white,label);
        this.box2.getChildren().addAll(black,labelPlayer1);

        panels.getChildren().addAll(box2,box);

        return panels;
    }





    // **********       UPDATE GRAPHIC INTERFACE         ***********

    private void updateBoard(Board currentBoard,boolean isUndo) {
        box.getChildren().removeAll(label,label2);
        label2 = new Label(Integer.toString(game.getCurrentBoard().getDiscs(game.getPlayer2())));
        box.getChildren().add(label2);

        box2.getChildren().removeAll(labelPlayer1,getLabelPlayer12);
        getLabelPlayer12 = new Label(Integer.toString(game.getCurrentBoard().getDiscs(game.getPlayer1())));
        box2.getChildren().add(getLabelPlayer12);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Tile tile = boardImage[y][x];
                Disc currentDisc = currentBoard.matrix[y][x];
                Color color;

                if(tile.isMark()) {
                    tileGroup.getChildren().remove(tile);
                    boardImage[y][x]=boardShadow[y][x];
                }
                Set<Disc> movablesPositions =game.getCurrentBoard().getAvailable(game.activePlayer);

                for (Disc disc : movablesPositions) {
                    if (disc.getRow() == y && disc.getCol() == x) {
                        Tile markedTile = new Tile(((x + y) % 2 == 0), true, x, y);

                        boardShadow[y][x] = tile;
                        boardImage[y][x] = markedTile;

                        TileHandler tileHandler = new TileHandler(this, markedTile, disc);
                        markedTile.setOnMouseClicked(tileHandler);
                        tileGroup.getChildren().add(markedTile);
                        markedTile.setMouseTransparent(false);
                    }
                }
                Tile currentTile;
                if (boardImage[y][x].isMark()) {
                    currentTile=boardShadow[y][x];
                }
                else  currentTile = boardImage[y][x];
                if (currentDisc!=null && !isUndo) {
                    if (currentDisc.getColour() == 0)
                        color = COLOUR_PLAYER_1;
                    else
                        color = COLOUR_PLAYER_2;
                    if ((!currentTile.hasPiece() || (currentTile.hasPiece() && currentTile.colorHasToChange(color)))) {
                        currentTile.setPiece(color);
                        pieceGroup.getChildren().add(currentTile.getPiece());
                    }
                }
                else if(currentTile.hasPiece() ) {
                    if( currentDisc==null) {
                        while (currentTile.hasPiece()){
                            pieceGroup.getChildren().remove(currentTile.getPiece());
                            currentTile.removePiece();
                        }
                    }
                    else{
                        if (currentDisc.getColour() == 0)
                            color = COLOUR_PLAYER_1;
                        else
                            color = COLOUR_PLAYER_2;
                        if (currentTile.colorHasToChange(color)) {
                            pieceGroup.getChildren().remove(currentTile.getPiece());
                            currentTile.removePiece();
                        }
                    }
                }
            }
        }
        if(game.getCurrentBoard().isComplete()) {
            PrintIfWinAlert(game.getCurrentBoard(), game.getPlayer1(), game.getPlayer2());
            return;
        }
        if(game.getCurrentBoard().getAvailable(game.activePlayer).isEmpty()){
            passAlert();
            game.passTurn();
            updateBoard(game.getCurrentBoard(), false);
        }
        undoButton.setMouseTransparent(game.getPreviousBoards().isEmpty());
    }




    // **********       BOARD HANDLER         ***********

    private class TileHandler implements EventHandler<MouseEvent>
    {
        private Tile currentTile;
        private Disc disc;
        private Controller controller;

        TileHandler(Controller control,Tile tile,Disc disc)
        {
            this.controller=control;
            this.currentTile=tile;
            this.disc=disc;
        }


        @Override
        public void handle(MouseEvent click)
        {
            if(game.activePlayer.isHuman()) {
                game.putDisc(disc);
                controller.updateBoard(game.getCurrentBoard(), false);
                undoButton.setMouseTransparent(game.getPreviousBoards().isEmpty());
            }

        }
    }




    // **********       BUTTONS         ***********

    // Undo button
    private class UndoButtonHandler implements EventHandler<MouseEvent>
    {
        @Override
        public void handle(MouseEvent click) {
            game.undo();
            if(game.getCurrentBoard()!=null) {
                updateBoard(game.getCurrentBoard(),true);
                undoButton.setMouseTransparent(game.getPreviousBoards().isEmpty());
            }
        }
    }

    //Move AI Button (reacts only if it is AI's turn)
    private class MoveAiHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent click) {
            if(game.getCurrentBoard().isComplete()) {
                PrintIfWinAlert(game.getCurrentBoard(), game.getPlayer1(), game.getPlayer2());
                return;

            }
            if (!game.activePlayer.isHuman()){
                boolean moveAi = game.moveAI();
                if(! moveAi){
                    errorAlert("AI stopped working.\n Please try again.");
                    return;
                }
                updateBoard(game.getCurrentBoard(), false);
            }
        }
    }

    // Get Tree Button (for the .dot file)
    private class TreeHandler implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent click){
            if((game.getMode() == 1 || game.getMode() == 2) && game.dotTree()){
                try {
                    File file = new File("dotFile.dot");
                    FileWriter fileWriter = new FileWriter(file);
                    String str = game.getAITree();
                    fileWriter.write(str);
                    fileWriter.close();
                    infoAlert("File was created successfully");
                } catch (Exception e) {
                    errorAlert("There was a problem with the file.\nIt could not be created.");
                }
            }
            else
                infoAlert("The file cannot be created because there was no decision made by AI.\nYou may be on mode 0 'Human vs Human' or AI may have not moved yet");
        }
    }




    // **********       SAVE FILE         ***********

    private class SaveHandler implements EventHandler<MouseEvent>
    {
        @Override
        public void handle(MouseEvent click)
        {
            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();
            try{
                File toSaveFile = fileChooser.showSaveDialog(stage);
                saveFile(toSaveFile);
            }catch (Exception ex){
                errorAlert("There was a problem with the file.\nIt could not be saved.");
            }
        }
    }
    //Saves application to file selected
    private void saveFile(File file) {
        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(game);
            o.close();
            f.close();

        }catch (Exception e) {
            errorAlert("There was a problem with the file.\nIt could not be saved.");
        }
    }



    // **********       ALERTS         ***********

    private void passAlert(){
        String msg;
        if(game.activePlayer.getColour() == 0) msg = "Player 1 ran out of moves.\nIt is now Player 2's turn.";
        else msg = "Player 2 ran out of moves.\nIt is now Player 1's turn.";
        Alert info = new Alert(Alert.AlertType.INFORMATION, msg);
        info.setHeaderText("Pass Turn");
        info.show();
    }

    private void infoAlert(String msg){
        Alert info = new Alert(Alert.AlertType.INFORMATION, msg);
        info.setHeaderText("Information");
        info.show();
    }

    private void errorAlert(String msg){
        Alert error = new Alert(Alert.AlertType.ERROR, msg);
        error.setHeaderText("Error");
        error.show();
    }

    private static void PrintIfWinAlert(Board currentBoard,Player player1,Player player2) {
        if (currentBoard.getDiscs(player1) > currentBoard.getDiscs(player2)) {
            Alert win1 = new Alert(Alert.AlertType.INFORMATION, "White =  " + currentBoard.getDiscs(player2) + "   Black = " + currentBoard.getDiscs(player1));
            win1.setHeaderText("Player 1 wins");
            win1.show();
        }
        else if(currentBoard.getDiscs(player1) < currentBoard.getDiscs(player2)){
            Alert win2 = new Alert(Alert.AlertType.INFORMATION, "White = " + currentBoard.getDiscs(player2) + "   Black = " + currentBoard.getDiscs(player1));
            win2.setHeaderText("Player 2 wins");
            win2.show();
        }
        else{
            Alert win3 = new Alert(Alert.AlertType.INFORMATION, "White = " + currentBoard.getDiscs(player2) + "   Black = " + currentBoard.getDiscs(player1));
            win3.setHeaderText("Tie");
            win3.show();
        }

    }


}
