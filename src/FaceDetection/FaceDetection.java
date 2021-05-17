package OpenCV;
import DataBase.Data;
import Interface.Screens.MainScreen;
import Interface.Screens.StartScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.*;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class FaceDetection extends VBox {
    public MainScreen mainScreen;
    public  FD_Controller controller;
    private final int DELAY = 30;

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
        //start a timer
        long start = System.currentTimeMillis();
        Task task = new Task<Void>() {
            @Override public Void call() throws InterruptedException {
                final int max = 1000000;
                for (int i = 1; i <= max; i++) {
                    //refresh every 1sec
                    Thread.sleep(1000);
                    long now = System.currentTimeMillis();
                    long elapsedTime = Math.abs(now - start);
                    if(elapsedTime/1000>DELAY){
                        //LOG OUT if face not detected after delay
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                Data.setImage("src/DataBase/defaultBackground.jpg");
                                mainScreen.stage.close();
                                mainScreen.faceDetection.controller.capture.release();
                                try {
                                    StartScreen startScreen = new StartScreen();
                                    startScreen.start(mainScreen.stage);
                                    startScreen.errorInfo.setText("You have been logged out because of inactivity");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    if(faceDetected()){
                        //face detected
                        //System.out.println("face detected after " + elapsedTime/1000+  " seconds.");
                        if(elapsedTime/1000>DELAY/5){
                            //if face is detected after a certain time
                            //System.out.println("hey you are back");
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    mainScreen.chat.receiveMessage("Hey you are back !(after " + elapsedTime/1000 + "sec)");
                                    mainScreen.manageFaceDetection();
                                }
                            });
                            break;
                        }
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}