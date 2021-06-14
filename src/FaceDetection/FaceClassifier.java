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

    public static int[] leftEyePos = new int[2];
    public static int[] rightEyePos = new int[2];
    public static int[] mouthPos = new int[2];
    public static int[] facePos = new int[2];

    public static String GetPerson (){
        return null;
    }

    public static void addEyes(List<Rect> newEyes){
        if(face.size() != MAX_FACES)
            return;

        newEyes = filter(newEyes);

        int amountToGo = eyes.size() + newEyes.size() - MAX_EYES;

        for(int i = 0; i < amountToGo; i++){
            eyes.remove(0);
        }
        for(int i = 0; i <= newEyes.size()-1; i++){
            eyes.add(newEyes.get(i));
        }

        if (eyes.size() == MAX_EYES){
            //TODO K-clustering
        }
    }

    public static List<Rect> filter(List<Rect> newParts){
        return null;
    }

    public static void addFace(List<Rect> newFace){
        int amountToGo = face.size() + newFace.size() - MAX_FACES;

        for(int i = 0; i < amountToGo; i++){
            face.remove(0);
        }
        for(int i = 0; i <= newFace.size()-1; i++){
            face.add(newFace.get(i));
        }

        if (face.size() == MAX_FACES){
            facePos = calcMiddle(face);
        }
    }

    public static int[] calcMiddle(List<Rect> boxes){
        int posX = 0;
        int posY = 0;
        for (int i = 0; i <= boxes.size(); i++){
            Rect hold = boxes.get(i);
            posX += hold.x + hold.height/2;
            posY += hold.y + hold.height/2;
        }
        int[] returnArr = {posX/boxes.size(), posY/boxes.size()};
        return returnArr;
    }

    public static void addMouth(List<Rect> newMouth){
        if(face.size() != MAX_FACES)
            return;

        newMouth = filter(newMouth);
        int amountToGo = mouth.size() + newMouth.size() - MAX_MOUTHS;

        for(int i = 0; i < amountToGo; i++){
            mouth.remove(0);
        }
        for(int i = 0; i <= newMouth.size()-1; i++){
            mouth.add(newMouth.get(i));
        }

        if (mouth.size() == MAX_MOUTHS){
            mouthPos = calcMiddle(mouth);
        }
    }
}
