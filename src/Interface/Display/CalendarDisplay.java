package Interface.Display;

import Interface.Display.ClockTools.AlarmVBox;
import Interface.Screens.MainScreen;
import Skills.Schedule.Course;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javafx.scene.paint.Color.LIGHTGRAY;


public class CalendarDisplay extends HBox {
    private final Duration period = Duration.ofMinutes(15);
    private final LocalTime beginningOfTheDay = LocalTime.of(00, 00);
    private final LocalTime endOfTheDay = LocalTime.of(23, 59);

    private LocalDate firstDate;
    private LocalDate lastDate;

    private final List<Slot> slots = new ArrayList<>();
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private MainScreen mainScreen;

    private AlarmVBox alarmVBox;
    private GridPane calendar;
    private ScrollPane scrollPane;

    private final int NBR_OF_DAYS = 10;

    private Skill_Schedule skill_schedule;

    public CalendarDisplay(MainScreen mainScreen) throws ParseException {
        this.mainScreen = mainScreen;
        this.skill_schedule = new Skill_Schedule();

        createContent();
        addSchedule();
    }

    private void createContent(){
        calendar = new GridPane();
        calendar.setGridLinesVisible(true);
        calendar.setStyle("-fx-background-color:#3d3d3d;");

        LocalDate today = LocalDate.now();
        firstDate = today.minusDays(NBR_OF_DAYS/2);
        lastDate = firstDate.plusDays(NBR_OF_DAYS);

        for (LocalDate day = firstDate; !day.isAfter(lastDate); day = day.plusDays(1)) {
            int slotIndex = 1;

            for (LocalDateTime startTime = day.atTime(beginningOfTheDay);
                 !startTime.isAfter(day.atTime(endOfTheDay));
                 startTime = startTime.plus(period)) {

                Slot slot = new Slot(startTime, period);
                slots.add(slot);

                calendar.add(slot.getView(), slot.getBeginning().getDayOfYear(), slotIndex++);
            }
        }

        DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("E\nMMM d");

        for (LocalDate date = firstDate; !date.isAfter(lastDate); date = date.plusDays(1)) {
            Label label = new Label(date.format(dFormatter));
            label.setPadding(new Insets(1));
            label.setTextAlignment(TextAlignment.CENTER);

            label.setTextFill(LIGHTGRAY);
            GridPane.setHalignment(label, HPos.CENTER);
            calendar.add(label, date.getDayOfYear(), 0);
        }

        int slotIndex = 1;
        DateTimeFormatter tFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (LocalDateTime startTime = today.atTime(beginningOfTheDay);
             !startTime.isAfter(today.atTime(endOfTheDay));
             startTime = startTime.plus(period)) {
            Label label = new Label(startTime.format(tFormatter));
            label.setPadding(new Insets(2));
            label.setTextFill(LIGHTGRAY);

            GridPane.setHalignment(label, HPos.RIGHT);
            calendar.add(label, 0, slotIndex);
            slotIndex++;
        }

        scrollPane = new ScrollPane(calendar);
        getChildren().add(scrollPane);

        alarmVBox = new AlarmVBox(this.mainScreen,true);
        getChildren().add(alarmVBox);
    }

    private void addSchedule() throws ParseException {
        ArrayList<Course> courses = skill_schedule.getInInterval(firstDate,lastDate);
        for (Course course:courses) {
            String desc = course.getSummary();
            LocalDate date =new SimpleDateFormat("yyyyMMdd").parse(course.getDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime time =new SimpleDateFormat("HHmmss").parse(course.getStart_Time()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime time1 =new SimpleDateFormat("HHmmss").parse(course.getEnd_Time()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            addReminder(desc,date,time,time1,Color.BLUEVIOLET);
        }
    }

    public void addReminder(String desc,LocalDate date,LocalTime fromTime,LocalTime toTime,Color color){
        BackgroundFill backgroundFill =
                new BackgroundFill(
                        color,
                        new CornerRadii(10),
                        new Insets(1)
                );
        Background background = new Background(backgroundFill);

        int[] inTable = convertDateToTable(date,fromTime,toTime);
        if(inTable!=null){
            Pane pane1  = new Pane();
            pane1.setBackground(background);
            pane1.setCursor(Cursor.HAND);
            pane1.setOnMouseClicked(event -> {
                getReminderInfo(desc,date.toString(),fromTime.toString());
            });
            calendar.add(pane1,inTable[0],inTable[1],1,inTable[2]);

            Text text = new Text(desc);
            text.setDisable(true);
            text.setTextOrigin(VPos.CENTER);
            text.setFill(LIGHTGRAY);
            text.setWrappingWidth(80);
            calendar.add(text, inTable[0], inTable[1], 1, inTable[2]);
        }
    }

    /**
     *
     * @param date
     * @param fromTime
     * @param toTime
     * @return [columnIndex, rowIndex, nbr of cell used in each row] in table
     */
    private int[] convertDateToTable(LocalDate date,LocalTime fromTime,LocalTime toTime){
        int col = 1; int row = 1; int rowSpan = 1;
        if(date.isAfter(firstDate.minusDays(1)) && date.isBefore(lastDate.plusDays(1))){
            Period datePeriod = Period.between(firstDate, date);
            col = datePeriod.getDays()+firstDate.getDayOfYear();

            long timePeriod = Duration.between(beginningOfTheDay, fromTime).toMinutes();
            row = (int) (timePeriod/period.toMinutes())+1;

            long duration = Math.abs(Duration.between(fromTime, toTime).toMinutes());
            if(duration<=period.toMinutes()){
                rowSpan = 1;
            }else{
                rowSpan = (int) (duration/period.toMinutes());
            }
        }else{
            mainScreen.chat.receiveMessage("The date you entered is not contained in the date interval of the calendar.");
            return null;
        }

        return new int[]{col,row,rowSpan};
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

    public VBox getCalendarShortcut(ArrayList<String> info) {
        VBox calendarShortcut = new VBox(20);

        Label fromCalendar = new Label("From Calendar:");
        fromCalendar.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        calendarShortcut.getChildren().add(fromCalendar);

        if (info.isEmpty()) {
            Label none = new Label("Nothing planned today!");
            none.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 16));
            none.setWrapText(true);
            none.setTextFill(Color.DARKRED.darker());
            none.setTranslateY(20);
            none.setAlignment(Pos.CENTER);
            none.setTextAlignment(TextAlignment.CENTER);
            calendarShortcut.getChildren().add(none);
        }
        else {
            int numOfEvents = info.size();
            int count = 0;
            int remaining = info.size();
            for (int i = 0; i<numOfEvents; i++) {
                count=count+1;
                if (count<=2) {
                    Label time = new Label(info.get(i).substring(0,5) + "- " + info.get(i).substring(6,11));
                    time.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
                    time.setTextFill(Color.WHITESMOKE);
                    time.setWrapText(true);
                    time.setMaxWidth(46);
                    time.setMaxWidth(46);

                    String desc = info.get(i).substring(12);
                    if (desc.length()>16) {
                        desc = desc.substring(0, 15) + "...";
                    }
                    Label description = new Label(desc);
                    description.setAlignment(Pos.CENTER);
                    description.setTextAlignment(TextAlignment.LEFT);
                    description.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
                    description.setTextFill(Color.WHITESMOKE);
                    description.setWrapText(true);
                    description.setMaxWidth(115);
                    description.setMaxWidth(115);

                    HBox event = new HBox(20);
                    event.setAlignment(Pos.CENTER_LEFT);
                    event.setStyle("-fx-background-color:#3d3d3d; -fx-background-insets: -7; -fx-background-radius: 10;");
                    event.getChildren().addAll(time, description);

                    calendarShortcut.getChildren().add(event);
                    remaining--;
                }
            }
            if (remaining>0) {
                Label l = new Label("+ " + remaining + " more today");
                l.setWrapText(true);
                l.setAlignment(Pos.CENTER);
                l.setTextAlignment(TextAlignment.CENTER);
                l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
                calendarShortcut.getChildren().add(l);
            }
        }
        return calendarShortcut;
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


        public LocalDateTime getBeginning() {
            return beginning;
        }

        public DayOfWeek getDayOfWeek() {
            return beginning.getDayOfWeek();
        }


        public Node getView() {
            return view;
        }

    }
}
