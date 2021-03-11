package Interface.Display;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarDisplay2 extends Application {
    private final LocalTime beginningOfTheDay = LocalTime.of(8, 00);
    private final Duration period = Duration.ofMinutes(30);
    private final LocalTime endOfTheDay = LocalTime.of(23, 59);

    private final List<Slot> slots = new ArrayList<>();
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane calendar = new GridPane();
        calendar.setGridLinesVisible(true);

        LocalDate today = LocalDate.now();
        LocalDate startOfTheWeek = today.minusDays(today.getDayOfWeek().getValue() - 1) ;
        LocalDate endingOfTheWeek = startOfTheWeek.plusDays(6);
        
        for (LocalDate day = startOfTheWeek; ! day.isAfter(endingOfTheWeek); day = day.plusDays(1)) {
            int slotIndex = 1 ;

            for (LocalDateTime startTime = day.atTime(beginningOfTheDay);
                 ! startTime.isAfter(day.atTime(endOfTheDay));
                 startTime = startTime.plus(period)) {


                Slot slot = new Slot(startTime, period);
                slots.add(slot);


                calendar.add(slot.getView(), slot.getDayOfWeek().getValue(), slotIndex);
                slotIndex++ ;
            }
        }


        DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("E\nMMM d");

        for (LocalDate date = startOfTheWeek; ! date.isAfter(endingOfTheWeek); date = date.plusDays(1)) {
            Label label = new Label(date.format(dFormatter));
            label.setPadding(new Insets(1));
            label.setTextAlignment(TextAlignment.CENTER);
            GridPane.setHalignment(label, HPos.CENTER);
            calendar.add(label, date.getDayOfWeek().getValue(), 0);
        }

        int slotIndex = 1 ;
        DateTimeFormatter tFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (LocalDateTime startTime = today.atTime(beginningOfTheDay);
             ! startTime.isAfter(today.atTime(endOfTheDay));
             startTime = startTime.plus(period)) {
            Label label = new Label(startTime.format(tFormatter));
            label.setPadding(new Insets(2));

            GridPane.setHalignment(label, HPos.RIGHT);
            calendar.add(label, 0, slotIndex);
            slotIndex++ ;
        }

        ScrollPane scroller = new ScrollPane(calendar);

        Scene scene = new Scene(scroller);
        scene.setFill(Color.BLACK);


        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static class Slot {

        private final LocalDateTime beginning;
        private final Duration period;
        private final Region view;
        public Slot(LocalDateTime beginning, Duration period) {

            this.beginning = beginning ;
            this.period = period ;

            view = new Region();
            view.setMinSize(80, 40);

        }


        public LocalTime getTime() {
            return beginning.toLocalTime() ;
        }


        public DayOfWeek getDayOfWeek() {
            return beginning.getDayOfWeek() ;
        }


        public Node getView() {
            return view;
        }

    }



    public static void main(String[] args) {
        launch(args);
    }
}