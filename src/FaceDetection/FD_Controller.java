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
import java.util.ArrayList;
import java.util.Arrays;
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

    // the eye cascade classifier object
    private CascadeClassifier rEyeCascade;
    // minimum eye size width
    public int absoluteREyesSizeWidth;
    // minimum eye size height
    public int absoluteREyesSizeHeight;
    // each rectangle in eye is a face
    public Rect[] currentREyesArray;

    // the eye cascade classifier object
    private CascadeClassifier lEyeCascade;
    // minimum eye size width
    public int absoluteLEyesSizeWidth;
    // minimum eye size height
    public int absoluteLEyesSizeHeight;
    // each rectangle in eye is a face
    public Rect[] currentLEyesArray;

    // the face cascade classifier object
    private CascadeClassifier mouthCascade;
    // minimum face size width
    public int absoluteMouthSizeWidth;
    // minimum face size height
    public int absoluteMouthSizeHeight;
    // each rectangle in faces is a face
    public Rect[] currentMouthArray;


    /**
     * Init the controller variables
     */
    public void init()
    {
        this.capture = new VideoCapture();

        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;

        this.rEyeCascade = new CascadeClassifier();
        this.absoluteREyesSizeWidth = 0;
        this.absoluteREyesSizeHeight = 0;
        this.lEyeCascade = new CascadeClassifier();
        this.absoluteLEyesSizeWidth = 0;
        this.absoluteLEyesSizeHeight = 0;

        this.mouthCascade = new CascadeClassifier();
        this.absoluteMouthSizeWidth = 0;
        this.absoluteMouthSizeHeight = 0;

        this.haarClassifier.setSelected(true);
        this.haarSelected();
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
                    // detection
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
        MatOfRect rectangle = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        if(lbpClassifier.isSelected()){
            detectFace(rectangle,grayFrame,frame);
        }
        //COMBINATION OF FACE, EYES, MOUTH HAAR CLASSIFIERS
        else if(haarClassifier.isSelected()){
            detectFace(rectangle,grayFrame,frame);
            detectLeftEye(rectangle,grayFrame,frame);
            detectRightEye(rectangle,grayFrame,frame);
            detectMouth(rectangle,grayFrame,frame);
        }
    }

    public void detectFace(MatOfRect rectangle, Mat grayFrame, Mat frame){
        // compute minimum face size (24% of the frame height)
        if (this.absoluteFaceSize == 0)
        {
            int size = grayFrame.rows();
            if (Math.round(size * 0.24f) > 0)
            {
                this.absoluteFaceSize = Math.round(size * 0.24f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, rectangle, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face
        previousFacesArray = currentFacesArray;
        currentFacesArray = rectangle.toArray();
        for (int i = 0; i < currentFacesArray.length; i++)
            Imgproc.rectangle(frame, currentFacesArray[i].tl(), currentFacesArray[i].br(), new Scalar(0, 255, 0, 128), 3);
    }

    public void detectLeftEye(MatOfRect rectangle, Mat grayFrame, Mat frame){
        // compute minimum left eye size width (10% of the frame height)
        if (this.absoluteLEyesSizeWidth == 0)
        {
            int width = grayFrame.rows();
            if (Math.round(width * 0.18f) > 0)
            {
                this.absoluteLEyesSizeWidth = Math.round(width * 0.18f);
            }
        }
        // compute minimum left eye size height (7% of the frame height)
        if (this.absoluteLEyesSizeHeight == 0)
        {
            int height = grayFrame.cols();
            if (Math.round(height * 0.12f) > 0)
            {
                this.absoluteLEyesSizeHeight = Math.round(height * 0.12f);
            }
        }

        // detect left eyes
        this.lEyeCascade.detectMultiScale(grayFrame, rectangle, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                this.absoluteLEyesSizeWidth, this.absoluteLEyesSizeHeight), new Size());

        currentLEyesArray = rectangle.toArray();
        filterLEyes();
        // each rectangle in a left eye is a left eye
        for (int i = 0; i < currentLEyesArray.length; i++)
            Imgproc.rectangle(frame, currentLEyesArray[i].tl(), currentLEyesArray[i].br(), new Scalar(125,0,0, 128), 3);
    }

    public void detectRightEye(MatOfRect rectangle, Mat grayFrame, Mat frame){
        // compute minimum right eye size width (10% of the frame height)
        if (this.absoluteREyesSizeWidth == 0)
        {
            int width = grayFrame.rows();
            if (Math.round(width * 0.18f) > 0)
            {
                this.absoluteREyesSizeWidth = Math.round(width * 0.18f);
            }
        }
        // compute minimum right eye size height (7% of the frame height)
        if (this.absoluteREyesSizeHeight == 0)
        {
            int height = grayFrame.cols();
            if (Math.round(height * 0.12f) > 0)
            {
                this.absoluteREyesSizeHeight = Math.round(height * 0.12f);
            }
        }

        // detect right eyes
        this.rEyeCascade.detectMultiScale(grayFrame, rectangle, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                this.absoluteREyesSizeWidth, this.absoluteREyesSizeHeight), new Size());

        currentREyesArray = rectangle.toArray();
        filterREyes();
        // each rectangle in a right eye is a right eye
        for (int i = 0; i < currentREyesArray.length; i++)
            Imgproc.rectangle(frame, currentREyesArray[i].tl(), currentREyesArray[i].br(), new Scalar(255,0,0, 128), 3);

    }

    /**
     * removes eyes detected not in the face
     */
    public void filterREyes(){
        if(currentFacesArray.length!=0){
            for (int i = 0; i < currentFacesArray.length; i++) {
                Rect face = currentFacesArray[i];
                int[] faceCenter = FaceClassifier.calcMiddle(new ArrayList<>(Arrays.asList(face)));
                for (int j = 0; j < currentREyesArray.length; j++) {
                    Rect rEye = currentREyesArray[j];
                    Point p1 = new Point(rEye.x,rEye.y);
                    Point p2 = new Point(rEye.x+rEye.width,rEye.y+rEye.height);
                    Point p3 = new Point(rEye.x+rEye.width,rEye.y);
                    Point p4 = new Point(rEye.x,rEye.y+rEye.height);
                    //remove if outside of face
                    if(!face.contains(p1)&&!face.contains(p2)&&!face.contains(p3)&&!face.contains(p4)){
                        currentREyesArray = removeElement(rEye,currentREyesArray);
                    }
                    //remove if below the face center
                    if(rEye.y>faceCenter[1]){
                        currentREyesArray = removeElement(rEye, currentREyesArray);
                    }
                }
            }
        }

    }

    public void filterLEyes(){
        if(currentFacesArray.length!=0) {
            for (int i = 0; i < currentFacesArray.length; i++) {
                Rect face = currentFacesArray[i];
                int[] faceCenter = FaceClassifier.calcMiddle(new ArrayList<>(Arrays.asList(face)));
                for (int j = 0; j < currentLEyesArray.length; j++) {
                    Rect lEye = currentLEyesArray[j];
                    Point p1 = new Point(lEye.x, lEye.y);
                    Point p2 = new Point(lEye.x + lEye.width, lEye.y + lEye.height);
                    Point p3 = new Point(lEye.x + lEye.width, lEye.y);
                    Point p4 = new Point(lEye.x, lEye.y + lEye.height);
                    // remove if not inside the face
                    if (!face.contains(p1) && !face.contains(p2) && !face.contains(p3) && !face.contains(p4)) {
                        currentLEyesArray = removeElement(lEye, currentLEyesArray);
                    }

                    //remove if below the face center
                    if (lEye.y > faceCenter[1]) {
                        currentLEyesArray = removeElement(lEye, currentLEyesArray);
                    }
                }
            }
        }
    }

    public Rect[] removeElement(Rect element, Rect[] list){
        Rect[] newList = new Rect[list.length-1];
        int counter = 0;
        for (int i = 0; i < list.length; i++) {
            if(!(list[i].x==element.x&&list[i].y==element.y)){
                newList[counter++] = list[i];
            }
        }
        return newList;
    }

    public void detectMouth(MatOfRect rectangle, Mat grayFrame, Mat frame){
        // compute minimum mouth size width (13% of the frame width)
        if (this.absoluteMouthSizeWidth == 0)
        {
            int width = grayFrame.rows();
            if (Math.round(width * 0.13f) > 0)
            {
                this.absoluteMouthSizeWidth = Math.round(width * 0.25f);
            }
        }
        // compute minimum mouth size height (10% of the frame height)
        if (this.absoluteMouthSizeHeight == 0)
        {
            int height = grayFrame.cols();
            if (Math.round(height * 0.1f) > 0)
            {
                this.absoluteMouthSizeHeight = Math.round(height * 0.1f);
            }
        }

        // detect mouth
        this.mouthCascade.detectMultiScale(grayFrame, rectangle, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE, new Size(
                this.absoluteMouthSizeWidth, this.absoluteMouthSizeHeight), new Size());

        // each rectangle in mouth is a mouth
        currentMouthArray = rectangle.toArray();
        filterMouths();
        for (int i = 0; i < currentMouthArray.length; i++)
            Imgproc.rectangle(frame, currentMouthArray[i].tl(), currentMouthArray[i].br(), new Scalar(0,0,255, 128), 3);
    }

    public void filterMouths(){
        if(currentFacesArray.length!=0) {
            //remove mouth outside of the face
            for (int i = 0; i < currentFacesArray.length; i++) {
                Rect face = currentFacesArray[i];
                for (int j = 0; j < currentMouthArray.length; j++) {
                    Rect mouth = currentMouthArray[j];
                    Point p1 = new Point(mouth.x, mouth.y);
                    Point p2 = new Point(mouth.x + mouth.width, mouth.y + mouth.height);
                    Point p3 = new Point(mouth.x + mouth.width, mouth.y);
                    Point p4 = new Point(mouth.x, mouth.y + mouth.height);
                    if (!face.contains(p1) && !face.contains(p2) && !face.contains(p3) && !face.contains(p4)) {
                        currentMouthArray = removeElement(mouth, currentMouthArray);
                    }
                }
            }

            //remove mouth higher than middle of the face
            for (int i = 0; i < currentFacesArray.length; i++) {
                Rect face = currentFacesArray[i];
                int[] faceCenter = FaceClassifier.calcMiddle(new ArrayList<>(Arrays.asList(face)));
                for (int j = 0; j < currentMouthArray.length; j++) {
                    Rect mouth = currentMouthArray[j];
                    if (mouth.y < faceCenter[1]) {
                        currentMouthArray = removeElement(mouth, currentMouthArray);
                    }
                }
            }
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

        this.checkboxSelection("src/FaceDetection/CascadeClassifiers/haarcascade_frontalface_default.xml",
                "src/FaceDetection/CascadeClassifiers/ojoI.xml",
                "src/FaceDetection/CascadeClassifiers/ojoD.xml",
                "src/FaceDetection/CascadeClassifiers/Mouth.xml");
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

        //this.checkboxSelection("src/FaceDetection/lbpcascade_frontalface.xml");
        this.checkboxSelection("src/FaceDetection/CascadeClassifiers/cascade.xml",null,null,null);
    }


    /**
     * Common operation for both checkbox selections
     *
     * @param classifierPath1
     *            the first path where the XML file representing a training
     *            set for a classifier is present
     */
    private void checkboxSelection(String classifierPath1, String classifierPath2, String classifierPath3,String classifierPath4)
    {
        if(lbpClassifier.isSelected()){
            // load the face classifier(s)
            this.faceCascade.load(classifierPath1);
        }else if(haarClassifier.isSelected()){
            // load the face classifier(s)
            this.faceCascade.load(classifierPath1);
            // load the left eye classifier(s)
            this.lEyeCascade.load(classifierPath2);
            // load the right eye classifier(s)
            this.rEyeCascade.load(classifierPath3);
            // load the eyes classifier(s)
            this.mouthCascade.load(classifierPath4);
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
