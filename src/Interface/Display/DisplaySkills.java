package Interface.Display;

import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

public class DisplaySkills extends VBox {
    private Background background = Data.createBackGround();

    public DisplaySkills() throws Exception {
        super(7);
        super.setBackground(background);
        super.setScaleX(0.8);
        super.setScaleY(0.8);
        super.setBorder(MainScreen.border);

        //for testing
        getChildren().setAll(getWeatherDisplay());
    }

    public WeatherDisplay getWeatherDisplay() throws Exception {
        return new WeatherDisplay();
    }
}
