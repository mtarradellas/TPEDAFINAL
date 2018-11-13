package View;

import Controller.Controller;
import Model.Reversi;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static javafx.application.Platform.exit;

public class App extends Application {

    // ArrayList with the contents obtained from console
    private ArrayList parameters;



    // **********       INITIALIZERS        ***********

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        // ArrayList with the contents obtained from console
        parameters = getArguments(getParameters());
        Reversi game;

        if (parameters == null) {
            exit();
            throw new IllegalStateException();
        }
        if (parameters.size() == 6) {
            game = loadFile((String) parameters.get(5));

            if ((int) parameters.get(0) != game.getSize())
                throw new InvalidParameterException("Invalid size");

            game.resetParameters((int) parameters.get(1), (int) parameters.get(3), (boolean) parameters.get(4), (boolean) parameters.get(2));

            if (parameters.get(0) == null) {
                game = loadFile((String) parameters.get(5));
                game.resetParameters((int) parameters.get(1), (int) parameters.get(3), (boolean) parameters.get(4), (boolean) parameters.get(2));
            }
        }
        else game = new Reversi((int) parameters.get(0), (int) parameters.get(1), (int) parameters.get(3), (boolean) parameters.get(4), (boolean) parameters.get(2));

        Controller gameController = new Controller(game);
        primaryStage.setScene(new Scene(gameController.createContent()));
        primaryStage.setTitle("REVERSI");
        primaryStage.show();
    }




    // **********       LOAD FILE        ***********

    private Reversi loadFile(String file){
        Reversi r;
        try {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);
            r = (Reversi) o.readObject();
            f.close();
            o.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidParameterException("Invalid file");
        }
        return r;
    }



    // **********       TRANSFORM ARGUMENTS TO PARAMETERS        ***********

    private ArrayList getArguments(Parameters parameters) {
        ArrayList<Object> ret = new ArrayList<>(6);

        //ret.add(0, 0);

        String arg, value;
        List<String> argList = parameters.getRaw();
        int num;
        for(int i = 0; i < argList.size(); i++){
            arg = argList.get(i);
            if(i < argList.size() - 1){
                value = argList.get(++i);
            }else{
                System.out.println("Missing parameters");
                return null;
            }
                switch(arg){
                    case"-size":
                        num = Integer.parseInt(value);
                        if(num != 4 && num != 6 && num != 8 && num != 10) throw new IllegalArgumentException("Size parameter must be '4', '6', '8' or '10'");
                        ret.add(0, num);
                        break;
                    case "-ai":
                        num = Integer.parseInt(value);
                        if (num != 0 && num != 1 && num != 2) throw new IllegalArgumentException("Ai parameter must be '0', '1' or '2'");
                        ret.add(1, num);
                        break;
                    case "-mode":
                        switch (value) {
                            case "time":
                                ret.add(2, false);
                                break;
                            case "depth":
                                ret.add(2, true);
                                break;
                            default:
                                throw new IllegalArgumentException("Mode parameter must be 'time' or 'depth'.");
                        }
                        break;
                    case "-param":
                        ret.add(3, Integer.parseInt(value));
                        break;
                    case "-prune":
                        switch (value) {
                            case "on":
                                ret.add(4, true);
                                break;
                            case "off":
                                ret.add(4, false);
                                break;
                            default:
                                throw new IllegalArgumentException("Prune parameter must 'on' or 'off'");
                        }
                        break;
                    case "-load":
                        ret.add(5, value);
                        break;
                    default:
                        System.out.println("Illegal Argument: \""+arg+"\".");
                        return null;
                }

        }
        if (ret.size() < 5) {
            System.out.println("Missing Parameters");
            return null;
        }
        return ret;
    }
}

