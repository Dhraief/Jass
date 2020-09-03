package ch.epfl.menu;

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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SinglePlayer implements Runnable {
    private static BooleanProperty clicked= new SimpleBooleanProperty();
    private static StringProperty name1 = new SimpleStringProperty();
    private static StringProperty name2 = new SimpleStringProperty();
    private static StringProperty name3 = new SimpleStringProperty();
    private static StringProperty name4 = new SimpleStringProperty();
    private final Scene scene;
    private Stage stage;

       public SinglePlayer() {
           GridPane beginPane=createBeginPane();
           StackPane mainPane= new StackPane();           
           mainPane.getChildren().add(beginPane);
           scene = new Scene(mainPane);
       }
       
       private GridPane createBeginPane() {
           clicked.set(false);
           GridPane beginPane = new GridPane();
           Pane jass = new JassTitle("SELECTION DES NOMS\n\n","white");
           beginPane.add(jass, 1, 0);
           Text begin = new Text("CLIQUEZ ICI \n POUR COMMENCER \n LA PARTIE");
           begin.setFont(new Font ("TimesRoman", 15));
           
           beginPane.add(begin, 2, 5);
           begin.setOnMouseClicked((e)->{
                   clicked.set(true);
                   long[] seeds = new long[5];
                   Random seed = new Random();
                   for (int i = 0; i < seeds.length; i++) {
                       seeds[i] = seed.nextLong();
                   }
                   Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
                   players.put(PlayerId.PLAYER_1, new GraphicalPlayerAdapter());
                   players.put(PlayerId.PLAYER_2, new PacedPlayer(new MctsPlayer(PlayerId.PLAYER_2,seeds[1],10_000) , 2));
                   players.put(PlayerId.PLAYER_3, new PacedPlayer(new MctsPlayer(PlayerId.PLAYER_3,seeds[2],10_000) , 2));
                   players.put(PlayerId.PLAYER_4, new PacedPlayer(new MctsPlayer(PlayerId.PLAYER_4,seeds[3],10_000) , 2));


                   Thread gameThread = new Thread(() -> {
                       JassGame g = new JassGame(seeds[0], players, mapNames());
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
               t.setFont(new Font ("TimesRoman", 30));
               beginPane.add(t, 0, 1+2*i);
           }
           Text player1 = new Text("JOUEUR 1\t\t\n");
           player1.setFont(new Font ("TimesRoman", 20));
           TextField p1Text = new TextField();
           name1.bind(p1Text.textProperty());
           beginPane.add(new HBox(player1,p1Text), 1, 1);
           
           Text player3 = new Text("JOUEUR 3\t\t\n\n\n");
           player3.setFont(new Font ("TimesRoman", 20));
           TextField p3Text = new TextField();
           name3.bind(p3Text.textProperty());
           beginPane.add(new HBox(player3,p3Text), 1, 2);
           
           Text player2 = new Text("JOUEUR 2\t\t\n");
           player2.setFont(new Font ("TimesRoman", 20));
           TextField p2Text = new TextField();
           name2.bind(p2Text.textProperty());
           beginPane.add(new HBox(player2,p2Text), 1, 3);
           
           Text player4 = new Text("JOUEUR 4\t\t\n");
           player4.setFont(new Font ("TimesRoman", 20));
           TextField p4Text = new TextField();
           name4.bind(p4Text.textProperty());
           beginPane.add(new HBox(player4,p4Text), 1, 4);
           
           beginPane.visibleProperty().bind(clicked.not());
           beginPane.setStyle("-fx-background-color: linear-gradient(  #F5DEB3, #CD853F)");
           beginPane.setPrefSize(1250, 700);
           beginPane.setAlignment(Pos.CENTER);
           return beginPane;
       }
   
       public Stage createStage() {
           this.stage = new Stage();
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
    @Override
    public void run() {
        Platform.runLater(
                () -> {
                Stage stage= createStage();
                        stage.show(); 
                }
                );        
     
    }
       }
