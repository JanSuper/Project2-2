package Interface.Display;

import Interface.Screens.MainScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherDisplay extends BorderPane {
    private String cityName;
    private Map<String, Object> weatherData;

    public WeatherDisplay() throws FileNotFoundException {
        getData();
        setTop();
        setCurrent();
        setDaily();
    }

    private void getData() {    //For testing example main logic  //TODO get actual data
        cityName = "Maastricht, NL";
        weatherData = new HashMap<>();

        Map<String, String> currentData = new HashMap<>();
        ArrayList<Object> dailyData = new ArrayList<>();

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int[] dayHigh = {15, 16, 17, 18, 19, 20, 21};
        int[] dayLow = {7, 8, 9, 10, 11, 12, 13};
        String[] daySummary = {"summary1", "summary2", "summary3", "summary4", "summary5", "summary6", "summary7"};

        for(int i = 1; i<7; i++) {
            Map<String, String> daily = new HashMap<>();
            daily.put("day", days[i]);
            daily.put("high", dayHigh[i]+"");
            daily.put("low", dayLow[i]+"");
            daily.put("summary", daySummary[i]);

            dailyData.add(daily);
        }
        weatherData.put("daily", dailyData);


        currentData.put("icon", "clear-day");
        currentData.put("temp", 15+"  °C");
        weatherData.put("current", currentData);

    }

    private void setTop() {
        HBox top = new HBox(40);
        top.setAlignment(Pos.CENTER);
        top.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        Label city = new Label(cityName);
        city.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        city.setTextFill(Color.WHITE);

        Button change = new Button("Change");
        change.setCursor(Cursor.HAND);
        change.setBackground(Background.EMPTY);
        change.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 12));
        change.setTextFill(Color.LIGHTSLATEGRAY.brighter());
        change.setBorder(new Border(new BorderStroke(Color.DARKGRAY.darker(), BorderStrokeStyle.SOLID, new CornerRadii(3,3,3,3,false), new BorderWidths(3))));
        change.setOnAction(e -> {});    //TODO

        top.getChildren().addAll(city, change);
        setTop(top);
    }

    private void setCurrent() throws FileNotFoundException {
        VBox current = new VBox(40);
        current.setBackground(Background.EMPTY);
        current.setAlignment(Pos.CENTER);
        current.setPadding(new Insets(15));

        Map<String, String> currentWeather = (Map<String, String>) weatherData.get("current");
        Image currentImage = getImage(currentWeather.get("icon"));
        ImageView currentImageView = new ImageView(currentImage);

        Label currently = new Label("Currently: ");
        currently.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        currently.setTextFill(MainScreen.themeColor);
        Label currentTemp = new Label(currentWeather.get("temp"));
        currentTemp.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 25));
        currentTemp.setTextFill(Color.BLACK);

        HBox tempBox = new HBox(20);
        tempBox.setAlignment(Pos.CENTER);
        tempBox.getChildren().addAll(currently, currentTemp);

        current.getChildren().addAll(currentImageView, tempBox);
        setLeft(current);
    }

    private void setDaily() {
        VBox dailyVBox = new VBox(45);
        dailyVBox.setBackground(Background.EMPTY);
        dailyVBox.setAlignment(Pos.BOTTOM_LEFT);
        dailyVBox.setPadding(new Insets(40));

        ArrayList<Map<String, String>> dailyForecast = (ArrayList<Map<String, String>>) weatherData.get("daily");
        for(int i = -1; i < dailyForecast.size(); i++) {
            HBox daily = new HBox();
            Label day = new Label();
            Label high = new Label();
            Label low = new Label();
            Label summary = new Label();

            if(i>-1) {
                day = new Label(dailyForecast.get(i).get("day"));
                day.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                day.setTextFill(Color.DARKGRAY.darker());
                high = new Label(dailyForecast.get(i).get("high") + " °C");
                high.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
                high.setTextFill(MainScreen.themeColor);
                low = new Label(dailyForecast.get(i).get("low") + " °C");
                low.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
                low.setTextFill(MainScreen.themeColor);
                summary = new Label(dailyForecast.get(i).get("summary"));
                summary.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 12));
                summary.setTextFill(Color.BLACK);
                summary.setWrapText(true);
            }
            else {
                day.setText("Day");
                day.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                day.setTextFill(Color.GRAY.darker());
                high.setText("High");
                high.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                high.setTextFill(Color.DARKGREEN.darker());
                low.setText("Low");
                low.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                low.setTextFill(Color.DARKGREEN.darker());
            }
            Region region1 = new Region();
            HBox.setHgrow(region1, Priority.ALWAYS);
            Region region2 = new Region();
            HBox.setHgrow(region2, Priority.ALWAYS);
            Region region3 = new Region();
            HBox.setHgrow(region3, Priority.ALWAYS);

            daily.getChildren().addAll(day, region1, high, region2, low, region3, summary);
            dailyVBox.getChildren().add(daily);
        }
        dailyVBox.setTranslateY(55);
        setCenter(dailyVBox);
    }

    private Image getImage(String status) throws FileNotFoundException {
        double imgDim = 65;
        switch (status) {
            case "clear-day":
                return new Image(new FileInputStream("src/res/weatherIcons/sunny.png"),imgDim,imgDim,false,true);
            case "clear-night":
                return new Image(new FileInputStream("src/res/weatherIcons/clear_night.png"),imgDim,imgDim,false,true);
            case "rain":
                return new Image(new FileInputStream("src/res/weatherIcons/rain.png"),imgDim,imgDim,false,true);
            case "snow":
                return new Image(new FileInputStream("src/res/weatherIcons/snow.png"),imgDim,imgDim,false,true);
            case "wind":
                return new Image(new FileInputStream("src/res/weatherIcons/wind.png"),imgDim,imgDim,false,true);
            case "cloudy":
                return new Image(new FileInputStream("src/res/weatherIcons/cloudy.png"),imgDim,imgDim,false,true);
            case "partly-cloudy-day":
                return new Image(new FileInputStream("src/res/weatherIcons/partly_cloudy.png"),imgDim,imgDim,false,true);
            case "cloudy-night":
                return new Image(new FileInputStream("src/res/weatherIcons/cloudy_night.png"),imgDim,imgDim,false,true);
        }
        return null;
    }
}