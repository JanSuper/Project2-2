package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.DisplaySkills;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreen {
    public ChatApp chat;
    public static DisplaySkills displaySkills;
    public BorderPane root;
    //public static Color themeColor = new Color(0,0.47379, 1, 1);
    public static Color themeColor = Color.DARKSLATEGRAY;

    public MainScreen() throws IOException {
        chat = new ChatApp(Data.getUsername());
        displaySkills = new DisplaySkills();
        createContent();
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void createContent() {
        int borderWidth = 10;
        Border border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));

        root = new BorderPane();
        root.setBorder(border);

        chat.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        chat.prefWidthProperty().bind(root.widthProperty().divide(2.8));
        displaySkills.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        displaySkills.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));

        root.setRight(chat);
        root.setLeft(displaySkills);
    }
}