package Interface.Screens;

import Agents.User;
import Interface.Screens.StartScreenTools.MenuTitle;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private final int WIDTH = 650;
    private final int HEIGHT = 800;

    private Stage stage;

    private TextField user;
    private PasswordField psw;
    private Text left;

    private int counter = 0;

    private StackPane root = new StackPane();
    private VBox menuBox;


    private List<Pair<String, Runnable>> menuData = Arrays.asList(
            new Pair<String, Runnable>("Log in", () -> {
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
            new Pair<String, Runnable>("Sign up", () -> {
                try {
                    signup();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
            new Pair<String, Runnable>("Exit to Desktop", Platform::exit)
    );

    private boolean checkInfo(String info){
        File userFile = new File("src/DataBase/Users/"+user.getText()+".txt");

        try{
            BufferedReader data = new BufferedReader(new FileReader(userFile));

            String s;
            while ((s = data.readLine()) != null)
            {
                if(s.startsWith(info))
                {
                    if(psw.getText().equals(data.readLine())){
                        return true;
                    }
                }
            }
            data.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void login() throws Exception {
        counter++;
        if(user.getText().isEmpty()||user.getText().isBlank() && psw.getText().isEmpty()||psw.getText().isBlank()){
            left.setText("Sorry, username or password not possible");
        }else{
            if(userAlreadyExists()){
                if(checkInfo("-Password")){
                    initializeAgents(false);
                    new MainScreen();
                    this.stage.close();
                }else{
                    if(counter ==1 ) {
                        left.setText("Username or password is wrong, 2 attempts left");
                    }else if(counter ==2 ) {
                        left.setText("Username or password is wrong, 1 attempts left");
                    }else if(counter >=3 ) {
                        left.setText("Username or password is wrong, you have used your 3 attempts");
                        System.out.println("Sorry, you have used your 3 attempts");
                        System.exit(0);
                    }
                }
            }else if(counter ==1 ) {
                left.setText("Try again, 2 attempts left");
            }else if(counter ==2 ) {
                left.setText("Try again, 1 attempts left");
            }else if(counter >=3 ) {
                left.setText("Sorry, you have used your 3 attempts");
                System.out.println("Sorry, you have used your 3 attempts");
                System.exit(0);
            }
        }
    }

    private void signup() throws Exception {
        if(user.getText().isEmpty()||user.getText().isBlank() && psw.getText().isEmpty()||psw.getText().isBlank()){
            left.setText("Sorry, username or password not possible");
        }else{
            if(!userAlreadyExists()){
                //out.println(user.getText() + " "+psw.getText());
                initializeAgents(true);
                new MainScreen();
                this.stage.close();
            }else{
                if(user.getText().isEmpty()){
                    left.setText("Sorry, username not possible");
                }else{
                    left.setText("Sorry, username already used");
                }
            }
        }
    }

    public boolean userAlreadyExists(){
        File userFile = new File("src/DataBase/Users/"+user.getText()+".txt");
        return userFile.exists();
    }

    public void initializeAgents(boolean signup){
        if(signup){
            createUser();
        }
        Data.setUsername(user.getText());
        Data.setPassword(psw.getText());

        Data.setUser(new User(user.getText(), psw.getText()));
    }

    public void createUser(){
        try {
            FileWriter writer;
            {
                try {
                    writer = new FileWriter("src/DataBase/Users/"+user.getText()+".txt");
                    PrintWriter out = new PrintWriter(writer);
                    out.println("Password: " + psw.getText());
                    out.println("Location: /");
                    writer.close();
                } catch (IOException e) {
                    left.setText("Sorry, something went wrong");
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setBackground(new Background(new BackgroundFill(new Color(0.2,0.35379, 0.65, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        menuBox.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(10))));
        menuBox.setMaxSize(520, 600);

        primaryStage = new Stage();
        root.setId("start-screen-pane");
        Background background = Data.createBackGround();
        root.setBackground(background);
        Scene scene = new Scene(root, 800, 800);
        addContent();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        this.stage = primaryStage;

        left = new Text("");
        left.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        left.setFill(Color.RED);
        left.setTranslateY(-10);
        root.getChildren().add(left);
    }

    private void addContent() {
        addTitle();
        addMenu();
        startAnimation();
    }

    private void addTitle() {
        MenuTitle title = new MenuTitle("Project 2-2 Assistant");
        title.setTranslateX(WIDTH / 2. -14);
        title.setTranslateY(HEIGHT / 3. - 25);
        root.getChildren().add(title);

        MenuTitle username = new MenuTitle("Username :");
        username.setTranslateX(WIDTH / 2. - 140);
        username.setTranslateY(HEIGHT / 3. + 37);
        root.getChildren().add(username);

        MenuTitle password = new MenuTitle("Password :");
        password.setTranslateX(WIDTH / 2. - 140);
        password.setTranslateY(HEIGHT / 3. + 87);
        root.getChildren().add(password);
    }

    private void addMenu() {
        user = new TextField();
        user.setFont(Font.font("Verdana", FontWeight.BOLD,15));
        user.setStyle("-fx-text-fill: dimgray;");
        user.setMaxWidth(200);

        psw = new PasswordField();
        psw.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        psw.setStyle("-fx-text-fill: dimgray;");
        psw.setMaxWidth(200);

        menuBox.getChildren().addAll(user, psw);

        menuData.forEach(data -> {
            Button button = new Button(data.getKey());
            button.setBackground(new Background(new BackgroundFill(Color.SLATEGREY.darker(), new CornerRadii(3,3,3,3,false), Insets.EMPTY)));
            button.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 16)));
            button.setPrefSize(200, 30);
            button.setTextFill(Color.LIGHTGRAY);
            button.setCursor(Cursor.HAND);
            button.setTranslateY(30);
            button.setOnMouseClicked(e -> data.getValue().run());

            menuBox.getChildren().add(button);
        });

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