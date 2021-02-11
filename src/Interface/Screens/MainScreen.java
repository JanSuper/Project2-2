package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainScreen {
    public ChatApp chat;
    public BorderPane root;

    public MainScreen(){
        chat = new ChatApp();
        createContent();
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void createContent(){
        StackPane right = new StackPane();
        right.getChildren().add(chat.root);
        root = new BorderPane();
        root.setRight(right);
        root.setBackground(Data.createBackGround());

        Border border = new Border(new BorderStroke(Color.DARKGRAY,
                BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(30)));
        root.setBorder(border);
    }

}
