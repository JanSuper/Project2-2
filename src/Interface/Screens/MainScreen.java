package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.DisplaySkills;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreen {
    public ChatApp chat;
    public DisplaySkills displayView;
    public BorderPane root;

    public MainScreen() throws IOException {
        chat = new ChatApp(Data.getUsername());
        displayView = new DisplaySkills();
        createContent();
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void createContent() {
        StackPane right = new StackPane();
        right.getChildren().add(chat);
        right.setAlignment(Pos.CENTER_RIGHT);

        StackPane left = new StackPane();
        left.getChildren().add(displayView);

        Border border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(15)));
        root = new BorderPane();
        root.setBorder(border);
        root.setRight(right);
        root.setLeft(left);
    }
}