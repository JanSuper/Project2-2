package FaceDetection;

import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

public class FaceClassifier {
    static final int MAX_EYES = 20;
    static final int MAX_FACES = 10;
    static final int MAX_MOUTHS = 10;

    public static boolean canClassify = false;

    public static List<Rect> eyes = new ArrayList();
    public static List<Rect> face = new ArrayList();
    public static List<Rect> mouth = new ArrayList();

    public static String GetPerson (){
        return null;
    }

    public static void addEyes(List<Rect> newEyes){

        int amountToGo = eyes.size() + newEyes.size() - MAX_EYES;

        for(int i = 0; i < amountToGo; i++){
            eyes.remove(0);
        }
        for(int i = 0; i <= newEyes.size()-1; i++){
            eyes.add(newEyes.get(i));
        }
    }

    public static List<Rect> filterEyes(List<Rect> newEyes){
        return null;
    }

    public static void addFace(List<Rect> newFace){

    }

    public static void addMouth(List<Rect> newMouth){

    }
}
