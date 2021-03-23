package Interface.Display;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MapDisplay extends VBox {
    private VBox current;


    public MapDisplay(String googlewebview) throws Exception {
        //just open the webview engine to google something
        if (googlewebview == "")
        {
            // create webview for google search
            WebView myWebView = new WebView();
            // get standard browser
            WebEngine engine = myWebView.getEngine();
            engine.load("https://www.google.de");
            current = new VBox(30);
            current.setScaleY(1.19);
            current.setPadding(new Insets(52, 0, 0, 0));
            current.getChildren().addAll(myWebView);
            getChildren().addAll(current);
        }
        // DISPLAY GOOGLE MAPS:
        // show a interactive google map without routes
        // read description below
        else if (googlewebview.contains("map")) {
            // Create a webview
            WebView myWebView = new WebView();
            WebEngine engine = myWebView.getEngine();

            // First I need to transform the google maps location names into coordinates
            // which I do by using another google maps api called: google maps directions.
            // For that I send a request to the google maps directions api servers by adding the locations
            // "googlewebview" to the url - the response will be json data format (xml is also possible):

            // 1. Filter the location words from the string and replace " " through "+" (to add it to the url)
            googlewebview = googlewebview.split(" ",2)[1];
            googlewebview = googlewebview.replace("-", "+");
            // 2. Send a request to google servers:
            String adress = "https://maps.googleapis.com/maps/api/geocode/json?address=" + googlewebview + "&key=AIzaSyDxxcJvhBUP-fFzH2i4oIIPAEVHPfkxDw8";
            URL url = new URL(adress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Read the data from the request
            Scanner sc = new Scanner(url.openStream());
            String inline = "";
            while(sc.hasNext())
            {
                inline+=sc.nextLine();
            }
            sc.close();
            // Parse the datas into json
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject)parse.parse(inline);
            // Get the longitude and latitude variables from the child nodes in the json "tree"
            JSONArray jsonarr_1 = (JSONArray) jobj.get("results");
            JSONObject jsonObject2 = (JSONObject)jsonarr_1.get(0);
            JSONObject jsonObject3 = (JSONObject)jsonObject2.get("geometry");
            JSONObject location = (JSONObject) jsonObject3.get("location");
            Object lat = location.get("lat");
            Object lng = location.get("lng");

            // Now I got the location names transformed into coordinates and next I will create
            // the final output html/javascript document to display the interactive google map.
            // For that I replace the variables longitude and latitude marked with "$" in the
            // googlemaps.html template and create a new requestgooglemaps.html file

            //read old: googlemaps.html
            //(->find the html files in folder /res)
            File f = new File("src/res/googlemaps.html");
            String content = new Scanner(f).useDelimiter("\\Z").next();
            //change variables
            content = content.replace("$latitude", lat.toString());
            content = content.replace("$longitude", lng.toString());
            //write new: requestgooglemaps.html
            FileWriter writer = new FileWriter("src/res/requestgooglemaps.html");
            writer.write(content);
            writer.close();
            File f_new = new File("src/res/requestgooglemaps.html");

            // Next I call the google maps javascript api by loading my new requestgooglemaps.html file
            // (see /res/googlemaps.html or once created /res/googlemapsrequest.html)

            engine.load(f_new.toURI().toString());
            current = new VBox(35);
            current.setScaleY(1.19);
            current.setPadding(new Insets(52, 0, 0, 0));
            current.getChildren().addAll(myWebView);
            getChildren().addAll(current);

            // Some comments :) :
            // Google Maps provides multiple api's for different services: Google Maps Javascript for the interactive
            // map, Google Maps Static to display just a map picture, Google Maps Places to get location information
            // and Google Maps Directions to get routing information. These are just a few and their are many
            // more (beside google maps) e.g. a youtube api to get youtube videos or a cloud service with AI tools
            // You can add and work with theses api's once you created a google cloud platform account
            // and unfortunately add bill information - but google provides for developers each month
            // 200$ for free (and additional daily free requests - in cases of developing with google maps these are between
            // 30.000-100.000 google maps api requests per month (distinguish between a interactive map request or a static map picture request)
        }
        // GOOGLE MAPS WITH ROUTES:
        // show a interactive google map with routes
        // almost same procedure - read description above starting from "DISPLAY GOOGLE MAPS"
        else if (googlewebview.contains("route"))
        {
            WebView myWebView = new WebView();
            WebEngine engine = myWebView.getEngine();

            googlewebview = googlewebview.split(" ",2)[1];
            String source = googlewebview.split("-",2)[0];
            String goal = googlewebview.split("-",2)[1];

            //request the starting map view coordinates
            //the route source and goal will transformed into coordinates in the googlemapsRoute.html file
            String address2 = "https://maps.googleapis.com/maps/api/geocode/json?address=" + source + "&key=AIzaSyDxxcJvhBUP-fFzH2i4oIIPAEVHPfkxDw8";
            URL url2 = new URL(address2);
            HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
            conn2.setRequestMethod("GET");
            conn2.connect();
            // Read the data from the request
            Scanner sc2 = new Scanner(url2.openStream());
            String inline2 = "";
            while(sc2.hasNext())
            {
                inline2+=sc2.nextLine();
            }
            sc2.close();
            // Parse the datas into json
            JSONParser parse2 = new JSONParser();
            JSONObject jobj2 = (JSONObject)parse2.parse(inline2);
            // Get the longitude and latitude variables from the child nodes in the json "tree"
            JSONArray jsonarr_12 = (JSONArray) jobj2.get("results");
            JSONObject jsonObject22 = (JSONObject)jsonarr_12.get(0);
            JSONObject jsonObject32 = (JSONObject)jsonObject22.get("geometry");
            JSONObject location2 = (JSONObject) jsonObject32.get("location");
            Object lat2 = location2.get("lat");
            Object lng2 = location2.get("lng");

            //read old: googlemaps.html
            File f = new File("src/res/googlemapsRoute.html");
            String content = new Scanner(f).useDelimiter("\\Z").next();
            //change variables
            content = content.replace("$latitude", lat2.toString());
            content = content.replace("$longitude", lng2.toString());
            content = content.replace("$source", source);
            content = content.replace("$goal", goal);
            //write new: requestgooglemaps.html
            FileWriter writer = new FileWriter("src/res/requestgooglemapsRoute.html");
            writer.write(content);
            writer.close();
            File f_new = new File("src/res/requestgooglemapsRoute.html");

            // Next I call the google maps javascript api by loading my new requestgooglemapsRoute.html file
            // (see /res/googlemaps.html or once created /res/requestgooglemapsRoute.html)
            engine.load(f_new.toURI().toString());
            current = new VBox(35);
            current.setScaleY(1.19);
            current.setPadding(new Insets(52, 0, 0, 0));
            current.getChildren().addAll(myWebView);
            getChildren().addAll(current);
        }
    }
}