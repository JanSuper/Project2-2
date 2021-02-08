package Interface.Chat;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatApp extends Application {

    private Stage stage;

    private TextArea messages = new TextArea();

    public ChatApp() throws Exception {
        this.stage = new Stage();
        start(this.stage);
    }

    private Parent createContent(){
        messages.setPrefHeight(550);
        TextField input = new TextField();

        VBox root = new VBox(20,messages,input);
        root.setPrefSize(600,600);
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }
}