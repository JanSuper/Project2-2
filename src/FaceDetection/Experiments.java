package FaceDetection;

import DataBase.Data;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.Stage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Experiments extends Application {
    FaceDetection faceDetection = new FaceDetection();

    private Imgcodecs Highgui;

    public Experiments() throws IOException {
    }

    public Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);
        return mat;
    }

    public Image detectFaceFromFrame(Mat frame){

        // detection
        faceDetection.controller.detectAndDisplay(frame);

        // convert the Mat object (OpenCV) to Image (JavaFX)
        return mat2Image(frame);
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

    public void initialize(){
        faceDetection.controller.faceCascade = new CascadeClassifier();
        faceDetection.controller.absoluteFaceSize = 0;

        faceDetection.controller.rEyeCascade = new CascadeClassifier();
        faceDetection.controller.absoluteREyesSizeWidth = 0;
        faceDetection.controller.absoluteREyesSizeHeight = 0;
        faceDetection.controller.lEyeCascade = new CascadeClassifier();
        faceDetection.controller.absoluteLEyesSizeWidth = 0;
        faceDetection.controller.absoluteLEyesSizeHeight = 0;

        faceDetection.controller.mouthCascade = new CascadeClassifier();
        faceDetection.controller.absoluteMouthSizeWidth = 0;
        faceDetection.controller.absoluteMouthSizeHeight = 0;

        faceDetection.controller.haarClassifier.setSelected(true);
        faceDetection.controller.haarSelected();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();

        Image image = new Image(new File("src/DataBase/me.jpg").toURI().toString(), Double.MAX_VALUE, Double.MAX_VALUE, false, true);
        Mat frame = imageToMat(image);

        Image imageDetected = detectFaceFromFrame(frame);

        //Creating the image view
        ImageView imageView = new ImageView();
        //Setting image to the image view
        imageView.setImage(imageDetected);
        //Setting the image view parameters
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(575);
        imageView.setPreserveRatio(true);
        //Setting the Scene object
        Group root = new Group(imageView);
        Scene scene = new Scene(root, imageDetected.getWidth(), imageDetected.getHeight());
        primaryStage.setTitle("Displaying Image");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
