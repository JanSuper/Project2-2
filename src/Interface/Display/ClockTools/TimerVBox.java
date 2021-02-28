package Interface.Display.ClockTools;

import Interface.Screens.MainScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TimerVBox extends VBox {
    private Timeline timerTimeline;
    private int hoursTimer = 0; int minutesTimer = 0; int secondsTimer = 0;
    private Label timerTime;
    private HBox plus; HBox minus;
    private Button startPauseResume; Button cancel;
    private HBox buttons;

    public TimerVBox() {
        setSpacing(40);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));

        createContent();
        getChildren().addAll(plus, timerTime, minus, buttons);
    }

    private void createContent() {
        timerTime = new Label();
        timerTime.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        timerTime.setTextFill(MainScreen.themeColor.darker().darker());
        timerTime.setAlignment(Pos.CENTER);
        setTimerTime();
        bindTimerLabelToTime();

        plus = new HBox(70);
        setPlusButtons(plus);
        minus = new HBox(80);
        setMinusButtons(minus);
        disablePlusMinus(false, plus, minus);

        cancel = new Button("Cancel");
        cancel.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY.brighter(), new CornerRadii(90,true), Insets.EMPTY)));
        cancel.setDisable(true);
        designTimerButton(cancel);
        cancel.setOnAction(e-> {
            hoursTimer = 0;
            minutesTimer = 0;
            secondsTimer = 0;
            timerTimeline.stop();
            setTimerTime();
            resetTimerButtons();
        });

        startPauseResume = new Button("Start");
        startPauseResume.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designTimerButton(startPauseResume);
        startPauseResume.setOnAction(e-> {
            disablePlusMinus(true, plus, minus);
            switch (startPauseResume.getText()) {
                case "Start":
                    timerTimeline.play();

                    startPauseResume.setText("Pause");
                    startPauseResume.setBackground(new Background(new BackgroundFill(Color.OLIVE, new CornerRadii(90, true), Insets.EMPTY)));
                    cancel.setDisable(false);
                    break;
                case "Pause":
                    timerTimeline.pause();

                    startPauseResume.setText("Resume");
                    startPauseResume.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90, true), Insets.EMPTY)));
                    break;
                case "Resume":
                    timerTimeline.play();

                    startPauseResume.setText("Pause");
                    startPauseResume.setBackground(new Background(new BackgroundFill(Color.OLIVE, new CornerRadii(90, true), Insets.EMPTY)));
                    break;
            }
        });

        buttons = new HBox(60);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(cancel, startPauseResume);
    }

    private void disablePlusMinus(boolean b, HBox plus, HBox minus) {
        for(int i = 0; i<3; i++) {
            plus.getChildren().get(i).setDisable(b);
            minus.getChildren().get(i).setDisable(b);
        }
    }

    private void setPlusButtons(HBox plus) {
        Button plusH = new Button("+");
        designPlusMinusButton(plusH);
        plusH.setOnAction(e -> {hoursTimer++; setTimerTime();});

        Button plusM = new Button("+");
        designPlusMinusButton(plusM);
        plusM.setOnAction(e -> {if (minutesTimer==59) {minutesTimer=0;} else {minutesTimer++;} setTimerTime();});

        Button plusS = new Button("+");
        designPlusMinusButton(plusS);
        plusS.setOnAction(e -> {if (secondsTimer==59) {secondsTimer=0;} else {secondsTimer++;} setTimerTime();});

        plus.setAlignment(Pos.CENTER);
        plus.setTranslateY(28);
        plus.getChildren().addAll(plusH, plusM, plusS);
    }

    private void setMinusButtons(HBox minus) {
        Button minusH = new Button("_");
        designPlusMinusButton(minusH);
        minusH.setOnAction(e -> {if(hoursTimer>0){hoursTimer--; setTimerTime();}});

        Button minusM = new Button("_");
        designPlusMinusButton(minusM);
        minusM.setOnAction(e -> {if (minutesTimer==0) {minutesTimer=59;} else {minutesTimer--;} setTimerTime();});

        Button minusS = new Button("_");
        designPlusMinusButton(minusS);
        minusS.setOnAction(e -> {if (secondsTimer==0) {secondsTimer=59;} else {secondsTimer--;} setTimerTime();});

        minus.setAlignment(Pos.CENTER);
        minus.setTranslateY(-31);
        minus.getChildren().addAll(minusH, minusM, minusS);
    }

    private void designPlusMinusButton(Button button) {
        button.setCursor(Cursor.HAND);
        button.setBackground(Background.EMPTY);
        button.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 28));
        button.setTextFill(Color.DARKVIOLET.darker().darker());
        button.setBorder(null);
        button.setAlignment(Pos.CENTER);
    }

    private void designTimerButton(Button button) {
        button.setCursor(Cursor.HAND);
        button.setUnderline(true);
        button.setPrefSize(90, 62);
        button.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));
        button.setTextFill(Color.LIGHTGRAY);
        button.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(90,true), new BorderWidths(2))));
        button.setAlignment(Pos.CENTER);
    }

    private void bindTimerLabelToTime() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timerTime.getText().equals("00 : 00 : 00")) {
                resetTimerButtons();
                timerTimeline.stop();
                notifyUser();
            }
            else {
                if(minutesTimer == 0 & secondsTimer == 0 & hoursTimer > 0) {
                    hoursTimer--;
                    minutesTimer =59;
                    secondsTimer =59;
                }
                else if (secondsTimer == 0 && minutesTimer > 0) {
                    minutesTimer--;
                    secondsTimer = 59;
                }
                else if(secondsTimer > 0) {
                    secondsTimer--;
                }
                setTimerTime();
            }}
        ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void notifyUser() { //TODO add sound
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

        Label timerLabel = new Label("Timer");
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

        Label label = new Label("Time's up! ");
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        label.setTextFill(Color.WHITESMOKE);
        label.setAlignment(Pos.CENTER);

        notification.getChildren().addAll(topBox, label);
    }

    private void resetTimerButtons() {
        startPauseResume.setText("Start");
        startPauseResume.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90, true), Insets.EMPTY)));
        disablePlusMinus(false, plus, minus);
        cancel.setDisable(true);
    }

    private void setTimerTime() {
        timerTime.setText(twoDigitString(hoursTimer)+" : "+twoDigitString(minutesTimer)+" : "+twoDigitString(secondsTimer));
    }

    private String twoDigitString(long number) {
        if (number == 0) { return "00"; }
        if (number / 10 == 0) { return "0" + number; }
        return String.valueOf(number);
    }
}
