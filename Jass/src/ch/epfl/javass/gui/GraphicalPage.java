package ch.epfl.javass.gui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public final class GraphicalPage{
    private Scene scene;

    public GraphicalPage(Text title , Text text) {
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(new Font("Verdana" , 30));
        
       
        text.setFont(new Font("Verdana" , 20));
        BorderPane pane = new BorderPane(text , title , null , null , null);
        pane.setStyle("-fx-background-color: linear-gradient(#E4EAA2, #9CD672)");
        scene = new Scene(pane);
    }
    
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Javass");
        stage.setMaxHeight(900);
        return stage;
    }

    
}
