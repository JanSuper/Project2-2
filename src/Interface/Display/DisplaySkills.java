package Interface.Display;

import DataBase.Data;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;

public class DisplaySkills extends VBox {
    private Background background = Data.createBackGround();
    //private Background background = new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY));

    public DisplaySkills() {
        super(7);
        super.setBackground(background);
    }
}
