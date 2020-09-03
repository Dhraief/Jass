package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * GraphicalPlayer : represente l'interface graphique d'un joueur humain
 * 
 * @author Mohamed Ali Dhraief (283509)
 * @author Amine Atallah (284592)
 *
 */
public final class GraphicalPlayer {
    private final Map<PlayerId, String> playerNames;
    private final PlayerId player;
    private final Scene scene;
    private final ObservableMap<Card, Image> observableMap = getMapCard();
    private final ObservableMap<Color, Image> observableMapTrump = getMapTrump();
    private final int WIDTH_HAND = 80;
    private final int HEIGHT_HAND = 120;
    private final int TRUMP_DIMENSIONS = 101;
    private final int WIDTH_CARD = 120;
    private final int HEIGHT_CARD = 180;
    
    /**
     * @param player
     * @param playerNames
     * @param beanScore
     * @param beanTrick
     *            crée les panneaux et les imbrique les uns aux autres
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames,
            ScoreBean beanScore, TrickBean beanTrick, HandBean beanHand,
            BlockingQueue<Card> queue) {

        this.playerNames = playerNames;
        this.player = player;
        GridPane scorePane = createScorePane(beanScore);
        GridPane trickPane = createTrickPane(beanTrick);
        HBox handPane = createHandPane(beanHand, queue);
        BorderPane pane = new BorderPane(trickPane, scorePane, null, handPane,
                null);       
        StackPane mainPane = new StackPane();
        mainPane.getChildren().add(pane);
        mainPane.getChildren().addAll(createVictoryPanes(beanScore));
        scene = new Scene(mainPane);
    
    }

    /**
     * @return le stage principale du stage
     */
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Javass - " + playerNames.get(player));
        stage.setMaxHeight(900);
        return stage;
    }

    private HBox createHandPane(HandBean hb, BlockingQueue<Card> queue) {
        HBox handPane = new HBox();
        handPane.setStyle("-fx-background-image: url(\"bois.jpg\");\n" + 
                "-fx-background-repeat: stretch;\n" + 
                "-fx-background-size: 1500 150;\n" + 
                "-fx-background-position: center center;\n"
                + "-fx-spacing: 5px;\n" + "-fx-padding: 5px;");
        handPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < Jass.HAND_SIZE; i++) {
            int s = i; // crée uniquement pour utilisation dans l'expression
                       // lambda

            ImageView card = new ImageView();
            card.setFitWidth(WIDTH_HAND);
            card.setFitHeight(HEIGHT_HAND);
            card.imageProperty().bind(Bindings.valueAt(observableMap,
                    Bindings.valueAt(hb.hand(), i)));

            card.setOnMouseClicked((e) -> {
                try {
                    queue.put(hb.hand().get(s));
                } catch (InterruptedException e1) {
                    throw new Error(e1);
                }
            });

            BooleanBinding isPlayable = Bindings.createBooleanBinding(() -> {
                return hb.playableCards().contains(hb.hand().get(s));
            }, hb.playableCards(), hb.hand());

            card.opacityProperty()
                    .bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
            card.disableProperty().bind(isPlayable.not());
            handPane.getChildren().add(card);
        }
        return handPane;
    }

    // retourne le texte contenant les noms des joueurs player1 et player2
    private Text nameCreator(PlayerId player1, PlayerId player2) {
        Text res = new Text(playerNames.get(player1) + " et "
                + playerNames.get(player2) + " : ");
        res.setTextAlignment(TextAlignment.RIGHT);
        return res;
    }

    // cree le texte du score et fais les liaisons nécessaire
    private Text turnScoreCreator(TeamId t, ScoreBean sb) {
        Text turnScore = new Text();
        turnScore.textProperty()
                .bind(Bindings.convert(sb.turnPointsProperty(t)));
        turnScore.setTextAlignment(TextAlignment.RIGHT);
        return turnScore;
    }

    // cree le texte du jeu et fais les liaisons nécessaire
    private Text gamePointsCreator(TeamId t, ScoreBean sb) {
        Text gamePoints = new Text();
        gamePoints.setTextAlignment(TextAlignment.RIGHT);
        gamePoints.textProperty()
                .bind(Bindings.convert(sb.gamePointsProperty(t)));
        return gamePoints;
    }

    // cree le texte des différences de score et fais les liaisons
    // nécessaire
    private Text diffCreator(TeamId t, ScoreBean sb) {
        Text textDiff = new Text();
        StringProperty difference = new SimpleStringProperty();
        textDiff.textProperty().bind(difference);
        sb.turnPointsProperty(t).addListener((o, oV, nV) -> {

            if (nV.intValue() != 0) {
                int diff = nV.intValue() - oV.intValue();
                difference.set("(+" + Integer.toString(diff) + ")");
            } else {

                difference.set("");
            }
        });
        return textDiff;
    }

    // Panneau du score
    private GridPane createScorePane(ScoreBean sb) {
        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima;-fx-background-color: lightgray;-fx-padding: 5px;-fx-alignment: center;");

        // noms x2
        Text names = nameCreator(PlayerId.PLAYER_1, PlayerId.PLAYER_3);
        Text names1 = nameCreator(PlayerId.PLAYER_2, PlayerId.PLAYER_4);
        scorePane.add(names, 0, 0);
        scorePane.add(names1, 0, 1);

        // chaines "TOTAL" x2
        Text stringTotal = new Text(" /Total :");
        stringTotal.setTextAlignment(TextAlignment.LEFT);
        scorePane.add(stringTotal, 3, 0);

        Text stringTotal1 = new Text(" /Total :");
        stringTotal1.setTextAlignment(TextAlignment.LEFT);
        scorePane.add(stringTotal1, 3, 1);

        // scores du pli
        Text turnScore = turnScoreCreator(TeamId.TEAM_1, sb);
        scorePane.add(turnScore, 1, 0);

        Text turnScore1 = turnScoreCreator(TeamId.TEAM_2, sb);
        scorePane.add(turnScore1, 1, 1);

        // scores de la partie
        Text gamePoints = gamePointsCreator(TeamId.TEAM_1, sb);
        scorePane.add(gamePoints, 4, 0);

        Text gamePoints1 = gamePointsCreator(TeamId.TEAM_2, sb);
        scorePane.add(gamePoints1, 4, 1);

        // difference
        Text textDiff = diffCreator(TeamId.TEAM_1, sb);
        scorePane.add(textDiff, 2, 0);

        Text textDiff1 = diffCreator(TeamId.TEAM_2, sb);
        scorePane.add(textDiff1, 2, 1);

        return scorePane;
    }

    private ImageView createTrump(TrickBean tb) {
        ImageView imageTrump = new ImageView();
        imageTrump.imageProperty()
                .bind(Bindings.valueAt(observableMapTrump, tb.trumpProperty()));
        imageTrump.setFitWidth(TRUMP_DIMENSIONS);
        imageTrump.setFitHeight(TRUMP_DIMENSIONS);
        return imageTrump;
    }

    private VBox createCard(TrickBean tb, Rectangle halo, PlayerId p,
            Pos value) {

        Text leftCard = new Text();
        leftCard.setStyle(
                "-fx-font: 14 Optima;-fx-padding: 5px; -fx-alignment: center;");
        leftCard = new Text(playerNames.get(p));

        ImageView imageCardLeft = new ImageView();
        imageCardLeft.imageProperty().bind(Bindings.valueAt(observableMap,
                Bindings.valueAt(tb.trick(), p)));
        imageCardLeft.setFitWidth(WIDTH_CARD);
        imageCardLeft.setFitHeight(HEIGHT_CARD);

        StackPane paneStack = new StackPane();
        halo.visibleProperty().bind(tb.winningPlayerProperty().isEqualTo(p));
        paneStack.getChildren().addAll(halo, imageCardLeft);
        VBox nodeCardName = null;
        if(p==player) {
             nodeCardName = new VBox(paneStack, leftCard);
        }else {
             nodeCardName = new VBox(leftCard, paneStack);
        }
        
        nodeCardName.setAlignment(value);
        return nodeCardName;
    }

    // Panneau du pli
    private GridPane createTrickPane(TrickBean tb) {

        GridPane trickPane = new GridPane();       
        trickPane.setStyle("-fx-background-image: url(\"tapis.jpg\");\n" + 
                "-fx-background-repeat: stretch;\n" + 
                "-fx-background-size: 2000 2000;\n" + 
                "-fx-background-position: center center;\n"
                + "-fx-padding: 5px;\n" + "-fx-border-width: 3px 0px;\n"
                + "-fx-border-style: solid;\n" + "-fx-border-color: gray;\n"
                + "-fx-alignment: center;");
        // ATOUT
        ImageView imageTrump = createTrump(tb);
        trickPane.add(imageTrump, 1, 1);
        GridPane.setHalignment(imageTrump, HPos.CENTER);

        // HALO
        Rectangle halo = getHalo();
        Rectangle halo2 = getHalo();
        Rectangle halo3 = getHalo();
        Rectangle halo4 = getHalo();

        // CARTE GAUCHE
        PlayerId p = player == PlayerId.PLAYER_1 ? PlayerId.PLAYER_4
                : PlayerId.values()[player.ordinal() - 1];
        VBox nodeCardName = createCard(tb, halo, p, Pos.CENTER);
        trickPane.add(nodeCardName, 0, 1);

        // CARTE DROITE
        PlayerId p2 = PlayerId.values()[(player.ordinal() + 1) % 4];
        VBox nodeCardName2 = createCard(tb, halo2, p2, Pos.CENTER);
        trickPane.add(nodeCardName2, 2, 1);

        // CARTE HAUT
        PlayerId p3 = PlayerId.values()[(p2.ordinal() + 1) % 4];
        VBox nodeCardName3 = createCard(tb, halo3, p3, Pos.CENTER);
        trickPane.add(nodeCardName3, 1, 0);

        // CARTE BAS (GraphicalPlayer)
        VBox nodeCardName4 = createCard(tb, halo4, player, Pos.BOTTOM_CENTER);
        trickPane.add(nodeCardName4, 1, 2);

        return trickPane;
    }

    // PANNEAUX DE VICTOIRE
    private BorderPane[] createVictoryPanes(ScoreBean sb) {
        BorderPane[] victoryPanes = new BorderPane[2];
        for (int i=0 ; i<TeamId.COUNT; i++) {
        victoryPanes[i] = new BorderPane();
        victoryPanes[i].setStyle(
                "-fx-font: 16 Optima;\n" + "-fx-background-color: white;");
        Text text = new Text();
        text.textProperty()
                .bind(Bindings.format(
                        "%s et %s ont gagne avec %d points contre %d .",
                        playerNames.get(PlayerId.values()[i]),
                        playerNames.get(PlayerId.values()[i+2]),
                        sb.totalPointsProperty(TeamId.values()[i]),
                        sb.totalPointsProperty(TeamId.values()[(i+1)%TeamId.COUNT])));
        victoryPanes[i].setCenter(text);
        victoryPanes[i].visibleProperty()
                .bind(sb.winningTeamProperty().isEqualTo(TeamId.values()[i]));
        }
        
        return victoryPanes;
    }
    
    // retourne un halo crée
    private Rectangle getHalo() {
        Rectangle halo = new Rectangle(120, 180);
        halo.setStyle("-fx-arc-width: 20;\n" + "-fx-arc-height: 20;\n"
                + "-fx-fill: transparent;\n" + "-fx-stroke: lightpink;\n"
                + "-fx-stroke-width: 5;\n" + "-fx-opacity: 0.5;");
        halo.setEffect(new GaussianBlur(4));
        return halo;
    }

    // Associe chaque carte a son image
    private ObservableMap<Card, Image> getMapCard() {
        ObservableMap<Card, Image> map = FXCollections.observableHashMap();
        for (int i = 0; i < Jass.HAND_SIZE; i++) {
            for (int j = 0; j < Color.COUNT; j++) {
                Card c = Card.of(Color.values()[j], Rank.values()[i]);
                map.put(c, new Image(getCardRef(c)));
            }
        }
        return map;
    }

    // Associe chaque couleur atout a son image
    private ObservableMap<Color, Image> getMapTrump() {
        ObservableMap<Color, Image> mapTrump = FXCollections
                .observableHashMap();
        for (int i = 0; i < Color.COUNT; i++) {
            mapTrump.put(Color.values()[i],
                    new Image(getColorRef(Color.values()[i])));
        }
        return mapTrump;
    }

    // retourne la chaine correspondante à l'URL de la carte donnée
    private String getCardRef(Card card) {
        return "/card_" + card.color().ordinal() + "_" + card.rank().ordinal()
                + "_" + "240.png";
    }

    // retourne la chaine correspondante à l'URL de la couleur donnée
    private String getColorRef(Color color) {
        return "/trump_" + color.ordinal() + ".png";
    }
}
