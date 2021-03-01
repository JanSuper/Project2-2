package Interface.Display.ClockTools;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class AlarmVBox extends VBox {
    private DatePicker d;
    public AlarmVBox() {
        setSpacing(40);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));

        createContent();
        getChildren().addAll(d);

    }

    private void createContent(){

        // create a date picker
        d = new DatePicker();

    }

    private void createAlert(){
        /*
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();

         */
    }
}
