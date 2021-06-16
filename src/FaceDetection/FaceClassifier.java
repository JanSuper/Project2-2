package FaceDetection;

import FileParser.FileParser;
import org.opencv.core.Rect;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FaceClassifier {
    public static Random rn = new Random();

    public static int count = 0;

    static public final int MAX_EYES = 20;
    static public final int MAX_FACES = 10;
    static public final int MAX_MOUTHS = 10;
    static final int MAX_DIFF = 5;
    static final int MAX_CLUSS = 5;

    static boolean eyesDone = false;
    static boolean mouthDone = false;

    public static boolean canClassify = false;

    public static List<Rect> eyes = new ArrayList();
    public static List<Rect> face = new ArrayList();
    public static List<Rect> mouth = new ArrayList();

    public static int[] leftEyePos = new int[2];
    public static int[] rightEyePos = new int[2];
    public static int[] mouthPos = new int[2];
    public static int[] facePos = new int[2];

    public static double eyesD;
    public static double midMouthD;
    public static double lEyeMouthD;
    public static double rEyeMouthD;
    public static double lEyeMidD;
    public static double rEyeMidD;

    public static List<String> data = new ArrayList();

    public static int writeCount = 0;

    public static String getPerson() throws IOException {
        eyesD = eucDis(leftEyePos, rightEyePos);
        midMouthD = eucDis(facePos, mouthPos);
        lEyeMidD = eucDis(leftEyePos, mouthPos);
        rEyeMouthD = eucDis(rightEyePos, mouthPos);
        lEyeMidD = eucDis(leftEyePos, facePos);
        rEyeMidD = eucDis(rightEyePos, facePos);

        String comb = eyesD + "," + midMouthD + "," + lEyeMouthD + "," + rEyeMouthD + "," + lEyeMidD + "," + rEyeMidD;
        writeCount++;

        if(writeCount == 1000) {
            data.add(comb);
            writeCount = 0;
        }

        return comb;
    }

    public static String getClosestPerson(){
        File[] users = new File("src/DataBase/Users").listFiles();
        for (File user:users) {
            String[] distances = FileParser.getUserInfo(user.getName(),"-Face").split(",");
            double[] dist = new double[distances.length];
            for (int i = 0; i < distances.length; i++) {
                dist[i] = Double.parseDouble(distances[i]);
            }
            //ANN here
            if(true){
                return user.getName();
            }
        }
        return "not found";
    }


    public static void writeToFile(){
        System.out.println("writing in data");

        PrintWriter writer = null;

        try {
            writer = new PrintWriter("Desktop/data.txt", "UTF-8");
            for(int i = 0; i < data.size(); i++) {
                writer.println(data.get(i));
            }
        } catch (IOException ex) {
            // Report
            System.out.println("bruuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuh");
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }

    }

    public static double eucDis(int[] a, int[] b){
        double distance = Math.sqrt(((double)(Math.pow(a[0] - b[0],2)) + (double)(Math.pow(a[1] - b[1],2))));
        return distance;
    }

    public static void addEyes(List<Rect> newEyes){

        if(face.size() != MAX_FACES)
            return;

        newEyes = Eyefilter(newEyes);

        int amountToGo = eyes.size() + newEyes.size() - MAX_EYES;

        for(int i = 0; i < amountToGo; i++){
            eyes.remove(0);
        }
        for(int i = 0; i <= newEyes.size()-1; i++){
//            System.out.println(newEyes.get(i).x + " " + newEyes.get(i).y);
            eyes.add(newEyes.get(i));
        }

        if (eyes.size() == MAX_EYES){

            int[][] hold = kCluster(eyes, 2);
            if (hold[0][0] < hold[1][0]){ //Right left
                rightEyePos = hold[0];
                leftEyePos = hold[1];
            }
            else{ // Left Right
                leftEyePos = hold[0];
                rightEyePos = hold[1];
            }
            eyesDone = true;

            if (mouthDone){
                canClassify=true;
            }
//            System.out.println("Left eye at " + Arrays.toString(leftEyePos));
//            System.out.println("Right eye at " + Arrays.toString(rightEyePos));
        }
    }

    public static List<Rect> Eyefilter(List<Rect> newParts){
        for(int i = newParts.size() -1 ; i >= 0; i--){
            if(newParts.get(i).y > facePos[1]){
                newParts.remove(i);
            }
        }
        return newParts;
    }

    public static List<Rect> MouthFilter(List<Rect> newParts){
        for(int i = newParts.size() -1 ; i >= 0; i--){
            if(newParts.get(i).y < facePos[1]){
                newParts.remove(i);
            }
        }
        return newParts;
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
//            System.out.println("Face at " + Arrays.toString(facePos));
        }

    }

    public static int[] calcMiddle(List<Rect> boxes){
        int posX = 0;
        int posY = 0;
        for (int i = 0; i < boxes.size(); i++){
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

        newMouth = MouthFilter(newMouth);
        int amountToGo = mouth.size() + newMouth.size() - MAX_MOUTHS;


        for(int i = 0; i < amountToGo; i++){
            mouth.remove(0);
        }
        for(int i = 0; i <= newMouth.size()-1; i++){
            mouth.add(newMouth.get(i));
        }

        if (mouth.size() == MAX_MOUTHS){
            mouthPos = calcMiddle(mouth);
            mouthDone = true;

            if (eyesDone){
                canClassify=true;
            }
//            System.out.println("Mouth at " + Arrays.toString(mouthPos));
        }
    }

    public static int[][] kCluster (List<Rect> boxes, int k){
        int[][] means = new int[k][2];
        List<Integer> numbers = new ArrayList();
        count = 1;
        for (int i = 0; i < k; i++){
            int random = rn.nextInt(boxes.size());
            while (numbers.contains(random)){
                random = rn.nextInt(boxes.size());
            }
            numbers.add(random);
            Rect boxHold = boxes.get(i);
            means[i][0] = boxHold.x + boxHold.width/2;
            means[i][1] = boxHold.y+ boxHold.height/2;
        }
        return kClusterRecursion(boxes, means, null);
    }

    public static int[][] kClusterRecursion (List<Rect> boxes, int[][] means, int[][] prevMeans){
        int k = means.length;
        int[][] holdMeans = new int[k][2];
        for(int i = 0; i < k; i++){ //You have to copy entry for entry because java is stupid :)))
            holdMeans[i][0] = means[i][0];
            holdMeans[i][1] = means[i][1];
        }
        List<ArrayList<Rect>> kLists = new ArrayList(); // List with lists of points that coincide with each mean
        for (int i = 0; i < k; i++){
            kLists.add(new ArrayList());
        }

        for(int i = 0; i < boxes.size(); i++){
            int memK = -1;
            double shortest = Double.POSITIVE_INFINITY;
            Rect holdBox = boxes.get(i);
            for(int j = 0; j < k; j++){
                double distance = Math.sqrt(((double)(Math.pow((holdBox.x + holdBox.height/2) - means[j][0],2)) + (double)(Math.pow((holdBox.y + holdBox.width/2) - means[j][1],2))));
                if (distance < shortest){
                    memK = j;
                    shortest = distance;
                }
            }
            kLists.get(memK).add(holdBox);
        }


        for(int i = 0; i < k; i++){
            int xPos = 0;
            int yPos = 0;
            List<Rect> holdList = kLists.get(i);
            for (int j = 0; j < holdList.size(); j++){
                xPos += holdList.get(j).x + holdList.get(j).height/2;
                yPos += holdList.get(j).y + holdList.get(j).width/2;
            }
            if(holdList.size() != 0) {
                xPos /= holdList.size();
                yPos /= holdList.size();

                means[i][0] = xPos;
                means[i][1] = yPos;
            }


        }


        if(prevMeans == null){
            count++;
            return kClusterRecursion(boxes, means, holdMeans);
        }
        else{
            int changeX = 0;
            int changeY = 0;
            for (int i = 0; i < k; i++){
                changeX += Math.abs(means[i][0] - prevMeans[i][0]);
                changeY += Math.abs(means[i][1] - prevMeans[i][1]);
            }
            changeX /= k;
            changeY /= k;

            if (changeX+changeY > MAX_DIFF && count < MAX_CLUSS){
                count++;
                return kClusterRecursion(boxes, means, holdMeans);
            }
            else{
                return means;
            }
        }
    }
}
