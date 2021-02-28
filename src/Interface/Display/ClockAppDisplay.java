package Interface.Display;

import Interface.Display.ClockTools.ClockVBox;
import Interface.Display.ClockTools.StopwatchVBox;
import Interface.Display.ClockTools.TimerVBox;
import Interface.Screens.MainScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ClockAppDisplay extends VBox {
    private HBox tabs;
    private Button prevTab;
    private TimerVBox timerVBox;
    private StopwatchVBox stopwatchVBox;

    private MainScreen mainScreen;

    public ClockAppDisplay(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        timerVBox = new TimerVBox();

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
            stopwatchVBox.resetStopwatch();
        }

        prevTab.setBackground(Background.EMPTY);
        prevTab.setTextFill(Color.LIGHTGRAY);
        prevTab.setBorder(null);
    }

    private void setAlarmView() {   //TODO
    }

    private void setClockView() {
        ClockVBox clockVBox = new ClockVBox();
        getChildren().add(clockVBox);
    }

    private void setTimerView() {
        getChildren().add(timerVBox);
    }

    private void setStopwatchView() {
        stopwatchVBox = new StopwatchVBox();
        getChildren().add(stopwatchVBox);
    }
}