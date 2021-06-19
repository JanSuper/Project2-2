package FaceDetection;

import DataBase.Data;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Experiments {
    FaceDetection faceDetection;

    private Imgcodecs Highgui;

    public Experiments(FaceDetection faceDetection){
        this.faceDetection = faceDetection;
    }

    public static void main(String[]args) {
        Platform.startup(new Runnable() {
            @Override
            public void run() {
                Experiments experiments = null;
                try {
                    experiments = new Experiments(new FaceDetection());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image image = new Image(new File(Data.getImage()).toURI().toString(),Double.MAX_VALUE,Double.MAX_VALUE,false,true);
                Mat frame = experiments.imageToMat(image);

                Image imageDetected = experiments.detectFaceFromFrame(frame);
            }
        });
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
        // init everything
        Image imageToShow = null;

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
}
