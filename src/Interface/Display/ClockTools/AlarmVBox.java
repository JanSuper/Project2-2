package Interface.Display.ClockTools;

import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class AlarmVBox extends VBox {
    private Label datePickerTxt;
    private DatePicker d;

    //FROM TIME
    private Label timePickerTxt;
    private int hoursTimer = 0; int minutesTimer = 0; int secondsTimer = 0;
    private Label timerTime;
    private HBox plus; HBox minus;

    //TO TIME
    private Label timePickerTxt1;
    private int hoursTimer1 = 0; int minutesTimer1 = 0; int secondsTimer1 = 0;
    private Label timerTime1;
    private HBox plus1; HBox minus1;

    private Label descriptionTxt;
    private TextField description;

    public ColorPicker colorPicker;

    private Button enter;

    private MainScreen mainScreen;
    private boolean isReminder;

    private Timeline timeline;

    public AlarmVBox(MainScreen mainScreen,boolean isReminder) {
        this.mainScreen = mainScreen;
        this.isReminder = isReminder;
        this.timeline = new Timeline();
        if(isReminder){
            setSpacing(8);
        }else{
            setSpacing(15);
        }
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));

        createContent();
        if(isReminder){
            getChildren().addAll(datePickerTxt,d,timePickerTxt,plus,timerTime,minus,timePickerTxt1,plus1,timerTime1,minus1,descriptionTxt,description,colorPicker,enter);
        }else{
            description.setPrefSize(150,100);
            getChildren().addAll(timePickerTxt,plus,timerTime,minus,descriptionTxt,description,enter);
        }
    }

    private void createContent(){
        datePickerTxt = new Label("Date:");
        datePickerTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        datePickerTxt.setTextFill(MainScreen.themeColor.darker());
        datePickerTxt.setAlignment(Pos.CENTER);

        // create a date picker
        d = new DatePicker();

        timePickerTxt = new Label();
        if(isReminder){
            timePickerTxt.setText("From:");
        }else{
            timePickerTxt.setText("Time:");
        }
        timePickerTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        timePickerTxt.setTextFill(MainScreen.themeColor.darker());
        timePickerTxt.setAlignment(Pos.CENTER);

        timerTime = new Label();
        timerTime.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        timerTime.setTextFill(MainScreen.themeColor.darker().darker());
        timerTime.setAlignment(Pos.CENTER);

        plus = new HBox(70);
        setPlusButtons(plus,true);
        minus = new HBox(80);
        setMinusButtons(minus,true);
        disablePlusMinus(false, plus, minus);

        timePickerTxt1 = new Label("To:");
        timePickerTxt1.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        timePickerTxt1.setTextFill(MainScreen.themeColor.darker());
        timePickerTxt1.setAlignment(Pos.CENTER);

        timerTime1 = new Label();
        timerTime1.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 58));
        timerTime1.setTextFill(MainScreen.themeColor.darker().darker());
        timerTime1.setAlignment(Pos.CENTER);

        plus1 = new HBox(70);
        setPlusButtons(plus1,false);
        minus1 = new HBox(80);
        setMinusButtons(minus1,false);
        disablePlusMinus(false, plus1, minus1);

        setTimerTime();

        descriptionTxt = new Label("Description:");
        descriptionTxt.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        descriptionTxt.setTextFill(MainScreen.themeColor.darker());
        descriptionTxt.setAlignment(Pos.CENTER);

        description = new TextField();
        description.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        description.setPrefSize(150,400);

        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.ORANGE);
        colorPicker.setOnAction((EventHandler) t -> enter.setBackground(new Background(new BackgroundFill(colorPicker.getValue(), new CornerRadii(90,true), Insets.EMPTY))));

        enter = new Button("Enter");
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        designAlarmButton(enter);
        enter.setOnMouseClicked(e-> {
            try {
                if(isReminder){
                    createAlert();
                }else{
                    addAlarm(timerTime.getText(),description.getText());
                }

            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
            hoursTimer = 0;
            minutesTimer = 0;
            secondsTimer = 0;
            timerTime.setText(twoDigitString(hoursTimer)+":"+twoDigitString(minutesTimer)+":"+twoDigitString(secondsTimer));
            if(isReminder){
                hoursTimer1 = 0;
                minutesTimer1 = 0;
                secondsTimer1 = 0;
                timerTime1.setText(twoDigitString(hoursTimer1)+":"+twoDigitString(minutesTimer1)+":"+twoDigitString(secondsTimer1));
            }
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
        addReminder(res);
        //mainScreen.prepareAlarms();
    }
    private String getAlreadyOnFile() throws IOException {
        String res = "";
        FileReader fr=new FileReader(Data.getRemindersFile());
        int i;
        while((i=fr.read())!=-1)
            res += ((char)i);
        fr.close();
        return res;
    }
    private void addReminder(String res){
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
                writer = new FileWriter(Data.getRemindersFile());
                PrintWriter out = new PrintWriter(writer);
                out.print(res);
                System.out.println(description.getText());
                if(description.getText().isEmpty()||description.getText().isBlank()){
                    out.print(Data.getUsername() + ";" + da + ";"+ timerTime.getText() + ";" +timerTime1.getText() + ";" + colorPicker.getValue() +  ";" + "\"no description\"" + "\n");
                }else{
                    out.print(Data.getUsername() + ";" + da + ";"+ timerTime.getText() + ";" +timerTime1.getText() + ";" + colorPicker.getValue() +  ";" + description.getText() + "\n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(da,dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.parse(timerTime.getText(),timeFormatter);
        LocalTime localTime1 = LocalTime.parse(timerTime1.getText(),timeFormatter);
        mainScreen.calendarDisplay.addReminder(description.getText(),localDate,localTime,localTime1,colorPicker.getValue());
        mainScreen.chat.receiveMessage("Reminder on the " + da + " from " + timerTime.getText() + " to " + timerTime1.getText() + " with description \"" + description.getText() + "\" has been added");
    }

    public void addAlarm(String time,String desc) throws ParseException {
        mainScreen.chat.receiveMessage("Today, there will be the alarm at " + time + " with description \"" + desc + "\"");
        displayAlarmAtTime(time,desc);
    }

    public void displayAlarmAtTime(String time,String desc) throws ParseException {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(getTimeDiffInSec(time)), event -> notifyUser(time,desc));
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    private void notifyUser(String time,String desc) { //TODO add sound
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

        Label timerLabel = new Label("Alarm of " + time);
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

        Label label = new Label(desc);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 26));
        label.setTextFill(Color.WHITESMOKE);
        label.setAlignment(Pos.CENTER);

        notification.getChildren().addAll(topBox, label);
    }

    public int getTimeDiffInSec(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = format.parse(time);
        Date date2 = format.parse(java.time.LocalTime.now().toString());
        int difference = (int) (date2.getTime() - date1.getTime());
        if(date2.before(date1)){
            return -difference/1000;
        }
        return difference/1000;
    }


    private void disablePlusMinus(boolean b, HBox plus, HBox minus) {
        for(int i = 0; i<3; i++) {
            plus.getChildren().get(i).setDisable(b);
            minus.getChildren().get(i).setDisable(b);
        }
    }

    private void setPlusButtons(HBox plus,boolean isFrom) {
        if(isReminder){
            if(isFrom){
                Button plusH = new Button("+");
                designPlusMinusButton(plusH);
                plusH.setOnAction(e -> {
                    if (hoursTimer==23) {hoursTimer=0;} else {hoursTimer++;} setTimerTime();
                });

                Button plusM = new Button("+");
                designPlusMinusButton(plusM);
                plusM.setOnAction(e -> {
                    if (minutesTimer==59) {minutesTimer=0;} else {minutesTimer++;} setTimerTime();
                });

                Button plusS = new Button("+");
                designPlusMinusButton(plusS);
                plusS.setOnAction(e -> {
                    if (secondsTimer==59) {secondsTimer=0;} else {secondsTimer++;} setTimerTime();
                });

                plus.setAlignment(Pos.CENTER);
                plus.setTranslateY(28);
                plus.getChildren().addAll(plusH, plusM, plusS);
            }else{
                Button plusH1 = new Button("+");
                designPlusMinusButton(plusH1);
                plusH1.setOnAction(e -> {
                    if (hoursTimer1==23) {hoursTimer1=0;} else {hoursTimer1++;} setTimerTime();
                });

                Button plusM1 = new Button("+");
                designPlusMinusButton(plusM1);
                plusM1.setOnAction(e -> {
                    if (minutesTimer1==59) {minutesTimer1=0;} else {minutesTimer1++;} setTimerTime();
                });

                Button plusS1 = new Button("+");
                designPlusMinusButton(plusS1);
                plusS1.setOnAction(e -> {
                    if (secondsTimer1==59) {secondsTimer1=0;} else {secondsTimer1++;} setTimerTime();
                });

                plus.setAlignment(Pos.CENTER);
                plus.setTranslateY(28);
                plus.getChildren().addAll(plusH1, plusM1, plusS1);
            }
        }else{
            Button plusH = new Button("+");
            designPlusMinusButton(plusH);
            plusH.setOnAction(e -> {
                if (hoursTimer==23) {hoursTimer=0;} else {hoursTimer++;} setTimerTime();
            });

            Button plusM = new Button("+");
            designPlusMinusButton(plusM);
            plusM.setOnAction(e -> {
                if (minutesTimer==59) {minutesTimer=0;} else {minutesTimer++;} setTimerTime();
            });

            Button plusS = new Button("+");
            designPlusMinusButton(plusS);
            plusS.setOnAction(e -> {
                if (secondsTimer==59) {secondsTimer=0;} else {secondsTimer++;} setTimerTime();
            });

            plus.setAlignment(Pos.CENTER);
            plus.setTranslateY(28);
            plus.getChildren().addAll(plusH, plusM, plusS);
        }
    }

    private void setMinusButtons(HBox minus,boolean isFrom) {
        if(isReminder){
            if(isFrom){
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
            }else{
                Button minusH1 = new Button("_");
                designPlusMinusButton(minusH1);
                minusH1.setOnAction(e -> {if(hoursTimer1>0){hoursTimer1--; setTimerTime();}});

                Button minusM1 = new Button("_");
                designPlusMinusButton(minusM1);
                minusM1.setOnAction(e -> {if (minutesTimer1==0) {minutesTimer1=59;} else {minutesTimer1--;} setTimerTime();});

                Button minusS1 = new Button("_");
                designPlusMinusButton(minusS1);
                minusS1.setOnAction(e -> {if (secondsTimer1==0) {secondsTimer1=59;} else {secondsTimer1--;} setTimerTime();});

                minus.setAlignment(Pos.CENTER);
                minus.setTranslateY(-31);
                minus.getChildren().addAll(minusH1, minusM1, minusS1);
            }
        }else{
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
        if(isReminder){
            timerTime1.setText(twoDigitString(hoursTimer1)+":"+twoDigitString(minutesTimer1)+":"+twoDigitString(secondsTimer1));
        }
    }

    private String twoDigitString(long number) {
        if (number == 0) { return "00"; }
        if (number / 10 == 0) { return "0" + number; }
        return String.valueOf(number);
    }
}
