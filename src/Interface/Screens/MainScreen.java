package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MainScreen {
    public ChatApp chat;
    public ClockAppDisplay clockAppDisplay;
    public WeatherDisplay weatherDisplay;
    public SkillEditorDisplay skillEditorDisplay;
    public BorderPane root;
    public CalendarDisplay calendarDisplay;
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
    private Timeline timeline;

    public MainScreen() throws Exception {
        borderWidth = 10;
        border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));
        chat = new ChatApp(Data.getUsername(),this);
        clockAppDisplay = new ClockAppDisplay(this);
        skillEditorDisplay = new SkillEditorDisplay(this);
        weatherDisplay = new WeatherDisplay(this);
        calendarDisplay = new CalendarDisplay(this);

        createContent();
        alarmsTime = new ArrayList<>();
        timeline = new Timeline();
        prepareAlarms();
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

        vBox = new VBox(40);
        setOptionsMenu();
    }

    public void setOptionsMenu() {
        userNameLabel = new Label(Data.getUsername());
        settings = new Button("Settings");
        help = new Button("Help");
        logOut = new Button("Log out");

        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 45)));
        userNameLabel.setStyle("-fx-text-fill: white");

        designOptionButton(settings);
        settings.setOnMouseClicked(event -> displaySettings());

        designOptionButton(help);
        help.setOnMouseClicked(event -> chat.receiveMessage("Tell me what to do for you. For example you can check the weather by typing \"How is the weather?\" or your UM schedule by typing \"Next Lecture\", \"This week Lecture\"."));

        designOptionButton(logOut);
        logOut.setOnMouseClicked(event -> {
            stage.close();
            Data.setImage("src/DataBase/defaultBackground.jpg");
            StartScreen startScreen= new StartScreen();
            try {
                startScreen.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        setMenuBackground();
        vBox.getChildren().setAll(userNameLabel, settings, help, logOut);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBorder(border);
        vBox.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        vBox.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        vBox.setScaleX(0.8);
        vBox.setScaleY(0.8);

        root.setLeft(vBox);
    }

    private void setMenuBackground() {
        if (themeColor.equals(Color.BLACK)) {
            vBox.setBackground(new Background(new BackgroundFill(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 0.4), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        else if (themeColor.equals(Color.LIGHTGRAY)) {
            vBox.setBackground(new Background(new BackgroundFill(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 0.29), CornerRadii.EMPTY, Insets.EMPTY)));
        }
        else { vBox.setBackground(new Background(new BackgroundFill(new Color(0.2, 0.35379, 0.65, 0.5), CornerRadii.EMPTY, Insets.EMPTY))); }
    }

    public void displaySettings() {
        Label settingsLabel = userNameLabel;
        settingsLabel.setText("Settings");

        Button editProf = settings;
        editProf.setText("Edit profile");

        Button themeColor = help;
        themeColor.setText("Theme Color");
        designOptionButton(themeColor);

        Button changeBackground = new Button("Background");
        designOptionButton(changeBackground);

        Button back = logOut;
        back.setText("Back");

        editProf.setOnMouseClicked(event -> chat.receiveMessage("You can change your password/location by typing \"Change my password/location/... to <YourNewPassword/YourNewLocation/...>\"."));
        themeColor.setOnMouseClicked(e-> displayThemeColors());
        changeBackground.setOnMouseClicked(event -> displayBackgroundEditing());
        back.setOnMouseClicked(event -> setOptionsMenu());

        vBox.getChildren().clear();
        vBox.getChildren().addAll(settingsLabel, editProf, themeColor,changeBackground, back);

        root.setLeft(vBox);
    }

    public void displayBackgroundEditing(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        try {
            if(selectedFile.toURI().toString().endsWith(".png")||selectedFile.toURI().toString().endsWith(".jpg")){
                String file = selectedFile.toPath().toString();
                Data.setImage(file);
                root.setBackground(Data.createBackGround());
            }else {
                chat.receiveMessage("The file "+selectedFile.toURI().toString()+" is not an image");
            }
        } catch(NullPointerException e){
            chat.receiveMessage("No file chosen");
        }
    }

    public void displayThemeColors() {
        Label themeColorLabel = userNameLabel;
        themeColorLabel.setText("Theme Color");

        HBox colors = new HBox(40);
        colors.setAlignment(Pos.CENTER);

        Color color = new Color(0.2, 0.35379, 0.65, 1);
        String colorString = "rgb(" + color.getRed() * 255 + "," + color.getGreen() * 255 + "," + color.getBlue() * 255 + ");";
        Button blue = new Button();
        blue.setStyle("-fx-border-radius: 5em; -fx-border-color:black; -fx-background-radius: 5em; -fx-min-width: 40px; -fx-min-height: 40px; -fx-max-width: 40px; -fx-max-height: 40px; -fx-background-color: "+ colorString +";");
        blue.setFocusTraversable(false);
        blue.setCursor(Cursor.HAND);

        Button black = new Button();
        black.setStyle("-fx-border-radius: 5em; -fx-border-color:white; -fx-background-radius: 5em; -fx-min-width: 40px; -fx-min-height: 40px; -fx-max-width: 40px; -fx-max-height: 40px; -fx-background-color: black;");
        black.setFocusTraversable(false);
        black.setCursor(Cursor.HAND);

        Button white = new Button();
        white.setStyle("-fx-border-radius: 5em; -fx-border-color:black; -fx-background-radius: 5em; -fx-min-width: 40px; -fx-min-height: 40px; -fx-max-width: 40px; -fx-max-height: 40px; -fx-background-color: lightgray;");
        white.setFocusTraversable(false);
        white.setCursor(Cursor.HAND);

        Button back = logOut;
        back.setText("Back");

        back.setOnMouseClicked(e -> displaySettings());
        blue.setOnMouseClicked(e -> {
            themeColor = new Color(0.2, 0.35379, 0.65, 1);
            try {
                chat.changeColor(themeColor);
                setMenuBackground();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        black.setOnMouseClicked(e -> {
            themeColor = Color.BLACK;
            try {
                chat.changeColor(themeColor);
                setMenuBackground();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        white.setOnMouseClicked(e -> {
            themeColor = Color.LIGHTGRAY;
            try {
                chat.changeColor(themeColor);
                setMenuBackground();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        colors.getChildren().addAll(blue, black, white);
        vBox.getChildren().clear();
        vBox.getChildren().addAll(themeColorLabel, colors, back);

        root.setLeft(vBox);
    }

    private void designOptionButton(Button button) {
        button.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(8,8,8,8,false), Insets.EMPTY)));
        button.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        button.setPrefWidth(250);
        button.setTextFill(Color.LIGHTGRAY);
        button.setCursor(Cursor.HAND);
    }

    public void setWeatherDisplay(String city, String country) throws Exception {
        weatherDisplay.setLocation(city, country);
        weatherDisplay.setSpacing(7);
        weatherDisplay.setBorder(border);
        weatherDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        weatherDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        weatherDisplay.setScaleX(0.8);
        weatherDisplay.setScaleY(0.8);

        root.setLeft(weatherDisplay);
    }

    public void setClockAppDisplay(String firstTab) {
        if (clockAppDisplay.prevTab != null) { clockAppDisplay.deselectTab(clockAppDisplay.prevTab); }
        switch(firstTab) {
            case "Alarm": clockAppDisplay.selectTab(clockAppDisplay.alarm); break;
            case "Clock": clockAppDisplay.selectTab(clockAppDisplay.clock); break;
            case "Timer": clockAppDisplay.selectTab(clockAppDisplay.timer); break;
            case "Stopwatch": clockAppDisplay.selectTab(clockAppDisplay.stopwatch); break;
        }
        clockAppDisplay.setBorder(border);
        clockAppDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        clockAppDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        clockAppDisplay.setScaleX(0.8);
        clockAppDisplay.setScaleY(0.8);

        root.setLeft(clockAppDisplay);
    }

    public void setSkillEditorAppDisplay(String firstTab) {
        if (skillEditorDisplay.prevTab != null) { skillEditorDisplay.deselectTab(skillEditorDisplay.prevTab); }
        if (firstTab.equals("Edit skill")) { skillEditorDisplay.selectTab(skillEditorDisplay.editSkill); }
        else { skillEditorDisplay.selectTab(skillEditorDisplay.addSkill); }

        skillEditorDisplay.setBorder(border);
        skillEditorDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        skillEditorDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        skillEditorDisplay.setScaleX(0.8);
        skillEditorDisplay.setScaleY(0.8);

        root.setLeft(skillEditorDisplay);
    }

    public void setMapDisplay(String type,String loc1,String loc2) throws Exception {
        MapDisplay mapDisplay = new MapDisplay(type,loc1,loc2);
        mapDisplay.setSpacing(7);
        mapDisplay.setBackground(Data.createBackGround());
        mapDisplay.setBorder(border);
        mapDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        mapDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        mapDisplay.setScaleX(0.8);
        mapDisplay.setScaleY(0.8);

        root.setLeft(mapDisplay);
    }


    public void displayUrlMediaPlayer(MediaPlayerDisplay mediaPlayerDisplay){
        mediaPlayerDisplay.setBackground(Data.createBackGround());
        mediaPlayerDisplay.setBorder(border);
        mediaPlayerDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        mediaPlayerDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        mediaPlayerDisplay.setScaleX(0.8);
        mediaPlayerDisplay.setScaleY(0.8);

        mediaPlayerDisplay.getMediaView().setPreserveRatio(true);


        root.setLeft(mediaPlayerDisplay);
    }

    public void displaySkill(Pane pane,String skill){
        pane.setBackground(Data.createBackGround());
        pane.setBorder(border);
        pane.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        pane.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        pane.setScaleX(0.8);
        pane.setScaleY(0.8);

        root.setLeft(pane);
    }

    public void prepareAlarms() throws IOException, ParseException {
        String allReminders = getAlreadyOnFile();
        int nbrOfInfo = 4;
        int counter = 0;
        String username = "";
        String day = "";
        String time = "";
        String time1 = "";
        String desc = "";
        int linesNbrChar = 0;
        for (int i = 0; i < allReminders.length(); i++) {
            if(allReminders.charAt(i)==';'&&counter<nbrOfInfo){
                if(counter==0){
                    int counter1 = linesNbrChar;
                    while(counter1<i){
                        username+=allReminders.charAt(counter1++);
                    }
                    //System.out.println("username = " + username);
                }else if(counter==1){
                    int counter1 = linesNbrChar+username.length()+1;
                    while(counter1<i){
                        day+=allReminders.charAt(counter1++);
                    }
                    //System.out.println("day = " + day);
                }else if(counter==2){
                    int counter1 = linesNbrChar+username.length() + day.length() +2;
                    while(counter1<i){
                        time+=allReminders.charAt(counter1++);
                    }
                    //System.out.println("time = " + time);
                }else if(counter==3){
                    int counter1 = linesNbrChar+username.length() + day.length()+time.length() +3;
                    while(counter1<i){
                        time1+=allReminders.charAt(counter1++);
                    }
                    //System.out.println("time1 = " + time);
                }
                counter++;
            }
            if(allReminders.charAt(i)=='\n'&&counter==nbrOfInfo){
                int counter1 = linesNbrChar+username.length()+day.length()+time.length()+time1.length()+4;
                while(allReminders.charAt(counter1)!='\n'){
                    desc+=allReminders.charAt(counter1);
                    counter1++;
                }

                //notify user if a reminder is for today
                String today = java.time.LocalDate.now().toString();
                if(username.equals(Data.getUsername())&&day.equals(today)){
                    chat.receiveMessage("Today, there will be the alarm from " + time + " to " + time1 + " with description \"" + desc + "\"");
                    alarmsTime.add(new String[]{time,desc});
                    displayReminderAtTime(time,desc);
                }
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(day,dateFormatter);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime localTime = LocalTime.parse(time,timeFormatter);
                LocalTime localTime1 = LocalTime.parse(time1,timeFormatter);
                //add any reminder in the calendar
                calendarDisplay.addReminder(desc,localDate,localTime,localTime1,Color.ORANGE);
                linesNbrChar=i+1;
                counter = 0;
                username = "";
                day = "";
                time = "";
                time1 = "";
                desc = "";
            }
        }
    }

    private String getAlreadyOnFile() throws IOException {
        String res = "";
        FileReader fr=new FileReader(Data.getRemindersFile());
        int i;
        while((i=fr.read())!=-1)
            res += ((char)i);
        fr.close();
        return res;
    }

    public void displayReminderAtTime(String time, String desc) throws ParseException {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(getTimeDiffInSec(time)), event -> notifyUser(time,desc));
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public int getTimeDiffInSec(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time);
        Date date2 = format.parse(java.time.LocalTime.now().toString());
        int difference = (int) (date2.getTime() - date1.getTime());
        if(date2.before(date1)){
            return -difference/1000;
        }
        return difference/1000;
    }

    private void notifyUser(String time,String desc) { //TODO add sound
        VBox notification = new VBox(40);
        notification.setAlignment(Pos.TOP_CENTER);
        notification.setPrefSize(300, 285);
        notification.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(7))));
        notification.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setOpacity(0.91);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(notification, 320, 190));
        stage.show();

        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2 - 280);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4 + 110);

        Label timerLabel = new Label("Reminder of " + time);
        timerLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 17));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setAlignment(Pos.TOP_LEFT);
        timerLabel.setTranslateX(15);

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 17));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.TOP_RIGHT);
        exit.setOnAction(e -> stage.close());

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox topBox = new HBox(60);
        topBox.setAlignment(Pos.CENTER);
        topBox.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));
        topBox.getChildren().addAll(timerLabel, region, exit);

        Label label = new Label(desc);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        label.setTextFill(Color.WHITESMOKE);
        label.setAlignment(Pos.CENTER);

        notification.getChildren().addAll(topBox, label);
    }

    public void exitWindow()
    {
        Platform.exit();
        System.exit(0);
    }
}