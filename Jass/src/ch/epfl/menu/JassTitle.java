package ch.epfl.menu;


import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class JassTitle extends Pane {
        private Text text;
        private Color color;
        public JassTitle(String name, String c) {
            if (c.equals("white")) color= Color.WHITE;
            if (c.equals("black")) color= Color.BLACK;
                
            String spread = "";
            for (char c1 : name.toCharArray()) {
                spread += c1 + " ";
            }
         text = new Text(spread);
         text.setFont(new Font ("Verdana",40));
         text.setFill(color); 
         text.setEffect(new DropShadow(30, Color.BLACK));
         getChildren().addAll(text);
        }

        public double getTitleWidth() {
            return text.getLayoutBounds().getWidth();
        }

        public double getTitleHeight() {
            return text.getLayoutBounds().getHeight();
        }
    }
