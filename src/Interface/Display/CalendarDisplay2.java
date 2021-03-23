package Interface.Display;

import Interface.Display.ClockTools.AlarmVBox;
import Interface.Screens.MainScreen;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.LIGHTGRAY;

;
public class CalendarDisplay2 extends HBox {
    private static final Paint WHITE = Color.LIGHTGRAY;
    private final LocalTime beginningOfTheDay = LocalTime.of(8, 00);
    private final Duration period = Duration.ofMinutes(30);
    private final LocalTime endOfTheDay = LocalTime.of(23, 59);

    private final List<Slot> slots = new ArrayList<>();
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private MainScreen mainScreen;

    public CalendarDisplay2(MainScreen mainScreen){
        this.mainScreen = mainScreen;

        GridPane calendar = new GridPane();
        //calendar.setStyle("-fx-background-color: #C0C0C0;");

        calendar.setGridLinesVisible(true);


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


                calendar.add(slot.getView(), slot.getDayOfWeek().getValue(), slotIndex);
                slotIndex++;
            }
        }


        DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("E\nMMM d");

        for (LocalDate date = startOfTheWeek; !date.isAfter(endingOfTheWeek); date = date.plusDays(1)) {
            Label label = new Label(date.format(dFormatter));
            label.setPadding(new Insets(1));
            label.setTextAlignment(TextAlignment.CENTER);

            label.setTextFill(WHITE);
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

            label.setTextFill(WHITE);


            GridPane.setHalignment(label, HPos.RIGHT);
            calendar.add(label, 0, slotIndex);
            slotIndex++;

        }
        Pane pane1  = new Pane();
        Pane pane2  = new Pane();
        Pane pane3  = new Pane();
        Pane pane4  = new Pane();
        Pane pane5  = new Pane();
        Pane pane6  = new Pane();

        BackgroundFill backgroundFill =
                new BackgroundFill(

                        Color.valueOf("#FF590081"),

                        new CornerRadii(10),
                        new Insets(1)
                );

        Background background =
                new Background(backgroundFill);
        calendar.add(pane1,1,5,1,6);
        calendar.add(pane3,2,7,1,4);
        calendar.add(pane2,1,13,1,4);

        calendar.add(pane5,4,8,1,13);
        calendar.add(pane6,5,7,1,4);
        pane1.setBackground(background);
        pane2.setBackground(background);
        pane3.setBackground(background);
        pane4.setBackground(background);
        pane5.setBackground(background);
        pane6.setBackground(background);


        // scene.setFill(Insets.Color.BLACK);
        Text text = new Text("KEN2430/2020-400/Lecture Mo/04 - Mathematical Modelling");
        calendar.add(text, 1, 4, 6, 6);

        text.setWrappingWidth(80);

        Text text2 = new Text("KEN2420/2020-400/Lecture Mo/04 - Theoretical Computer Science");
        //text2.setFill(Color.RED);
        calendar.add(text2, 1, 13, 4, 4);
        text2.setWrappingWidth(80);
        text2.setFill(LIGHTGRAY);

        Text text3 = new Text("KEN2420/2020-400/Lecture Tue/04 - Theoretical Computer Science");
        calendar.add(text3, 2, 7, 4, 4);
        text3.setWrappingWidth(80);
        text3.setFill(LIGHTGRAY);

        Text text4 = new Text("KEN2600/2020-003/Presentation First Phase/01 - Project 2-2 (The specific schedule will be announced by your supervisor)");
        calendar.add(text4, 4, 2, 10, 18);
        text4.setWrappingWidth(80);
        text4.setFill(LIGHTGRAY);

        Text text5 = new Text("KEN2420/2020-400/Lecture Fri/04 - Theoretical Computer Science");
        calendar.add(text5, 5, 7, 4, 4);
        text5.setWrappingWidth(80);
        text5.setFill(LIGHTGRAY);




        calendar.setStyle("-fx-background-color:#3d3d3d;");
        text.setTextOrigin(VPos.CENTER);
        text.setFill(LIGHTGRAY);

        ScrollPane scroller = new ScrollPane(calendar);
/*
        Scene scene = new Scene(scroller);
        scene.setFill(BLACK);


        primaryStage.setScene(scene);
        primaryStage.show();
*/
<<<<<<< Updated upstream
        AlarmVBox alarmVBox = new AlarmVBox(mainScreen,true);
        alarmVBox.setVisible(false);
        getChildren().addAll(calendar,alarmVBox);
    }


    public static class Slot {

        private final LocalDateTime beginning;
        private final Duration period;
        private final Region view;

        public Slot(LocalDateTime beginning, Duration period) {

            this.beginning = beginning;
            this.period = period;

            view = new Region();
            view.setMinSize(160, 40);

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
