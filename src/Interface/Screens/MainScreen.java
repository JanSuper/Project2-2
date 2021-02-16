package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.WeatherDisplay;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainScreen {
    public static ChatApp chat;
    public static BorderPane root;
    public static int borderWidth;
    public static Border border;
    public static Color themeColor = new Color(0.2,0.35379, 0.65, 1);
    public static Stage stage;

    public MainScreen() throws Exception {
        borderWidth = 10;
        border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));
        chat = new ChatApp(Data.getUsername());

        createContent();
        start(new Stage());
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        stage = primaryStage;
        primaryStage.show();
    }

    public void createContent() {
        root = new BorderPane();
        root.setBorder(border);
        root.setBackground(Data.createBackGround());

        chat.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        chat.prefWidthProperty().bind(root.widthProperty().divide(2.8));

        root.setRight(chat);
        setOptionsMenu();
    }

    public static void setOptionsMenu() {
        Label userNameLabel = new Label(Data.getUsername());
        userNameLabel.setTranslateX(10);
        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 45)));
        userNameLabel.setStyle("-fx-text-fill: white");

        Button settings = new Button("Settings");
        settings.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        settings.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        settings.setPrefWidth(250);
        settings.setTextFill(Color.LIGHTGRAY);
        settings.setOnKeyReleased(event -> {});   //TODO
        settings.setCursor(Cursor.HAND);

        Button help = new Button("Help");
        help.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        help.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        help.setPrefWidth(250);
        help.setTextFill(Color.LIGHTGRAY);
        help.setOnKeyReleased(event -> {});   //TODO
        help.setCursor(Cursor.HAND);

        Button logOut = new Button("Log out");
        logOut.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        logOut.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        logOut.setPrefWidth(250);
        logOut.setTextFill(Color.LIGHTGRAY);
        logOut.setCursor(Cursor.HAND);
        logOut.setOnMouseClicked(event -> {
            stage.close();
            StartScreen startScreen= new StartScreen();
            try {
                startScreen.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        VBox vBox = new VBox(40);
        vBox.getChildren().setAll(userNameLabel, settings, help, logOut);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(new Color(0.2,0.35379, 0.65, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        vBox.setBorder(border);
        vBox.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        vBox.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        vBox.setScaleX(0.8);
        vBox.setScaleY(0.8);

        root.setLeft(vBox);
    }

    public static void setWeatherDisplay(String city, String country) throws Exception {
        WeatherDisplay weatherDisplay = new WeatherDisplay(city, country);
        weatherDisplay.setSpacing(7);
        weatherDisplay.setBackground(Data.createBackGround());
        weatherDisplay.setBorder(border);
        weatherDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        weatherDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        weatherDisplay.setScaleX(0.8);
        weatherDisplay.setScaleY(0.8);

        root.setLeft(weatherDisplay);
    }
}