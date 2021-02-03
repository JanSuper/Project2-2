package DataBase;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class Data {

    public static Background createBackGround(){
        Image image = new Image(String.valueOf(Data.class.getResource("background.jpg")),800,800,false,true);

        // Image image = new Image("https://images.hdqwalls.com/wallpapers/simple-gray-background-4k-br.jpg",800,800,false,true);

        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);

        Background background = new Background(backgroundImage);
        return background;
    }

}
