package FaceDetection;
import Interface.Screens.MainScreen;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.*;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.util.Timer;
import java.util.TimerTask;

public class FD_Controller {
    public MainScreen mainScreen = null;
    // FXML buttons
    @FXML
    private Button cameraButton;
    // the FXML area for showing the current frame
    @FXML
    private ImageView originalFrame;
    // checkbox for selecting the Haar Classifier
    @FXML
    private CheckBox haarClassifier;
    // checkbox for selecting the LBP Classifier
    @FXML
    private CheckBox lbpClassifier;
    // checkbox for selecting the Haar eyes Classifier
    @FXML
    private CheckBox haarEyesClassifier;

    // a timer for acquiring the video stream
    private Timer timer;
    // the OpenCV object that performs the video capture
    public VideoCapture capture;
    // a flag to change the button behavior
    public boolean cameraActive;
    private Image CamStream;
    private Imgcodecs Highgui;

    // the face cascade classifier object
    private CascadeClassifier faceCascade;
    // minimum face size
    public int absoluteFaceSize;
    // each rectangle in faces is a face
    public Rect[] currentFacesArray;
    public Rect[] previousFacesArray;

    // the face cascade classifier object
    private CascadeClassifier eyeCascade;
    // minimum face size width
    public int absoluteEyesSizeWidth;
    // minimum face size height
    public int absoluteEyesSizeHeight;
    // each rectangle in faces is a face
    public Rect[] currentEyesArray;


    /**
     * Init the controller variables
     */
    public void init()
    {
        this.capture = new VideoCapture();

        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        this.eyeCascade = new CascadeClassifier();
        this.absoluteEyesSizeWidth = 0;
        this.absoluteEyesSizeHeight = 0;

        this.lbpClassifier.setSelected(true);
        this.lbpSelected();
        startCamera();
    }

    /**
     * The action triggered by pushing the button on the GUI
     */
    @FXML
    public void startCamera()
    {
        if (!this.cameraActive)
        {
            // disable setting checkboxes
            this.haarClassifier.setDisable(true);
            this.lbpClassifier.setDisable(true);
            this.haarEyesClassifier.setDisable(true);

            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened())
            {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                TimerTask frameGrabber = new TimerTask() {
                    @Override
                    public void run()
                    {
                        CamStream = grabFrame();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // show the original frames
                                originalFrame.setImage(CamStream);
                                // set fixed width
                                originalFrame.setFitWidth(250);
                                // preserve image ratio
                                originalFrame.setPreserveRatio(true);

                            }
                        });
                    }
                };
                this.timer = new Timer();
                this.timer.schedule(frameGrabber, 0, 33);

                // update the button content
                this.cameraButton.setText("Stop Camera");
                if(mainScreen!=null){
                    mainScreen.manageFaceDetection();
                }
            }
            else
            {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        }
        else
        {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.cameraButton.setText("Start Camera");
            // enable setting checkboxes
            this.haarClassifier.setDisable(false);
            this.lbpClassifier.setDisable(false);
            this.haarEyesClassifier.setDisable(false);

            // stop the timer
            if (this.timer != null)
            {
                this.timer.cancel();
                this.timer = null;
            }
            // release the camera
            this.capture.release();
            // clean the image area
            originalFrame.setImage(null);
        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    private Image grabFrame()
    {
        // init everything
        Image imageToShow = null;
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened())
        {
            try
            {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty())
                {
                    // face detection
                    this.detectAndDisplay(frame);

                    // convert the Mat object (OpenCV) to Image (JavaFX)
                    imageToShow = mat2Image(frame);
                }

            }
            catch (Exception e)
            {
                // log the (full) error
                System.err.print("ERROR");
                e.printStackTrace();
            }
        }

        return imageToShow;
    }

    /**
     * Perform face detection and show a rectangle around the detected face.
     *
     * @param frame
     *            the current frame
     */
    public void detectAndDisplay(Mat frame)
    {
        // init
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        if(haarClassifier.isSelected()||lbpClassifier.isSelected()){
            // compute minimum face size (20% of the frame height)
            if (this.absoluteFaceSize == 0)
            {
                int height = grayFrame.rows();
                if (Math.round(height * 0.2f) > 0)
                {
                    this.absoluteFaceSize = Math.round(height * 0.2f);
                }
            }

            // detect faces
            this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                    this.absoluteFaceSize, this.absoluteFaceSize), new Size());

            // each rectangle in faces is a face
            previousFacesArray = currentFacesArray;
            currentFacesArray = faces.toArray();
            for (int i = 0; i < currentFacesArray.length; i++)
                Imgproc.rectangle(frame, currentFacesArray[i].tl(), currentFacesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
        }else{
            // compute minimum eyes size width (10% of the frame height)
            if (this.absoluteEyesSizeWidth == 0&&this.absoluteEyesSizeWidth==0)
            {
                int height = grayFrame.rows();
                if (Math.round(height * 0.1f) > 0)
                {
                    this.absoluteEyesSizeWidth = Math.round(height * 0.1f);
                }
            }
            // compute minimum eyes size height (7.5% of the frame height)
            if (this.absoluteEyesSizeWidth == 0&&this.absoluteEyesSizeWidth==0)
            {
                int height = grayFrame.rows();
                if (Math.round(height * 0.075f) > 0)
                {
                    this.absoluteEyesSizeHeight = Math.round(height * 0.075f);
                }
            }

            // detect eyes
            this.eyeCascade.detectMultiScale(grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                    this.absoluteEyesSizeWidth, this.absoluteEyesSizeHeight), new Size());

            // each rectangle in faces is a face
            currentEyesArray = faces.toArray();
            for (int i = 0; i < currentEyesArray.length; i++)
                Imgproc.rectangle(frame, currentEyesArray[i].tl(), currentEyesArray[i].br(), new Scalar(255,0,0, 255), 3);
        }

    }

    /**
     * When the Haar checkbox is selected, deselect the other one and load the
     * proper XML classifier
     *
     * HOW TO TRAIN CLASSIFIER:
     *
     * F:\Downloads\opencv\build\x64\vc14\bin\opencv_createsamples.exe -info F:\Downloads\classTrain\faces.info -num 1000 -w 128 -h 128 -vec F:\Downloads\classTrain\face
     * s.vec
     *
     * F:\Downloads\opencv\build\x64\vc15\bin\opencv_traincascade.exe -data F:\Downloads\classTrain\Data -vec F:\Downloads\classTrain\faces.vec -bg F:\Downloads\cla
     * ssTrain\annotation_neg.txt -numPos 1000 -numNeg 3027 -numStages 2 -w 128 -h 128 -featureType LBP -precalcIdxBufSize 2048 -precalcValBufSize 2048
     *
     *
     *
     */
    @FXML
    protected void haarSelected()
    {
        // check whether the other are selected and deselect them
        if (this.lbpClassifier.isSelected()) {
            this.lbpClassifier.setSelected(false);
        }
        if(this.haarEyesClassifier.isSelected()){
            this.haarEyesClassifier.setSelected(false);
        }

        this.checkboxSelection("src/FaceDetection/haarcascade_frontalface_default.xml");

    }

    /**
     *
     When the LBP checkbox is selected, deselect the other one and load the
     * proper XML classifier
     */
    @FXML
    protected void lbpSelected()
    {
        // check whether the other are selected and deselect them
        if(haarClassifier.isSelected()){
            haarClassifier.setSelected(false);
        }
        if (this.haarEyesClassifier.isSelected()) {
            this.haarEyesClassifier.setSelected(false);
        }

        //this.checkboxSelection("src/FaceDetection/lbpcascade_frontalface.xml");
        this.checkboxSelection("src/FaceDetection/cascade.xml");
    }

    /**
     *
     When the Haar checkbox is selected, deselect the other one and load the
     * proper XML classifier
     */
    @FXML
    protected void haarEyeSelected()
    {
        // check whether the other are selected and deselect them
        if (this.haarClassifier.isSelected()){
            this.haarClassifier.setSelected(false);
        }
        if(this.lbpClassifier.isSelected()){
            this.lbpClassifier.setSelected(false);
        }

        this.checkboxSelection("src/FaceDetection/haarcascade_eye_tree_eyeglasses.xml");
    }

    /**
     * Common operation for both checkbox selections
     *
     * @param classifierPath
     *            the absolute path where the XML file representing a training
     *            set for a classifier is present
     */
    private void checkboxSelection(String... classifierPath)
    {
        if(haarClassifier.isSelected()||lbpClassifier.isSelected()){
            // load the face classifier(s)
            for (String xmlClassifier : classifierPath)
            {
                this.faceCascade.load(xmlClassifier);
            }
        }else{
            // load the eyes classifier(s)
            for (String xmlClassifier : classifierPath)
            {
                this.eyeCascade.load(xmlClassifier);
            }
        }


        // now the capture can start
        this.cameraButton.setDisable(false);
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
