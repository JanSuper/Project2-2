package Interface;

import Interface.Chat.ChatApp;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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
        right.setTranslateX(-10);
        right.getChildren().add(chat.root);
        root = new BorderPane();
        root.setRight(right);
    }


}
