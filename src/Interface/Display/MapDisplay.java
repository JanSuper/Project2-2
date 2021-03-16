package Interface.Display;

import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.*;

public class MapDisplay extends VBox {
    private VBox current;

    public MapDisplay(boolean googlewebview) throws Exception {
        //just google something
        if (googlewebview)
        {
            WebView myWebView = new WebView();
            WebEngine engine = myWebView.getEngine();
            engine.load("https://www.google.de");
            current = new VBox(35);
            current.getChildren().addAll(myWebView);
            getChildren().addAll(current);
        }
        // call googlemaps api - see in resources googlemaps.html
        else {
            WebView myWebView = new WebView();
            WebEngine engine = myWebView.getEngine();
            File f = new File("src/res/googlemaps.html");
            engine.load(f.toURI().toString());
            current = new VBox(35);
            current.getChildren().addAll(myWebView);
            getChildren().addAll(current);
        }
    }
}