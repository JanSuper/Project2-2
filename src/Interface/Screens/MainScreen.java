package Interface.Screens;

import DataBase.Data;
import Interface.Chat.ChatApp;
import Interface.Display.*;
import OpenCV.FaceDetection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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
    public FaceDetection faceDetection;
    public StartScreen startScreen;

    public ChatApp chat;
    public ClockAppDisplay clockAppDisplay;
    public WeatherDisplay weatherDisplay;
    public SkillEditorDisplay skillEditorDisplay;
    public BorderPane root;
    public CalendarDisplay calendarDisplay;
    public Menu menu;
    public int borderWidth;
    public Border border;
    public static Color themeColor = new Color(0.2,0.35379, 0.65, 1);
    public Stage stage;

    public ArrayList<String> todaysRemindersShortcut = new ArrayList<>();
    private ArrayList<String[]> alarmsTime;
    private Timeline timeline;

    private boolean firstFaceViewed;

    public MainScreen(StartScreen startScreen,FaceDetection faceDetection,Stage stage) throws Exception {
        this.startScreen = startScreen;
        this.faceDetection = faceDetection;
        faceDetection.mainScreen = this;

        borderWidth = 10;
        border = new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(borderWidth)));

        chat = new ChatApp(Data.getUsername(),this);
        clockAppDisplay = new ClockAppDisplay(this);
        skillEditorDisplay = new SkillEditorDisplay(this);
        weatherDisplay = new WeatherDisplay(this);
        calendarDisplay = new CalendarDisplay(this);

        alarmsTime = new ArrayList<>();
        timeline = new Timeline();
        prepareReminders(calendarDisplay.firstDate,calendarDisplay.lastDate);

        createContent();

        this.stage = stage;
        start(this.stage);

        firstFaceViewed = false;
        manageFaceDetection();
    }

    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    public void createContent() throws Exception {
        root = new BorderPane();
        root.setBorder(border);
        root.setBackground(Data.createBackGround());

        chat.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        chat.prefWidthProperty().bind(root.widthProperty().divide(2.8));
        root.setRight(chat);

        menu = new Menu(this);
        setMenu("MainMenu");
    }

    public void manageFaceDetection(){
        final boolean[] faceDetected = {true};
        Task task = new Task<Void>() {
            @Override public Void call() throws InterruptedException {
                while (faceDetected[0]){
                    if(!firstFaceViewed&&faceDetection.faceDetected()){
                        //Assistant's first message when sees user for first time
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                chat.receiveMessage("Welcome " + Data.getUsername() + "! How may I help you?");
                            }
                        });
                        firstFaceViewed = true;
                    }
                    //refresh every 1sec
                    Thread.sleep(1000);
                    if(!faceDetection.faceDetected()){
                        //Manage face not detected
                        faceDetection.manageFaceLeaving();
                        faceDetected[0] = false;
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void setMenu(String menuString) throws Exception {
        switch (menuString){
            case "MainMenu": menu.displayMainMenu(); break;
            case "Shortcuts": menu.displayShortcutsMenu(); break;
            case "ThemeColors": menu.displayThemeColorsMenu(); break;
            case "Background": menu.displayBackgroundEditing(); break;
            case "Settings": menu.displaySettingsMenu(); break;
        }
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
        if (firstTab.equals("Add rule")) { skillEditorDisplay.selectTab(skillEditorDisplay.addRule); }
        else { skillEditorDisplay.selectTab(skillEditorDisplay.addSkill); }

        skillEditorDisplay.setBorder(border);
        skillEditorDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        skillEditorDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        skillEditorDisplay.setScaleX(0.8);
        skillEditorDisplay.setScaleY(0.8);

        root.setLeft(skillEditorDisplay);
    }

    public void setMapDisplay(String type,String loc1,String loc2) throws Exception {
        MapDisplay mapDisplay = new MapDisplay(this, type,loc1,loc2);
        mapDisplay.setBackground(Data.createBackGround());
        mapDisplay.setBorder(border);
        mapDisplay.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        mapDisplay.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        mapDisplay.setScaleX(0.8);
        mapDisplay.setScaleY(0.8);
        mapDisplay.myWebView.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2).subtract(45));

        root.setLeft(mapDisplay);
    }

    public void displayUrlMediaPlayer(MediaPlayerDisplay mediaPlayerDisplay){
        VBox mp = (VBox) addEscTo(mediaPlayerDisplay, true);
        mp.setBackground(Data.createBackGround());
        mp.setBorder(border);
        mp.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        mp.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        mp.setScaleX(0.8);
        mp.setScaleY(0.8);

        mediaPlayerDisplay.mediaView.setFitWidth(mp.getPrefWidth()-borderWidth*2);
        mediaPlayerDisplay.mediaView.setFitHeight(mp.getPrefHeight()-borderWidth*2);
        mediaPlayerDisplay.prefWidthProperty().bind(mp.widthProperty().subtract(borderWidth*2));
        mediaPlayerDisplay.prefHeightProperty().bind(mp.heightProperty().subtract(borderWidth*2));
        mediaPlayerDisplay.mediaView.setPreserveRatio(true);

        root.setLeft(mp);
    }

    public void displaySkill(Pane pane,String skill) {
        pane = (Pane) addEscTo(pane, false);
        pane.setBackground(Data.createBackGround());
        pane.setBorder(border);
        pane.prefHeightProperty().bind(root.heightProperty().subtract(borderWidth*2));
        pane.prefWidthProperty().bind(root.widthProperty().subtract(chat.prefWidthProperty()).subtract(borderWidth*2));
        pane.setScaleX(0.8);
        pane.setScaleY(0.8);

        root.setLeft(pane);
    }

    private Node addEscTo(Node node, Boolean isMediaPlayer) {
        VBox newPane = new VBox(0);

        HBox topBox = new HBox(0);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 17));
        exit.setTextFill(Color.WHITE);
        exit.setBorder(null);
        exit.setAlignment(Pos.TOP_RIGHT);
        exit.setOnAction(e -> {
            try {
                if (isMediaPlayer){Data.getMp().pause();}
                setMenu("MainMenu");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        topBox.getChildren().addAll(region, exit);
        newPane.getChildren().addAll(topBox, node);

        return newPane;
    }

    public void prepareReminders(LocalDate firstDate, LocalDate lastDate) throws IOException, ParseException {
        String allReminders = getAlreadyOnFile();
        int nbrOfInfo = 5;
        int counter = 0;
        String username = "";
        String day = "";
        String time = "";
        String time1 = "";
        String color = "";
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
                }else if(counter==4){
                    int counter1 = linesNbrChar+username.length() + day.length()+time.length()+time1.length() +4;
                    while(counter1<i){
                        color+=allReminders.charAt(counter1++);
                    }
                    //System.out.println("color  = " + color);
                }
                counter++;
            }
            if(allReminders.charAt(i)=='\n'&&counter==nbrOfInfo){
                int counter1 = linesNbrChar+username.length()+day.length()+time.length()+time1.length()+color.length()+nbrOfInfo;
                while(allReminders.charAt(counter1)!='\n'){
                    desc+=allReminders.charAt(counter1);
                    counter1++;
                }

                //notify user if a reminder is for today
                String today = java.time.LocalDate.now().toString();
                if(username.equals(Data.getUsername())&&day.equals(today)){
                    todaysRemindersShortcut.add(time.substring(0,5) +";"+ time1.substring(0,5) +";"+ desc);
                    alarmsTime.add(new String[]{time,desc});
                    displayReminderAtTime(time,desc);
                }
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(day,dateFormatter);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime localTime = LocalTime.parse(time,timeFormatter);
                LocalTime localTime1 = LocalTime.parse(time1,timeFormatter);
                //add any reminder in the calendar
                if(localDate.isAfter(firstDate.minusDays(1)) && localDate.isBefore(lastDate.plusDays(1))) {
                    calendarDisplay.addReminder(desc, localDate, localTime, localTime1, Color.valueOf(color));
                }
                linesNbrChar=i+1;
                counter = 0;
                username = "";
                day = "";
                time = "";
                time1 = "";
                color = "";
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