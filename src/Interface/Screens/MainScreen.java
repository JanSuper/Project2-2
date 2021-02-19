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
    public ChatApp chat;
    public BorderPane root;
    public int borderWidth;
    public Border border;
    public static Color themeColor = new Color(0.2,0.35379, 0.65, 1);
    public Stage stage;

    private Label userNameLabel;
    private Button settings;
    private Button help;
    private Button logOut;
    private VBox vBox;

    public MainScreen() throws Exception {
        borderWidth = 10;
        border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));
        chat = new ChatApp(Data.getUsername(),this);

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

    public void setOptionsMenu() {
        userNameLabel = new Label(Data.getUsername());
        settings = new Button("Settings");
        help = new Button("Help");
        logOut = new Button("Log out");
        vBox = new VBox(40);

        settings.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        settings.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        settings.setPrefWidth(250);
        settings.setTextFill(Color.LIGHTGRAY);
        settings.setCursor(Cursor.HAND);
        settings.setOnMouseClicked(event -> {
            displaySettings(vBox);
        });

        userNameLabel.setTranslateX(10);
        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 45)));
        userNameLabel.setStyle("-fx-text-fill: white");

        help.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        help.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        help.setPrefWidth(250);
        help.setTextFill(Color.LIGHTGRAY);
        help.setOnKeyReleased(event -> {});   //TODO
        help.setCursor(Cursor.HAND);

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

    public void displaySettings(VBox vBox){
        userNameLabel.setText("Settings");
        settings.setText("Volume");
        help.setText("Help");
        logOut.setText("Back");

        settings.setOnMouseClicked(event -> {
            displaySettings(vBox);
        });

        help.setOnKeyReleased(event -> {});   //TODO

        logOut.setOnMouseClicked(event -> {
            setOptionsMenu();
        });
    }

    public void setWeatherDisplay(String city, String country) throws Exception {
        WeatherDisplay weatherDisplay = new WeatherDisplay(city, country,this);
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