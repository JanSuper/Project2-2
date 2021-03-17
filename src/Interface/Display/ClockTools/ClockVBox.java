package Interface.Display.ClockTools;

import Interface.Screens.MainScreen;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ClockVBox extends VBox {
    private String country;
    private Label digitalClock;
    public ClockVBox() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(140,0,0,0));

        digitalClock = new Label();
        digitalClock.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        digitalClock.setTextFill(MainScreen.themeColor.darker().darker());
        digitalClock.setAlignment(Pos.CENTER);
        bindClockLabelToTime();

        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        int dayOfMonth = currentDate.getDayOfMonth();
        Month month = currentDate.getMonth();

        Label dateLabel = new Label(dayOfWeek.toString()+", "+dayOfMonth+" "+month);
        dateLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        dateLabel.setTextFill(MainScreen.themeColor.darker());
        dateLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(digitalClock, dateLabel);
    }

    private void bindClockLabelToTime() {
        //digital clock updates per second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                actionEvent -> {
                    Calendar time = null;
                    if(country==null){
                        time = Calendar.getInstance();
                    }else{
                        TimeZone timeZone = TimeZone.getTimeZone(country);
                        time = Calendar.getInstance(timeZone);
                    }
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

    //returns padded string from specified width
    public static String pad(int fieldWidth, char padChar, String s) {
        return String.valueOf(padChar).repeat(Math.max(0, fieldWidth - s.length())) + s;
    }

    public void setCountry(String country) {
        this.country = country;
        bindClockLabelToTime();
    }
}
