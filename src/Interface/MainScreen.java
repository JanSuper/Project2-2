package Interface;

import DataBase.Data;
import Interface.Chat.ChatApp;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen {
    public ChatApp chat;

    public MainScreen(){
        chat = new ChatApp();
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(chat.root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
