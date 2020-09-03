package ch.epfl.menu;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.Arrays;
import java.util.List;

public class JassMenuApp extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private String textTuto = getTutorial();
    private String textCredits = getCredits();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Jass Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Single Player", new SinglePlayer()),
            new Pair<String, Runnable>("Multi Player", new MultiPlayer()),
            new Pair<String, Runnable>("Tutorial", new GraphicalPage("Tutoriel: ", textTuto)),
            new Pair<String, Runnable>("Credits",  new GraphicalPage("Credits: ",textCredits)),
            new Pair<String, Runnable>("Exit to Desktop", Platform::exit)
            );

    private Pane root = new Pane();
    private VBox menuBox = new VBox(-5);
    private Line line;

    private Parent createContent() {
        addBackground();
        addTitle();
        double lineX = WIDTH / 2 - 100;
        double lineY = HEIGHT / 3 + 50;
        addLine(lineX, lineY);
        addMenu(lineX + 5, lineY + 5);
        startAnimation();
        return root;
    }
    
    private void addBackground() {
        ImageView imageView =  new ImageView(
          new Image 
            ( "/notBad.jpeg"));
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        root.getChildren().add(imageView);
    }

    private void addTitle() {
        JassTitle title = new JassTitle("JASS","white");
        title.setTranslateX(WIDTH / 2 - title.getTitleWidth() / 2);
        title.setTranslateY(HEIGHT / 3 -50);
        root.getChildren().add(title);
    }

    private void addLine(double x, double y) {
        line = new Line(x-300, y, x-300, y + 300);
        line.setStrokeWidth(3);
        line.setStroke(Color.color(1, 1, 1, 0.75));
        line.setEffect(new DropShadow(5, Color.BLACK));
        line.setScaleY(0);
        root.getChildren().add(line);
    }

    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1), line);
        st.setToY(1);
        st.setOnFinished(e -> {
            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x-300);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            JassMenuItem item = new JassMenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);
            Rectangle clip = new Rectangle(300, 50);
            clip.translateXProperty().bind(item.translateXProperty().negate());
            item.setClip(clip);
            menuBox.getChildren().addAll(item);
        });
        root.getChildren().add(menuBox);
    }
    
    private String getTutorial() {
        return "Bienvenue dans le jeu Jass,\n pour joueur en mode solo\n appuyer sur SinglePlayer\n sinon appuyer sur MultiPlayer.\n"
                + "Pour jouer c'est tres simple,\n il suffit de cliquer sur les cartes\n et appliquer les regles du jass\n"
                + "Bon jeu.";
    }
    
    private String getCredits() {
        return "Projet réalisé par :\n"
                + "Amine Atallah\n"
                + "     &\n"
                + "Mohammed Ali Dhraief";
    }
}

