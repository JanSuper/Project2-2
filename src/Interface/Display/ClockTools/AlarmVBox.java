package Interface.Display.ClockTools;

import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AlarmVBox extends VBox {
    private Label datePickerTxt;
    private DatePicker d;

    private Label timePickerTxt;
    private int hoursTimer = 0; int minutesTimer = 0; int secondsTimer = 0;
    private Label timerTime;
    private HBox plus; HBox minus;

    private Label descriptionTxt;
    private TextField description;

    private Button enter;

    private MainScreen mainScreen;

    public AlarmVBox(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));

        createContent();
        getChildren().addAll(datePickerTxt,d,timePickerTxt,plus,timerTime,minus,descriptionTxt,description,enter);

    }

    private void createContent(){
        datePickerTxt = new Label("Date:");
        datePickerTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        datePickerTxt.setTextFill(MainScreen.themeColor.darker());
        datePickerTxt.setAlignment(Pos.CENTER);

        // create a date picker
        d = new DatePicker();

        timePickerTxt = new Label("Time:");
        timePickerTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        timePickerTxt.setTextFill(MainScreen.themeColor.darker());
        timePickerTxt.setAlignment(Pos.CENTER);

        timerTime = new Label();
        timerTime.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        timerTime.setTextFill(MainScreen.themeColor.darker().darker());
        timerTime.setAlignment(Pos.CENTER);
        setTimerTime();

        plus = new HBox(70);
        setPlusButtons(plus);
        minus = new HBox(80);
        setMinusButtons(minus);
        disablePlusMinus(false, plus, minus);

        descriptionTxt = new Label("Description:");
        descriptionTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        descriptionTxt.setTextFill(MainScreen.themeColor.darker());
        descriptionTxt.setAlignment(Pos.CENTER);

        description = new TextField();
        description.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        description.setPrefSize(10,50);
        description.setScaleX(0.5);description.setScaleY(0.5);

        enter = new Button("Enter");
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designAlarmButton(enter);
        enter.setOnAction(e-> {
            try {
                createAlert();
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
            hoursTimer = 0;
            minutesTimer = 0;
            secondsTimer = 0;
            timerTime.setText(twoDigitString(hoursTimer)+" : "+twoDigitString(minutesTimer)+" : "+twoDigitString(secondsTimer));
            description.setText("");
        });
    }

    private void designAlarmButton(Button button) {
        button.setCursor(Cursor.HAND);
        button.setUnderline(true);
        button.setPrefSize(90, 62);
        button.setFont(Font.font("Tahoma", FontWeight.BOLD, 16));
        button.setTextFill(Color.LIGHTGRAY);
        button.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(90,true), new BorderWidths(2))));
        button.setAlignment(Pos.CENTER);
    }

    private void createAlert() throws IOException, ParseException {
        String res = getAlreadyOnFile();
        addNewAlarm(res);
        mainScreen.prepareAlarms();
    }
    private String getAlreadyOnFile() throws IOException {
        String res = "";
        FileReader fr=new FileReader("src\\DataBase\\alarm.txt");
        int i;
        while((i=fr.read())!=-1)
            res += ((char)i);
        fr.close();
        return res;
    }
    private void addNewAlarm(String res){
        String da = "";
        //convert date to string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = d.getValue();
        if (date != null) {
            da = (formatter.format(date));
        } else {
            da = LocalDate.now().format(formatter);
        }

        FileWriter writer;
        {
            try {
                writer = new FileWriter("src\\DataBase\\alarm.txt");
                PrintWriter out = new PrintWriter(writer);
                out.print(res);

                out.print(Data.getUsername() + ";" + da + ";"+ timerTime.getText() + ";" + description.getText() + "\n");

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Alarm at " + timerTime.getText() + " with description \"" + description.getText() + "\" has been added");
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
        plusH.setOnAction(e -> {if (hoursTimer==23) {hoursTimer=0;} else {hoursTimer++;} setTimerTime();});

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

    private void setTimerTime() {
        timerTime.setText(twoDigitString(hoursTimer)+":"+twoDigitString(minutesTimer)+":"+twoDigitString(secondsTimer));
    }

    private String twoDigitString(long number) {
        if (number == 0) { return "00"; }
        if (number / 10 == 0) { return "0" + number; }
        return String.valueOf(number);
    }
}
