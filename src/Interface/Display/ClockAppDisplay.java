package Interface.Display;

import Interface.Screens.MainScreen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import javax.swing.event.ChangeListener;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClockAppDisplay extends VBox {
    private HBox tabs;
    private Button prevTab;
    private Timeline stopwatchTimeline;
    private int minutes = 0, secs = 0, millis = 0;

    private MainScreen mainScreen;

    public ClockAppDisplay(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        setBackground(new Background(new BackgroundFill(new Color(0.08,0.12, 0.15, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));

        setTabs();
        getChildren().add(tabs);
    }

    public void setTabs() {
        tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        tabs.setPrefHeight(80);
        tabs.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        Button alarm = new Button("Alarm");
        designTab(alarm);

        Button clock = new Button("Clock");
        designTab(clock);

        Button timer = new Button("Timer");
        designTab(timer);

        Button stopwatch = new Button("Stopwatch");
        designTab(stopwatch);

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.CENTER);
        exit.setTranslateY(-17);
        exit.setTranslateX(-2);
        exit.setOnAction(e -> mainScreen.setOptionsMenu());

        selectTab(alarm);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        tabs.getChildren().addAll(alarm, clock, timer, stopwatch, region, exit);
        }

    private void designTab(Button tab) {
        tab.setCursor(Cursor.HAND);
        tab.setBackground(Background.EMPTY);
        tab.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        tab.setTextFill(Color.LIGHTGRAY);
        tab.setPrefSize(160, 80);
        tab.setAlignment(Pos.CENTER);
        tab.setOnAction(e -> {deselectTab(prevTab); selectTab(tab);});
    }

    private void selectTab(Button selectedTab) {
        prevTab = selectedTab;
        selectedTab.setBackground(new Background(new BackgroundFill(MainScreen.themeColor.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        selectedTab.setTextFill(Color.LIGHTGRAY.brighter());
        selectedTab.setBorder(new Border(new BorderStroke(Color.LIGHTSLATEGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        if(getChildren().size() == 2) {
            getChildren().remove(1);
        }
        switch(selectedTab.getText()) {
            case "Alarm": setAlarmView(); break;
            case "Clock": setClockView(); break;
            case "Timer": setTimerView(); break;
            case "Stopwatch": setStopwatchView(); break;
        }
    }

    private void deselectTab(Button prevTab) {
        if(prevTab.getText().equals("Stopwatch")) {
            minutes = 0;
            secs = 0;
            millis = 0;
            stopwatchTimeline.pause();
        }

        prevTab.setBackground(Background.EMPTY);
        prevTab.setTextFill(Color.LIGHTGRAY);
        prevTab.setBorder(null);
    }

    private void setAlarmView() {   //TODO
    }

    private void setClockView() {
        VBox clockVBox = new VBox(20);
        clockVBox.setAlignment(Pos.CENTER);
        clockVBox.setPadding(new Insets(140,0,0,0));

        Label digitalClock = new Label();
        digitalClock.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        digitalClock.setTextFill(MainScreen.themeColor.darker().darker());
        digitalClock.setAlignment(Pos.CENTER);
        bindClockLabelToTime(digitalClock);

        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        int dayOfMonth = currentDate.getDayOfMonth();
        Month month = currentDate.getMonth();

        Label dateLabel = new Label(dayOfWeek.toString()+", "+dayOfMonth+" "+month);
        dateLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        dateLabel.setTextFill(MainScreen.themeColor.darker());
        dateLabel.setAlignment(Pos.CENTER);

        clockVBox.getChildren().addAll(digitalClock, dateLabel);
        getChildren().add(clockVBox);
    }
    private int getTime(String time,int nbr){
        char x = 'e'; char y = 'e';
        int counter = 0;
        for (int i = 0;i<time.length();i++){
            if(time.charAt(i)==':'){
                x = time.charAt(i-2-counter+nbr);
                y = time.charAt(i-1-counter+nbr);
                counter++;
            }
        }
        x = (char)(x + y);
        return x;
    }

    private void setTimerView() {
        VBox timerVBox = new VBox(40);
        timerVBox.setAlignment(Pos.CENTER);
        timerVBox.setPadding(new Insets(80,0,80,0));

        TextField timerTime = new TextField("00:00:00");
        timerTime.setPrefWidth(10);
        timerTime.setFont(Font.font("Tahoma", FontWeight.BOLD, 58));
        //timerTime.setTextFormatter(MainScreen.themeColor.darker().darker());
        timerTime.setAlignment(Pos.CENTER);
        minutes = getTime(timerTime.getText(),0);
        secs = getTime(timerTime.getText(),1);
        millis = getTime(timerTime.getText(),2);
        bindStopwatchLabelToTime(timerTime);

        Button startPause = new Button("Start");
        Button stop = new Button("Stop");
        stop.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY.brighter(), new CornerRadii(90,true), Insets.EMPTY)));
        designStopwatchButton(stop);
        stop.setOnAction(e-> {
            minutes = 0;
            secs = 0;
            millis = 0;
            stopwatchTimeline.pause();
            startPause.setText("Start");
            startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
            timerTime.setText("00:00:000");
        });

        startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designStopwatchButton(startPause);
        startPause.setOnAction(e-> {
            if(startPause.getText().equals("Start")) {
                minutes = getTime(timerTime.getText(),0);
                secs = getTime(timerTime.getText(),1);
                millis = getTime(timerTime.getText(),2);
                bindStopwatchLabelToTime(timerTime);
                stopwatchTimeline.play();

                startPause.setText("Pause");
                startPause.setBackground(new Background(new BackgroundFill(Color.OLIVE, new CornerRadii(90,true), Insets.EMPTY)));
            }
            else if(startPause.getText().equals("Pause")) {
                stopwatchTimeline.pause();
                startPause.setText("Start");
                startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
            }
        });

        HBox buttons = new HBox(60);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(stop, startPause);

        timerVBox.getChildren().addAll(timerTime, buttons);
        getChildren().add(timerVBox);
    }

    private void setStopwatchView() {
        VBox stopwatchVBox = new VBox(40);
        stopwatchVBox.setAlignment(Pos.CENTER);
        stopwatchVBox.setPadding(new Insets(80,0,80,0));

        Label stopwatchTime = new Label("00:00:000");
        stopwatchTime.setFont(Font.font("Tahoma", FontWeight.BOLD, 58));
        stopwatchTime.setTextFill(MainScreen.themeColor.darker().darker());
        stopwatchTime.setAlignment(Pos.CENTER);
        bindStopwatchLabelToTime(stopwatchTime);

        VBox laps = new VBox(17);
        laps.setAlignment(Pos.CENTER);
        laps.setBackground(new Background(new BackgroundFill(new Color(0.1,0.1, 0.12, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));

        ScrollPane lapsList = new ScrollPane(laps);
        lapsList.setFitToWidth(true);
        lapsList.setMaxHeight(300);
        lapsList.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        lapsList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lapsList.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        lapsList.vvalueProperty().bind(laps.heightProperty());  //updating scrollPane

        Button lapReset = new Button("Lap");
        lapReset.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY.brighter(), new CornerRadii(90,true), Insets.EMPTY)));
        lapReset.setDisable(true);
        designStopwatchButton(lapReset);
        lapReset.setOnAction(e-> {
            if(lapReset.getText().equals("Reset")) {
                minutes = 0;
                secs = 0;
                millis = 0;
                stopwatchTimeline.pause();
                stopwatchTime.setText("00:00:000");

                lapReset.setText("Lap");
                lapReset.setDisable(true);
                laps.getChildren().clear();
            }
            else if(lapReset.getText().equals("Lap")) {
                Label lap = new Label("Lap " + (laps.getChildren().size()+1) + "     " + stopwatchTime.getText());
                lap.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 18));
                lap.setTextFill(Color.LIGHTSLATEGRAY.brighter());
                laps.getChildren().add(lap);
            }
        });

        Button startPause = new Button("Start");
        startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designStopwatchButton(startPause);
        startPause.setOnAction(e-> {
            if(startPause.getText().equals("Start")) {
                stopwatchTimeline.play();

                startPause.setText("Pause");
                startPause.setBackground(new Background(new BackgroundFill(Color.OLIVE, new CornerRadii(90,true), Insets.EMPTY)));
                lapReset.setDisable(false);
                lapReset.setText("Lap");
            }
            else if(startPause.getText().equals("Pause")) {
                stopwatchTimeline.pause();

                startPause.setText("Start");
                startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
                lapReset.setDisable(false);
                lapReset.setText("Reset");
            }
        });

        HBox buttons = new HBox(60);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(lapReset, startPause);

        stopwatchVBox.getChildren().addAll(stopwatchTime, buttons, lapsList);
        getChildren().add(stopwatchVBox);
    }

    private void designStopwatchButton(Button button) {
        button.setCursor(Cursor.HAND);
        button.setPrefSize(100, 60);
        button.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        button.setTextFill(Color.LIGHTGRAY);
        button.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(90,true), new BorderWidths(2))));
        button.setAlignment(Pos.CENTER);
    }

    private void bindClockLabelToTime(Label digitalClock) {
        //digital clock updates per second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
            actionEvent -> {
                Calendar time = Calendar.getInstance();
                String hourString = pad(2, ' ', time.get(Calendar.HOUR) == 0 ? "12" : time.get(Calendar.HOUR) + "");
                String minuteString = pad(2, '0', time.get(Calendar.MINUTE) + "");
                String secondString = pad(2, '0', time.get(Calendar.SECOND) + "");
                String ampmString = time.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                digitalClock.setText(hourString + ":" + minuteString + ":" + secondString + " " + ampmString);
            }), new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void bindStopwatchLabelToTime(Label stopwatchTime) {
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            if(millis == 1000) {
                secs++;
                millis = 0;
            }
            if(secs == 60) {
                minutes++;
                secs = 0;
            }
            stopwatchTime.setText((((minutes /10) == 0) ? "0" : "") + minutes + ":"
                    + (((secs/10) == 0) ? "0" : "") + secs + ":"
                    + (((millis/10) == 0) ? "00" : (((millis/100) == 0) ? "0" : "")) + millis++);
        }));
        stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        stopwatchTimeline.setAutoReverse(false);
    }

    private void bindStopwatchLabelToTime(TextField timer) {
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            if(millis == 1000) {
                secs--;
                millis = 0;
            }
            if(secs == 60) {
                minutes--;
                secs = 0;
            }
            timer.setText((((minutes /10) == 0) ? "0" : "") + minutes + ":"
                    + (((secs/10) == 0) ? "0" : "") + secs + ":"
                    + (((millis/10) == 0) ? "00" : (((millis/100) == 0) ? "0" : "")) + millis++);
        }));
        stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        stopwatchTimeline.setAutoReverse(false);
    }


    //returns padded string from specified width
    public static String pad(int fieldWidth, char padChar, String s) {
        return String.valueOf(padChar).repeat(Math.max(0, fieldWidth - s.length())) + s;
    }
}
