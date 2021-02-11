package Interface.Display;

import DataBase.Data;
import javafx.scene.layout.VBox;

public class DisplaySkills extends VBox {
    public DisplaySkills() {
        super(7);
        super.setBackground(Data.createBackGround());
        super.setPrefWidth(915);
    }
}
