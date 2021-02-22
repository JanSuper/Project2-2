package DataBase;

import Agents.Assistant;
import Agents.User;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class Data {
    private static String username = "defaultUsername";
    private static String password = "defaultPasseword";

    private static String [][] dataSet;

    private static User user;
    private static Assistant assistant;

    public static Background createBackGround(){
        Image image = new Image(String.valueOf(Data.class.getResource("background.jpg")),Double.MAX_VALUE,Double.MAX_VALUE,false,true);

        BackgroundSize backgroundSize = new BackgroundSize(Double.MAX_VALUE, Double.MAX_VALUE, true, true, true, true);

        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);

        Background background = new Background(backgroundImage);
        return background;
    }

    public static String[][] getDataSet() {
        return dataSet;
    }

    public static void setDataSet(String[][] dataSet) {
        Data.dataSet = dataSet;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Data.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Data.password = password;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Data.user = user;
    }

    public static Assistant getAssistant() {
        return assistant;
    }

    public static void setAssistant(Assistant assistant) {
        Data.assistant = assistant;
    }
}
