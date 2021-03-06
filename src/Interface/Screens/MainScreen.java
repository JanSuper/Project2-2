package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.ClockAppDisplay;
import Interface.Display.MediaPlayerDisplay;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainScreen {
    public ChatApp chat;
    private ClockAppDisplay clockAppDisplay;
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

    private ArrayList<String[]> alarmsTime;

    public MainScreen() throws Exception {
        borderWidth = 10;
        border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));
        chat = new ChatApp(Data.getUsername(),this);
        clockAppDisplay = new ClockAppDisplay(this);

        createContent();
        alarmsTime = new ArrayList<>();
        //prepareAlarms();
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

        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 45)));
        userNameLabel.setStyle("-fx-text-fill: white");

        designOptionButton(settings);
        settings.setOnMouseClicked(event -> displaySettings());

        designOptionButton(help);
        help.setOnKeyReleased(event -> {});   //TODO

        designOptionButton(logOut);
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

    private void designOptionButton(Button button) {
        button.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        button.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        button.setPrefWidth(250);
        button.setTextFill(Color.LIGHTGRAY);
        button.setCursor(Cursor.HAND);
    }

    public void displaySettings(){
        userNameLabel.setText("Settings");
        settings.setText("Volume");
        help.setText("Change Password");
        logOut.setText("Back");

        settings.setOnMouseClicked(event -> {}); //TODO

        help.setOnKeyReleased(event -> {});   //TODO

        logOut.setOnMouseClicked(event -> setOptionsMenu());
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

    public void setClockAppDisplay() {
        clockAppDisplay.setBorder(border);
        clockAppDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        clockAppDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        clockAppDisplay.setScaleX(0.8);
        clockAppDisplay.setScaleY(0.8);

        root.setLeft(clockAppDisplay);
    }

    public void setMediaPlayerDisplay(MediaPlayerDisplay mediaPlayerDisplay){
        mediaPlayerDisplay.setBackground(Data.createBackGround());
        mediaPlayerDisplay.setBorder(border);
        mediaPlayerDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        mediaPlayerDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        mediaPlayerDisplay.setScaleX(0.8);
        mediaPlayerDisplay.setScaleY(0.8);

        mediaPlayerDisplay.getMediaView().setPreserveRatio(true);


        root.setLeft(mediaPlayerDisplay);
    }

    public void setMediaPlayerDisplay(Pane pane){
        pane.setBackground(Data.createBackGround());
        pane.setBorder(border);
        pane.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        pane.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        pane.setScaleX(0.8);
        pane.setScaleY(0.8);

        root.setLeft(pane);
    }

    public void prepareAlarms() throws IOException, ParseException {
        String allAlarms = getAlreadyOnFile();
        System.out.println(allAlarms);
        int counter = 0;
        String username = "";
        String day = "";
        String time = "";
        String desc = "";
        int alreadyIn = 0;
        for (int i = 0; i < allAlarms.length(); i++) {
            if(allAlarms.charAt(i)==';'&&counter==0){
                counter++;
                int counter1 = alreadyIn;
                while(counter1<i){
                    username+=allAlarms.charAt(alreadyIn + counter1++);
                }
                System.out.println("username = " + username);
            }else if(allAlarms.charAt(i)==';'&&counter<3){
                if(counter==1){
                    counter++;
                    int counter1 = alreadyIn + username.length()+1;
                    while(counter1<alreadyIn + username.length()+1 + 10){
                        day+=allAlarms.charAt(alreadyIn + counter1++);
                    }
                    System.out.println("day = " + day);
                }else if(counter==2){
                    counter++;
                    int counter1 = alreadyIn + username.length() + day.length() +2;
                    while(counter1<alreadyIn + username.length() + day.length() +2 + 12){
                        time+=allAlarms.charAt(alreadyIn + counter1++);
                    }
                    System.out.println("time = " + time);
                }
            }else if(allAlarms.charAt(i)=='\n'&&counter==3){
                int counter1 = (alreadyIn+username.length()+day.length()+time.length()+3);
                while(allAlarms.charAt(counter1)!='\n'){
                    if(allAlarms.charAt(counter1)!='\n'){
                        desc+=allAlarms.charAt(counter1);
                    }
                    counter1++;
                }
                System.out.println("desc = " + desc);
                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(day);
                if(username.equals(Data.getUsername())&&date1.equals(LocalDate.now())){
                    manageAlarm(time,desc);
                }
                alreadyIn+=(username.length()+day.length()+time.length()+desc.length()+3);
                counter = 0;
                username = "";
                day = "";
                time = "";
                desc = "";
            }
        }
    }

    private String getAlreadyOnFile() throws IOException {
        String res = "";
        FileReader fr=new FileReader("src\\DataBase\\alarm.txt");
        int i;
        while((i=fr.read())!=-1)
            res += ((char)i);
        fr.close();
        return res;
    }

    private void manageAlarm(String time,String desc){
        boolean alreadyIn = false;
        for (int i = 0; i < alarmsTime.size(); i++) {
            if(alarmsTime.get(i)[0].equals(time)&&alarmsTime.get(i)[1].equals(desc)){
                alreadyIn = true;
            }
        }
        if(!alreadyIn){
            alarmsTime.add(new String[]{time,desc});
        }
        //TODO create the alert at the time in question

    }

}