package Interface.MenuTools;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuTitle extends Pane {
    private Text text;

    public MenuTitle(String name) {
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }


        text = new Text(spread);
        text.setId("title");
        text.setFill(Color.WHITE);
        Font font = Font.loadFont("file:res/fonts/PlayMeGames-Demo.otf", 40);
        text.setFont(font);
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
