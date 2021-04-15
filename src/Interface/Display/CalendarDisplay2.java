package Interface.Display;

import Interface.Display.ClockTools.AlarmVBox;
import Interface.Screens.MainScreen;
import Interface.Screens.StartScreen;
import Skills.Schedule.Skill_Schedule;
import javafx.css.PseudoClass;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.LIGHTGRAY;

;
public class CalendarDisplay2 extends HBox {
    private final Duration period = Duration.ofMinutes(15);
    private final LocalTime beginningOfTheDay = LocalTime.of(00, 00);
    private final LocalTime endOfTheDay = LocalTime.of(23, 59);

    private final List<Slot> slots = new ArrayList<>();
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private MainScreen mainScreen;

    private AlarmVBox alarmVBox;
    private GridPane calendar;
    private ScrollPane scrollPane;

    public CalendarDisplay2(MainScreen mainScreen){
        this.mainScreen = mainScreen;

        createContent();
        addSchedule();
        getChildren().addAll(scrollPane,alarmVBox);
    }

    private void createContent(){
        calendar = new GridPane();
        calendar.setGridLinesVisible(true);
        calendar.setStyle("-fx-background-color:#3d3d3d;");

        LocalDate today = LocalDate.now();
        LocalDate startOfTheWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endingOfTheWeek = startOfTheWeek.plusDays(6);

        for (LocalDate day = startOfTheWeek; !day.isAfter(endingOfTheWeek); day = day.plusDays(1)) {
            int slotIndex = 1;

            for (LocalDateTime startTime = day.atTime(beginningOfTheDay);
                 !startTime.isAfter(day.atTime(endOfTheDay));
                 startTime = startTime.plus(period)) {

                Slot slot = new Slot(startTime, period);
                slots.add(slot);

                calendar.add(slot.getView(), slot.getDayOfWeek().getValue(), slotIndex++);
            }
        }

        DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("E\nMMM d");

        for (LocalDate date = startOfTheWeek; !date.isAfter(endingOfTheWeek); date = date.plusDays(1)) {
            Label label = new Label(date.format(dFormatter));
            label.setPadding(new Insets(1));
            label.setTextAlignment(TextAlignment.CENTER);

            label.setTextFill(Color.LIGHTGRAY);
            GridPane.setHalignment(label, HPos.CENTER);
            calendar.add(label, date.getDayOfWeek().getValue(), 0);
        }

        int slotIndex = 1;
        DateTimeFormatter tFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (LocalDateTime startTime = today.atTime(beginningOfTheDay);
             !startTime.isAfter(today.atTime(endOfTheDay));
             startTime = startTime.plus(period)) {
            Label label = new Label(startTime.format(tFormatter));
            label.setPadding(new Insets(2));
            label.setTextFill(Color.LIGHTGRAY);

            GridPane.setHalignment(label, HPos.RIGHT);
            calendar.add(label, 0, slotIndex);
            slotIndex++;
        }

        scrollPane = new ScrollPane();
        scrollPane.setContent(calendar);

        alarmVBox = new AlarmVBox(this.mainScreen,true);

        addReminder("this is a test","2021-03-07","00:00:00");
    }

    private void addSchedule(){
        //TODO add every courses of the schedule that are supposed to be in the calendar
    }

    public void addReminder(String desc,String date,String hour){
        BackgroundFill backgroundFill =
                new BackgroundFill(
                        Color.valueOf("#FF590081"),

                        new CornerRadii(10),
                        new Insets(1)
                );
        Background background = new Background(backgroundFill);

        Pane pane1  = new Pane();
        pane1.setBackground(background);
        //TODO be able to convert the string date to a cell in the calendar
        calendar.add(pane1,1,5,1,6);
        pane1.setCursor(Cursor.HAND);
        pane1.setOnMouseClicked(event -> {
            getReminderInfo(desc,date,hour);
        });

        Text text = new Text(desc);
        text.setDisable(true);
        text.setTextOrigin(VPos.CENTER);
        text.setFill(LIGHTGRAY);
        text.setWrappingWidth(80);
        calendar.add(text, 1, 5, 1, 6);
    }

    private void getReminderInfo(String desc,String date,String hour) {
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

        Label timerLabel = new Label(date + " at " + hour);
        timerLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 13));
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


    public static class Slot {

        private final LocalDateTime beginning;
        private final Duration period;
        private final Region view;

        public Slot(LocalDateTime beginning, Duration period) {

            this.beginning = beginning;
            this.period = period;

            view = new Region();
            view.setMinSize(80, 16);

        }


        public LocalTime getTime() {
            return beginning.toLocalTime();
        }


        public DayOfWeek getDayOfWeek() {
            return beginning.getDayOfWeek();
        }


        public Node getView() {
            return view;
        }

    }
}
