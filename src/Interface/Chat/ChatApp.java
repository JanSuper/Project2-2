package Interface.Chat;

import Agents.Assistant;
import DataBase.Data;
import Interface.Display.CalendarDisplay;
import Interface.Display.MediaPlayerDisplay;
import Interface.Screens.MainScreen;
import Skills.Schedule.Skill_Schedule;
import Skills.SkillEditor;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class ChatApp extends VBox {
    public ObservableList messages = FXCollections.observableArrayList();
    private HBox user;
    private ScrollPane scroller;
    private HBox typeField;
    private TextField userInput;
    private Button sendMessageButton;
    private Image image;
    private Color themeColor = MainScreen.themeColor;
    private List<String>userMessages;
    private List<String>assistantMessages;
    private Assistant assistant_answer;

    private SkillEditor skillEditor;
    private MainScreen mainScreen;

    public class MessageBubble extends HBox {
        private Background userBubbleBackground;
        private Background assistantBubbleBackground;

        public MessageBubble(String message, int direction) {
            if(direction==0){
                assistantMessages.add(message);
            }else{
                userMessages.add(message);
            }
            userBubbleBackground = new Background(new BackgroundFill(Color.GRAY.darker(), new CornerRadii(7,0,7,7,false), Insets.EMPTY));
            assistantBubbleBackground = new Background(new BackgroundFill(themeColor, new CornerRadii(0,7,7,7,false), Insets.EMPTY));
            createLabel(message, direction);
            skillEditor = new SkillEditor();
        }

        private void createLabel(String message, int direction) {
            Label messageLabel = new Label(message);
            messageLabel.setPadding(new Insets(6));
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setWrapText(true);
            messageLabel.setFont((Font.font("Cambria", 17)));
            messageLabel.maxWidthProperty().bind(widthProperty().multiply(0.75));
            messageLabel.setTranslateY(5);

            if(direction == 0){
                messageLabel.setBackground(assistantBubbleBackground);
                messageLabel.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setTranslateX(10);
                setAlignment(Pos.CENTER_LEFT);
            }
            else{
                messageLabel.setBackground(userBubbleBackground);
                messageLabel.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setTranslateX(-10);
                setAlignment(Pos.CENTER_RIGHT);
            }
            getChildren().setAll(messageLabel);
        }
    }

    public ChatApp(String userName,MainScreen mainScreen) throws Exception {
        super(7);
        super.setBackground(new Background(new BackgroundFill(themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        userMessages = new ArrayList<>();
        assistantMessages = new ArrayList<>();
        this.mainScreen = mainScreen;

        assistant_answer = new Assistant(this.mainScreen, userName, assistantMessages);

        Label userNameLabel = new Label(userName);
        userNameLabel.setAlignment(Pos.CENTER);
        userNameLabel.setTranslateX(10);
        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        userNameLabel.setStyle("-fx-text-fill: white");

        FileInputStream fis = new FileInputStream("src/res/userIcon.png");
        image = new Image(fis,25,25,true,true);
        ImageView userIcon = new ImageView(image);

        user = new HBox(20);
        user.getChildren().addAll(userIcon, userNameLabel);

        createComponents();
        getChildren().setAll(user, scroller, typeField);
        setPadding(new Insets(40));
        setMaxHeight(Double.MAX_VALUE);
        setMinHeight(Double.MIN_VALUE);
        receiveMessage("Welcome " + Data.getUsername() + "! How may I help you?"); //Assistant's first message
    }

    private void createComponents() {
        createMessageView();
        createInputView();
    }

    private void createMessageView() {
        VBox messagesBox = new VBox(6);
        Bindings.bindContentBidirectional(messages, messagesBox.getChildren());

        scroller = new ScrollPane(messagesBox);
        scroller.setPrefHeight(750);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background: transparent; -fx-background-color: lightslategray;");
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroller.vvalueProperty().bind(messagesBox.heightProperty());   //updating scroller
    }

    private void createInputView() {
        typeField = new HBox(7);

        userInput = new TextField();
        userInput.setPromptText("Type message");
        userInput.setPrefWidth(380);
        userInput.setTranslateY(12);
        userInput.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(3,3,3,3,false), Insets.EMPTY)));
        userInput.setFont((Font.font("Cambria", 14)));
        userInput.setStyle("-fx-text-fill: black; -fx-prompt-text-fill: gray");
        userInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                try {
                    sendMessage(userInput.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userInput.setText("");
            }
        });

        sendMessageButton = new Button(">>");
        sendMessageButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        sendMessageButton.setTextFill(Color.LIGHTSEAGREEN.brighter());
        sendMessageButton.setBackground(null);
        sendMessageButton.setBorder(null);
        sendMessageButton.setCursor(Cursor.HAND);
        sendMessageButton.setTranslateY(5);

        sendMessageButton.disableProperty().bind(userInput.lengthProperty().isEqualTo(0));
        sendMessageButton.setOnAction(event-> {
            try {
                sendMessage(userInput.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            userInput.setText("");
        });
        typeField.getChildren().setAll(userInput, sendMessageButton);
    }

    public void sendMessage(String message) throws Exception {
        messages.add(new MessageBubble(message, 1));
        assistant_answer.setAssistantMessage(assistantMessages);

        /*if(message.toLowerCase().contains("create skill")){
            messages.add(new MessageBubble("Please enter the title of the new skill",0));
        }
        else
        if(assistantMessages.get(assistantMessages.size()-1).equals("Please enter the title of the new skill")||
                assistantMessages.get(assistantMessages.size()-1).equals("Please remove the space in the new skill")
        ){
            if(!message.contains(" ")){
                if(!skillEditor.createSkill(message)){
                    messages.add(new MessageBubble("Couldn't create the new skill for some reasons",0));
                }
            }else{
                messages.add(new MessageBubble("Please remove the space in the new skill",0));
            }
        }*/
        if(message.toLowerCase().contains("change password")){
            messages.add(new MessageBubble("Please enter a new password",0));
        }
        else if(assistantMessages.get(assistantMessages.size()-1).equals("Please enter a new password")||
                assistantMessages.get(assistantMessages.size()-1).equals("Please remove the space in the password")
        ){
            if(!message.contains(" ")){
                if(!changePassword(message)){
                    messages.add(new MessageBubble("Couldn't change the password for some reasons",0));
                }
            }else{
                messages.add(new MessageBubble("Please remove the space in the password",0));
            }
        }
        else if(message.toLowerCase().contains("media player")) {
            messages.add(new MessageBubble("Do you want a \"local file\" or a \"url\" ? ", 0));
        }
        else if(assistantMessages.get(assistantMessages.size()-1).equals("Do you want a \"local file\" or a \"url\" ? ")){
            if(message.toLowerCase().contains("local file")){
                messages.add(new MessageBubble("Please choose a file",0));
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(mainScreen.stage);
                try {
                    Media media = new Media (selectedFile.toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setAutoPlay(true);
                    MediaPlayerDisplay mediaControl = new MediaPlayerDisplay(mediaPlayer);
                    mainScreen.setMediaPlayerDisplay(mediaControl);
                } catch(NullPointerException e){
                    messages.add(new MessageBubble("No file chosen",0));
                } catch(MediaException e){
                    messages.add(new MessageBubble("filetype not supported",0));
                }
            }else if(message.toLowerCase().contains("url")){
                messages.add(new MessageBubble("Please write the url of the video",0));
            }
        }
        else if(assistantMessages.get(assistantMessages.size()-1).equals("Please write the url of the video")){
            WebView webview = new WebView();
            webview.getEngine().load(
                    message
            );

            Pane pane = new Pane();
            pane.getChildren().add(webview);
            mainScreen.setMediaPlayerDisplay(pane);
        }
        else if(message.toLowerCase().contains("clock")){
            mainScreen.setClockAppDisplay();
        }
        else if(message.toLowerCase().contains("calendar")){
            mainScreen.setCalendarDisplay(new CalendarDisplay(mainScreen));
        }

        else if (message.toLowerCase().contains("look up")){
            messages.add(new MessageBubble("Please enter your search",0));

        }
        else if(assistantMessages.get(assistantMessages.size()-1).equals("Please enter your search")){
            String searchURL = "https://www.google.com/search" + "?q=" + message;
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome.exe " + searchURL});
            messages.add(new MessageBubble("Done!",0));
        }

        else
        {
            receiveMessage(assistant_answer.getResponse(message));
        }
    }

    public boolean changePassword(String message){
        System.out.println(Data.getPassword());
        String[][]dataset = Data.getDataSet();
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                System.out.println(dataset[i][j]);
                if(dataset[i][j].equals(Data.getPassword())&&j == 1){
                    dataset[i][j] = message;
                    Data.setPassword(message);
                    rewriteUsers(dataset);
                    System.out.println("new password " + message);
                    return true;
                }
            }
        }
        return false;
    }

    public void rewriteUsers(String[][]dataset){
        FileWriter writer;
        {
            try {
                writer = new FileWriter(Data.getUsersFile());
                PrintWriter out = new PrintWriter(writer);
                for (int i = 0; i < dataset.length; i++) {
                    for (int j = 0; j < dataset[i].length; j++) {
                        if(j==1){
                            out.print(dataset[i][j]);
                        }else{
                            out.print(dataset[i][j] + " ");
                        }
                    }
                    out.println();
                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveMessage(String message) {    //adds assistant's response
        MessageBubble messageBubble = new MessageBubble(message, 0);
        if(messages.isEmpty()) {    //fixing position of first message
            messageBubble.setAlignment(Pos.TOP_LEFT);
        }
        messages.add(messageBubble);
    }

    public List getAssistantMessage()
    {
        return assistantMessages;
    }
}