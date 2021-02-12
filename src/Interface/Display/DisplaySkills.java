package Interface.Display;

import DataBase.Data;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

import java.io.FileNotFoundException;

public class DisplaySkills extends VBox {
    private Background background = Data.createBackGround();
    //private Background background = new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY));

    public DisplaySkills() throws Exception {
        super(7);
        super.setBackground(background);

        //for testing
        getChildren().setAll(getWeatherDisplay());
    }

    public WeatherDisplay getWeatherDisplay() throws Exception {
        return new WeatherDisplay();
    }
}
