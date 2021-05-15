package OpenCV;
import Interface.Screens.MainScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;

import java.util.Timer;
import java.util.TimerTask;

public class FaceDetection extends VBox {
    private MainScreen mainScreen;
    public  FD_Controller controller;
    private final int DELAY = 30;
    private boolean firstView;

    public FaceDetection(MainScreen mainScreen){
        this.mainScreen = mainScreen;
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
    public boolean faceDetected(){
        // current frame
        Mat frame = new Mat();
        // read the current frame
        controller.capture.read(frame);
        controller.detectAndDisplay(frame);
        //Check if there is a face
        if(controller.currentFacesArray.length>0){
            //Check if the face is moving
            if(controller.previousFacesArray.length>0){
                return true;
            }
        }
        return false;
    }

    public void manageFaceLeaving(){
        if(!faceDetected()){
            System.out.println("timer started");
            Timer timer = new Timer();
            long start = System.currentTimeMillis();
            final long[] end = {System.currentTimeMillis()};
            final long[] elapsedTime = {end[0] - start};
            TimerTask task = new TimerTask(){
                public void run(){
                    if(faceDetected()){
                        timer.cancel();
                        end[0] = System.currentTimeMillis();
                        elapsedTime[0] = end[0] - start;
                        System.out.println("face detected after " + elapsedTime[0]);
                        if(elapsedTime[0]>DELAY/5){
                            mainScreen.chat.receiveMessage("Hey you are back !");
                        }
                    }
                }
            };
            timer.schedule(task,DELAY*1000);
        }
    }
}