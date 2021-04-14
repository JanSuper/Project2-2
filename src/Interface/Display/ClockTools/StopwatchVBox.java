package Interface.Display.ClockTools;

import Interface.Screens.MainScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class StopwatchVBox extends VBox {
    private Label stopwatchTime;
    private HBox buttons;
    private ScrollPane lapsList;
    private Timeline stopwatchTimeline;
    private int minutesStopwatch = 0, secondsStopwatch = 0, millisStopwatch = 0;
    public Button startPause;
    public Button lapReset;
    public Label lap;
    private VBox laps;

    public StopwatchVBox() {
        setSpacing(40);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(80,0,20,0));

        createContent();
        getChildren().addAll(stopwatchTime, buttons, lapsList);
    }

    private void createContent() {
        stopwatchTime = new Label("00:00:000");
        stopwatchTime.setFont(Font.font("Tahoma", FontWeight.BOLD, 58));
        stopwatchTime.setTextFill(MainScreen.themeColor.darker().darker());
        stopwatchTime.setAlignment(Pos.CENTER);
        bindStopwatchLabelToTime(stopwatchTime);

        laps = new VBox(17);
        laps.setAlignment(Pos.CENTER);
        laps.setBackground(new Background(new BackgroundFill(new Color(0.1,0.1, 0.12, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));

        lapsList = new ScrollPane(laps);
        lapsList.setFitToWidth(true);
        lapsList.setMaxHeight(400);
        lapsList.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        lapsList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lapsList.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        lapsList.vvalueProperty().bind(laps.heightProperty());  //updating scrollPane

        lapReset = new Button("Lap");
        lapReset.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY.brighter(), new CornerRadii(90,true), Insets.EMPTY)));
        lapReset.setDisable(true);
        designStopwatchButton(lapReset);
        lapReset.setOnAction(e-> {
            if(lapReset.getText().equals("Reset")) {
                resetStopwatch();
            }
            else if(lapReset.getText().equals("Lap")) {
                lapStopwatch();
            }
        });

        startPause = new Button("Start");
        startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designStopwatchButton(startPause);
        startPause.setOnAction(e-> {
            if(startPause.getText().equals("Start")) {
                startStopwatch();
            }
            else if(startPause.getText().equals("Pause")) {
                pauseStopwatch();
            }
        });

        buttons = new HBox(60);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(lapReset, startPause);
    }

    public void lapStopwatch() {
        lap = new Label("Lap " + (laps.getChildren().size()+1) + "     " + stopwatchTime.getText());
        lap.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 18));
        lap.setTextFill(Color.LIGHTSLATEGRAY.brighter());
        laps.getChildren().add(lap);
    }

    public void startStopwatch() {
        stopwatchTimeline.play();

        startPause.setText("Pause");
        startPause.setBackground(new Background(new BackgroundFill(Color.OLIVE, new CornerRadii(90,true), Insets.EMPTY)));
        lapReset.setDisable(false);
        lapReset.setText("Lap");
    }

    public void pauseStopwatch() {
        stopwatchTimeline.pause();

        startPause.setText("Start");
        startPause.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        lapReset.setDisable(false);
        lapReset.setText("Reset");
    }

    public void resetStopwatch() {
        minutesStopwatch = 0;
        secondsStopwatch = 0;
        millisStopwatch = 0;
        stopwatchTimeline.pause();
        stopwatchTime.setText("00:00:000");

        lapReset.setText("Lap");
        lapReset.setDisable(true);
        laps.getChildren().clear();
    }

    private void designStopwatchButton(Button button) {
        button.setCursor(Cursor.HAND);
        button.setPrefSize(100, 60);
        button.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        button.setTextFill(Color.LIGHTGRAY);
        button.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(90,true), new BorderWidths(2))));
        button.setAlignment(Pos.CENTER);
    }

    private void bindStopwatchLabelToTime(Label stopwatchTime) {
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            if(millisStopwatch == 1000) {
                secondsStopwatch++;
                millisStopwatch = 0;
            }
            if(secondsStopwatch == 60) {
                minutesStopwatch++;
                secondsStopwatch = 0;
            }
            stopwatchTime.setText((((minutesStopwatch /10) == 0) ? "0" : "") + minutesStopwatch + ":"
                    + (((secondsStopwatch /10) == 0) ? "0" : "") + secondsStopwatch + ":"
                    + (((millisStopwatch /10) == 0) ? "00" : (((millisStopwatch /100) == 0) ? "0" : "")) + millisStopwatch++);
        }));
        stopwatchTimeline.setCycleCount(Timeline.INDEFINITE);
        stopwatchTimeline.setAutoReverse(false);
    }
}
