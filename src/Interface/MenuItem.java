package Interface;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class MenuItem extends Pane {
    private Text text;

    public MenuItem(String name) {
        Polygon bg = new Polygon(
                0, 0,
                200, 0,
                200, 30,
                0, 30
        );
        bg.setStroke(Color.WHITE);

        bg.fillProperty().bind(
                Bindings.when(pressedProperty())
                        .then(Color.BLACK)
                        .otherwise(Color.color(0, 0, 0, 0.25))
        );


        text = new Text(name);
        Font font = Font.loadFont("file:res/fonts/PlayMeGames-Demo.otf", 20);
        text.setFont(font);
        text.setTranslateX(5);
        text.setTranslateY(20);
        text.setFill(Color.WHITE);


        getChildren().addAll(bg, text);
    }

    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    }
}
