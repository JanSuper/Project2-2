package OpenCV;
import Interface.Screens.MainScreen;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;

public class FaceDetection extends VBox {
    public  FD_Controller controller;

    public FaceDetection(){
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        addContent();
    }
    public void addContent()
    {
        try
        {
            // load the FXML resource
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FD_FX.fxml"));
            getChildren().add(loader.load());
            // set a whitesmoke background
            setStyle("-fx-background-color: whitesmoke;");

            // init the controller
            controller = loader.getController();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}