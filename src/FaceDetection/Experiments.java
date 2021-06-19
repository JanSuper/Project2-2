package FaceDetection;

import Interface.Screens.MainScreen;
import Interface.Screens.StartScreen;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;

public class Experiments {
    FaceDetection faceDetection;
    FaceClassifier faceClassifier;

    private Imgcodecs Highgui;

    public Experiments(FaceDetection faceDetection,FaceClassifier faceClassifier){
        this.faceClassifier = faceClassifier;
        this.faceDetection = faceDetection;
    }

    public static void main(String[]args) throws Exception {
        Experiments experiments = new Experiments(new FaceDetection(),new FaceClassifier());

    }

    public void detectFaceFromFrame(Mat frame){
        // init everything
        Image imageToShow = null;

        // detection
        faceDetection.controller.detectAndDisplay(frame);

        // convert the Mat object (OpenCV) to Image (JavaFX)
        imageToShow = mat2Image(frame);
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    private Image mat2Image(Mat frame)
    {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Highgui.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}
