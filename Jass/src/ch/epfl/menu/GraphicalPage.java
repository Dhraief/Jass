package ch.epfl.menu;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class GraphicalPage implements Runnable{
    
    private Scene scene;


    public GraphicalPage(String t , String contenu) {
        Text title= new Text (t);
        title.setFont(new Font("Verdana" , 50));
        title.setFill(Color.WHITESMOKE);
        title.setTextAlignment(TextAlignment.CENTER);
        Text text= new Text (contenu);
        text.setFont(new Font("Verdana" , 39));
        text.setFill(Color.WHITE);
        
        BorderPane pane = new BorderPane(text , title , null , null , null);
        pane.setStyle("-fx-background-image: url(\"fondTuto.jpg\");\n" + 
                "-fx-background-repeat: stretch;\n" + 
                "-fx-background-size: 2000 950;\n" + 
                "-fx-background-position: center center;\n");
        pane.setPrefSize(500, 900);
        scene = new Scene(pane);
    }


    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Javass");
        stage.setMaxHeight(850);
        return stage;
    }
    @Override
    public void run() {
        Platform.runLater(
                () -> { createStage().show(); }
                );
    }
    
    
}

