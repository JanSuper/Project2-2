package Interface.Display;

import Interface.Screens.MainScreen;
import Weather.WeatherFetch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WeatherDisplay extends VBox {
    private String cityName;
    private String countryName;
    private Map<String, Object> weatherData;
    private double imgDim = 65;
    private HBox top;
    private VBox current;
    private VBox dailyVBox;

    private MainScreen mainScreen;

    public WeatherDisplay(String city, String country,MainScreen mainScreen) throws Exception {
        this.cityName = city;
        this.countryName = country;

        this.mainScreen = mainScreen;

        getData();
        setTop();
        setCurrent();
        setDaily();

        getChildren().setAll(top, current, dailyVBox);
        setMaxHeight(Double.MAX_VALUE);
        setMinHeight(Double.MIN_VALUE);
    }

    private void getData() throws Exception {
        String rawWeatherData = WeatherFetch.getWeather(cityName, countryName);
        List<String[]> separateLines = new ArrayList<>();
        rawWeatherData.lines().forEach(s -> separateLines.add(s.split(",")));

        ArrayList<String> dayH = new ArrayList<>();
        ArrayList<String> dayL = new ArrayList<>();
        ArrayList<String> daySummary = new ArrayList<>();
        for(int i = 1; i <= 8; i++) {
            dayH.add(separateLines.get(i)[12]);
            dayL.add(separateLines.get(i)[11]);

            int count = separateLines.get(i).length;
            if(count==27) {
                String sum = separateLines.get(i)[count - 2] + "," + separateLines.get(i)[count - 1];
                daySummary.add(sum.replace("\"", ""));
            }
            else {
                daySummary.add(separateLines.get(i)[count - 1].replace("\"", ""));
            }
        }

        weatherData = new HashMap<>();
        Map<String, String> currentData = new HashMap<>();
        ArrayList<Object> dailyData = new ArrayList<>();

        DayOfWeek currentDayOfWeek = DayOfWeek.of(LocalDate.now().getDayOfWeek().getValue());
        String[] days = {currentDayOfWeek.plus(1).name(), currentDayOfWeek.plus(2).name(), currentDayOfWeek.plus(3).name(), currentDayOfWeek.plus(4).name(), currentDayOfWeek.plus(5).name(), currentDayOfWeek.plus(6).name(), currentDayOfWeek.plus(7).name()};

        for(int i = 0; i<7; i++) {
            Map<String, String> daily = new HashMap<>();
            daily.put("day", days[i]);
            daily.put("high", dayH.get(i+1));
            daily.put("low", dayL.get(i+1));
            daily.put("summary", daySummary.get(i+1));

            dailyData.add(daily);
        }
        weatherData.put("daily", dailyData);


        currentData.put("icon", daySummary.get(0));
        currentData.put("temp", 15+"  °C"); //TODO
        currentData.put("high", dayH.get(0));
        currentData.put("low", dayL.get(0));
        weatherData.put("current", currentData);
    }

    private void setTop() {
        top = new HBox(60);
        top.setAlignment(Pos.CENTER);
        top.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        Label city = new Label(cityName + ", " + countryName);
        city.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        city.setTextFill(Color.WHITE);
        city.setAlignment(Pos.CENTER);

        Button change = new Button("Change");
        change.setCursor(Cursor.HAND);
        change.setBackground(Background.EMPTY);
        change.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 13));
        change.setTextFill(Color.LIGHTGRAY);
        change.setBorder(new Border(new BorderStroke(Color.DARKGRAY.darker(), BorderStrokeStyle.SOLID, new CornerRadii(3,3,3,3,false), new BorderWidths(3))));
        change.setAlignment(Pos.CENTER);
        change.setOnAction(e -> {});    //TODO

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.CENTER_RIGHT);
        exit.setOnAction(e -> mainScreen.setOptionsMenu());

        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);

        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        top.getChildren().addAll(region1, city, change, region2, exit);
    }

    private void setCurrent() throws FileNotFoundException {
        current = new VBox(35);
        current.setBackground(Background.EMPTY);
        current.setAlignment(Pos.CENTER);
        current.setPadding(new Insets(15));

        Map<String, String> currentWeather = (Map<String, String>) weatherData.get("current");

        Rectangle currentConditionImage = new Rectangle(0, 0, 65, 65);
        currentConditionImage.setArcWidth(40.0);
        currentConditionImage.setArcHeight(40.0);
        ImagePattern pattern = new ImagePattern(getImage(currentWeather.get("icon")));
        currentConditionImage.setFill(pattern);
        currentConditionImage.setEffect(new DropShadow(20, Color.BLACK));

        Label currentConditionLabel = new Label(currentWeather.get("icon"));
        currentConditionLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        currentConditionLabel.setTextFill(Color.DARKRED.darker());

        Label currently = new Label("Currently: ");
        currently.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
        currently.setTextFill(MainScreen.themeColor);
        Label currentTemp = new Label(currentWeather.get("temp"));
        currentTemp.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 25));
        currentTemp.setTextFill(Color.BLACK);

        HBox currentTempBox = new HBox(20);
        currentTempBox.setAlignment(Pos.CENTER);
        currentTempBox.getChildren().addAll(currently, currentTemp);

        Label h = new Label("H:");
        h.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        h.setTextFill(MainScreen.themeColor);
        Label hTemp = new Label(currentWeather.get("high")+"°");
        hTemp.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 19));
        hTemp.setTextFill(Color.BLACK);
        Label l = new Label("L:");
        l.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        l.setTextFill(MainScreen.themeColor);
        Label lTemp = new Label(currentWeather.get("low")+"°");
        lTemp.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 19));
        lTemp.setTextFill(Color.BLACK);

        HBox hLBox = new HBox(10);
        hLBox.setAlignment(Pos.CENTER);
        hLBox.getChildren().addAll(h, hTemp, l, lTemp);

        current.getChildren().addAll(currentConditionImage, currentConditionLabel, currentTempBox, hLBox);
    }

    private void setDaily() {
        dailyVBox = new VBox(25);
        dailyVBox.setBackground(Background.EMPTY);
        dailyVBox.setAlignment(Pos.BOTTOM_CENTER);
        dailyVBox.setPadding(new Insets(60));

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
                day.setTextFill(Color.LIGHTSLATEGRAY.darker().darker());
                high = new Label(dailyForecast.get(i).get("high") + " °C");
                high.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
                high.setTextFill(MainScreen.themeColor);
                low = new Label(dailyForecast.get(i).get("low") + " °C");
                low.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
                low.setTextFill(MainScreen.themeColor);
                summary = new Label(dailyForecast.get(i).get("summary"));
                summary.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
                summary.setTextFill(Color.BLACK);
                summary.setWrapText(true);
            }
            else {
                day.setText("");
                day.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                day.setTextFill(MainScreen.themeColor.brighter());
                high.setText("High");
                high.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                high.setTextFill(Color.LIGHTSLATEGRAY.darker());
                low.setText("Low");
                low.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
                low.setTextFill(Color.LIGHTSLATEGRAY.darker());
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
        dailyVBox.setTranslateY(-50);
    }

    private Image getImage(String status) throws FileNotFoundException {
        Image img;
        if(status.contains(",")) {
            String[] s = status.split(",");
            status = s[0];
        }
        switch (status) {
            case "Clear":
                img = new Image(new FileInputStream("src/res/weatherIcons/day_clear.png"),imgDim,imgDim,false,true);
                break;
            case "Partially cloudy":
                img = new Image(new FileInputStream("src/res/weatherIcons/day_partial_cloud.png"),imgDim,imgDim,false,true);
                break;
            case "Overcast":
                img =  new Image(new FileInputStream("src/res/weatherIcons/overcast.png"),imgDim,imgDim,false,true);
                break;
            case "Thunderstorm":
                img = new Image(new FileInputStream("ssrc/res/weatherIcons/rain_thunder.png"),imgDim,imgDim,false,true);
                break;
            case "Thunderstorm Without Precipitation":
                img = new Image(new FileInputStream("src/res/weatherIcons/thunder.png"),imgDim,imgDim,false,true);
                break;
            case "Rain": case "Rain Showers": case "Heavy Rain": case "Light Rain": case "Drizzle": case "Heavy Drizzle": case "Light Drizzle":
                case "Heavy Drizzle/Rain": case "Freezing Drizzle/Freezing Rain": case "Heavy Freezing Drizzle/Freezing Rain":
                case "Light Freezing Drizzle/Freezing Rain": case "Heavy Freezing Rain": case "Light Freezing Rain":
                img = new Image(new FileInputStream("src/res/weatherIcons/rain.png"),imgDim,imgDim,false,true);
                break;
            case "Snow And Rain Showers": case "Heavy Rain And Snow": case "Light Rain And Snow":
                img = new Image(new FileInputStream("src/res/weatherIcons/sleet.png"),imgDim,imgDim,false,true);
                break;
            case "Snow": case "Snow Showers": case "Heavy Snow": case "Light Snow": case "Blowing Or Drifting Snow":
                img = new Image(new FileInputStream("src/res/weatherIcons/snow.png"),imgDim,imgDim,false,true);
                break;
            case "Mist":
                img = new Image(new FileInputStream("src/res/weatherIcons/mist.png"),imgDim,imgDim,false,true);
                break;
            case "Squalls":
                img = new Image(new FileInputStream("src/res/weatherIcons/wind.png"),imgDim,imgDim,false,true);
                break;
            case "Fog": case "Freezing Fog":
                img = new Image(new FileInputStream("src/res/weatherIcons/fog.png"),imgDim,imgDim,false,true);
                break;
            case "Funnel Cloud/Tornado":
                img = new Image(new FileInputStream("src/res/weatherIcons/tornado.png"),imgDim,imgDim,false,true);
                break;
            default:
                return new Image(new FileInputStream("src/res/weatherIcons/unknown.png"),imgDim,imgDim,false,true);
        }
        return img;
    }
}