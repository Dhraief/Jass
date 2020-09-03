package ch.epfl.menu;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MultiPlayer implements Runnable {
    private static BooleanProperty clicked= new SimpleBooleanProperty();

    public static StringProperty name1 = new SimpleStringProperty();
    public static StringProperty name2 = new SimpleStringProperty();
    public static StringProperty name3 = new SimpleStringProperty();
    public static StringProperty name4 = new SimpleStringProperty();
    
    public static StringProperty Ip1 = new SimpleStringProperty();
    public static StringProperty Ip2 = new SimpleStringProperty();
    public static StringProperty Ip3 = new SimpleStringProperty();
    public static StringProperty Ip4 = new SimpleStringProperty();
    
    private final Scene scene;
    private Stage stage;
    


       public MultiPlayer() {
           GridPane beginPane=createBeginPane() ;
           StackPane mainPane= new StackPane();           
           mainPane.getChildren().add(beginPane);
           scene = new Scene(mainPane);
       }
       
       private GridPane createBeginPane() {
           clicked.set(false);
           GridPane beginPane = new GridPane();
           Pane jass = new JassTitle("BIENVENUE AU JEU DE JASS\n\n","white");
           
           beginPane.add(jass, 4, 0);
            Text begin = new Text("Veuillez entrer dans la première case \n le nom (optionnel) et l'adresse IP si c'est un \n" + 
                   "                   \"joueur distant\" \n "
                   + "COMMENCER");
           beginPane.add(begin, 2, 3);
           
           begin.setOnMouseClicked((e)->{
               clicked.set(true);
               long[] seeds = new long[5];
               Random seed = new Random();
               for (int i = 0; i < seeds.length; i++) {
                   seeds[i] = seed.nextLong();
               }

               Thread gameThread = new Thread(() -> {
                   JassGame g = new JassGame(seeds[0],mapPlayers(), mapNames());
                   while (!g.isGameOver()) {
                       g.advanceToEndOfNextTrick();
                       try {
                           Thread.sleep(1000);
                       } catch (Exception ee) {
                       }
                   }
               });
               
               stage.close();
               gameThread.setDaemon(true);
               gameThread.start();
       });
           for(int i=0 ; i<TeamId.COUNT ; i++) {
               Text t = new Text("\t\tEQUIPE "+(i+1)+"\t\t\t");
               beginPane.add(t, 0, 1+2*i);
           }
           Text player1 = new Text("JOUEUR 1\t\t\n");
           TextField p1Text = new TextField();
           name1.bind(p1Text.textProperty());
           TextField ip1Text = new TextField();
           Ip1.bind(ip1Text.textProperty());
           beginPane.add(new HBox(player1,p1Text,ip1Text), 1, 1);
           
           Text player3 = new Text("JOUEUR 3\t\t\n\n\n");
           TextField p3Text = new TextField();
           name3.bind(p3Text.textProperty());
           TextField ip3Text = new TextField();
           Ip3.bind(ip3Text.textProperty());
           
           beginPane.add(new HBox(player3,p3Text,ip3Text), 1, 2);
           
           Text player2 = new Text("JOUEUR 2\t\t\n");
           TextField p2Text = new TextField();

           TextField ip2Text = new TextField();
           Ip2.bind(ip2Text.textProperty());

           name2.bind(p2Text.textProperty());
           beginPane.add(new HBox(player2,p2Text,ip2Text), 1, 3);
           
           Text player4 = new Text("JOUEUR 4\t\t\n");
           TextField p4Text = new TextField();
           name4.bind(p4Text.textProperty());
           TextField ip4Text = new TextField();
           Ip4.bind(ip4Text.textProperty());
           beginPane.add(new HBox(player4,p4Text,ip4Text), 1, 4);
           
           
           beginPane.visibleProperty().bind(clicked.not());
           beginPane.setStyle("-fx-background-color: linear-gradient(#E4EAA2, #9CD672)");
           beginPane.setAlignment(Pos.CENTER);
           return beginPane;
       }
   
       public Stage createStage() {
           Stage stage = new Stage();
           stage.setScene(scene);
           stage.setTitle("Javass");
           stage.setMaxHeight(900);
           return stage;
       }
       public Map<PlayerId , String> mapNames() {
           Map<PlayerId , String> map = new HashMap<>();
           String nameP1 = name1.get().equals(null) || name1.get().equals("") ? "Aline": name1.get();
           String nameP2 = name2.get().equals(null) || name2.get().equals("")? "Bastien": name2.get();
           String nameP3 = name3.get().equals(null) || name3.get().equals("")? "Colette": name3.get();
           String nameP4 = name4.get().equals(null) || name4.get().equals("")? "David": name4.get();
           map.put(PlayerId.PLAYER_1, nameP1);
           map.put(PlayerId.PLAYER_2, nameP2);
           map.put(PlayerId.PLAYER_3, nameP3);
           map.put(PlayerId.PLAYER_4, nameP4);
           return map;
       }

    public Map<PlayerId, Player> mapPlayers() {
        Random seed = new Random();
        long[] seeds = new long[5];
        for (int i = 0; i < seeds.length; i++) {
            seeds[i] = seed.nextLong();
        }
        Map<PlayerId, Player> map = new HashMap<>();
        map.put(PlayerId.PLAYER_1, new GraphicalPlayerAdapter());
        
        Player nameP2 = null;
        try {
            nameP2 = name2.get().equals(null) || name2.get().equals("")
                    ? new PacedPlayer(
                            new MctsPlayer(PlayerId.values()[1], seeds[1], 10_000),
                            2)
                    : new RemotePlayerClient(name2.get());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Player nameP3 = null;
        try {
            nameP3 = name3.get().equals(null) || name3.get().equals("")
                    ? new PacedPlayer(
                            new MctsPlayer(PlayerId.values()[2], seeds[2], 10_000),
                            2)
                    : new RemotePlayerClient(name3.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Player nameP4 = null;
        try {
            nameP4 = name3.get().equals(null) || name3.get().equals("")
                    ? new PacedPlayer(
                            new MctsPlayer(PlayerId.values()[3], seeds[3], 10_000),
                            2)
                    : new RemotePlayerClient(name4.get());
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put(PlayerId.PLAYER_2, nameP2);
        map.put(PlayerId.PLAYER_3, nameP3);
        map.put(PlayerId.PLAYER_4, nameP4);
        return map;
    }
    @Override
    public void run() {
        Platform.runLater(
                () -> {
                this.stage= createStage();
                        stage.show(); 
                }
                );        
       
    }
       }