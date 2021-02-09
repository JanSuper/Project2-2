package Interface;

import Interface.Chat.ChatApp;
import Interface.MenuTools.MenuItem;
import Interface.MenuTools.MenuTitle;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.scene.control.TextField;
import DataBase.Data;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class StartScreen extends Application {
    private final int WIDTH = 1500;
    private final int HEIGHT = 800;

    private Stage stage;

    private TextField user;
    private TextField psw;
    private Text left;

    public String [][] dataSet;
    private int counter = 0;
    private boolean login = false;

    private StackPane root = new StackPane();
    private VBox menuBox = new VBox(-5);

    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Enter", () -> {
                try {
                    counter++;
                    for(int i=0;!login&&i<dataSet.length;i++){
                        if (user.getText().equals(dataSet[i][0])&&psw.getText().equals(dataSet[i][1])){
                            login=true;
                        }
                    }
                    if(login) {
                        Data.setUsername(user.getText());
                        Data.setPassword(psw.getText());
                        new MainScreen();
                        this.stage.close();
                    }else if(counter ==1 ) {
                        left.setText("Try again, 2 attempts left");
                    }else if(counter ==2 ) {
                        left.setText("Try again, 1 attempts left");
                    }else if(counter >=3 ) {
                        left.setText("Sorry, you have used your 3 attempts");
                        System.out.println("Sorry, you have used your 3 attempts");
                        System.exit(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),

            new Pair<String, Runnable>("Exit to Desktop", Platform::exit)
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = new Stage();
        root.setId("start-screen-pane");
        Background background = Data.createBackGround();
        root.setBackground(background);
        Scene scene = new Scene(root, 800, 800);
        addContent();
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("Blokus Start Screen");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        this.stage = primaryStage;

        left = new Text("");
        left.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        left.setFill(Color.RED);
        left.setTranslateY(-65);left.setTranslateX(-15);
        root.getChildren().add(left);

        //create 2 arrays, one to store the data of the user and the other to split the data between password and username
        String [] splitData;
        String [][] data = new String [0][];

        // Begin a new file reader object directed at the text file we want to read (input)
        File file = new File("src\\DataBase\\users.txt");
        // We want to cast out file reader to a buffered reader! (for reasons which will be clear next lecture).
        BufferedReader br;
        {
            // If the file does not exist, we will get an error, so try catch to make java happy
            try {
                // create buffered reader
                br = new BufferedReader(new FileReader(file));
                String st = "";

                // while another line exists in our text file, we read it!
                while ((st = br.readLine()) != null) {
                    // instead of printing them, here we can also store the users in an array
                    splitData = st.split(" ",2);

                    //split the array bewteen usernames and password
                    if(splitData.length == 2) {
                        String[][] res = new String[data.length+1][splitData.length];
                        for(int i=0;i<data.length;i++){
                            for(int j=0;j<res[0].length;j++){
                                res[i][j]=data[i][j];
                            }
                        }
                        res[data.length][0]=splitData[0];
                        res[data.length][1]=splitData[1];

                        data = res;
                    }
                }
                // catch exceptions if the files are not found
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        //the array we use in the actionlistener need to be final, or we can't use it
        dataSet = data;
    }

    private void addContent() {
        addTitle();
        double lineX = WIDTH / 2. ;
        double lineY = HEIGHT / 3. + 250;
        addMenu(lineX + 5, lineY + 5);

        startAnimation();
    }


    private void addTitle() {
        MenuTitle title = new MenuTitle("Project 2-2 Assistant");
        title.setTranslateX(WIDTH / 2. + title.getTitleWidth()/10);
        title.setTranslateY(HEIGHT / 3. - 50);
        root.getChildren().add(title);

        MenuTitle username = new MenuTitle("Username :");
        username.setTranslateX(WIDTH / 2. - 100);
        username.setTranslateY(HEIGHT / 3. + 50);
        root.getChildren().add(username);

        MenuTitle password = new MenuTitle("Password :");
        password.setTranslateX(WIDTH / 2. - 100);
        password.setTranslateY(HEIGHT / 3. + 120);
        root.getChildren().add(password);
    }

    private void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);
        menuData.forEach(data -> {
            MenuItem item = new MenuItem(data.getKey());
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);item.setTranslateY(-50);

            Rectangle clip = new Rectangle(300, 30);
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        });

        user = new TextField();
        user.setFont(Font.font("Verdana", FontWeight.BOLD,15));
        user.setStyle("-fx-text-fill: red;");
        user.setMaxWidth(200);
        user.setTranslateX(WIDTH / 2. - 50);
        user.setTranslateY(-300);
        menuBox.getChildren().add(user);

        psw = new TextField();
        psw.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        psw.setStyle("-fx-text-fill: red;");
        psw.setMaxWidth(200);
        psw.setTranslateX(WIDTH / 2. - 50);
        psw.setTranslateY(-250);
        menuBox.getChildren().add(psw);

        root.getChildren().add(menuBox);
    }

    private void startAnimation() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1));
        st.setToY(1);
        st.setOnFinished(e -> {

            for (int i = 0; i < menuBox.getChildren().size(); i++) {
                Node n = menuBox.getChildren().get(i);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(1 + i * 0.15), n);
                tt.setToX(0);
                tt.setOnFinished(e2 -> n.setClip(null));
                tt.play();
            }
        });
        st.play();
    }

}