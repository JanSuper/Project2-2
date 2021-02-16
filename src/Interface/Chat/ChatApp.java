package Interface.Chat;

import DataBase.Data;
import Interface.Screens.MainScreen;
import Skills.Schedule.Skill_Schedule;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.IOException;


public class ChatApp extends VBox {
    private ObservableList<Node> messages = FXCollections.observableArrayList();
    private HBox user;
    private ScrollPane scroller;
    private HBox typeField;
    private TextField userInput;
    private Button sendMessageButton;
    private Image image;
    private Color themeColor = MainScreen.themeColor;

    private class MessageBubble extends HBox {
        private Background userBubbleBackground;
        private Background assistantBubbleBackground;

        public MessageBubble(String message, int direction) {
            userBubbleBackground = new Background(new BackgroundFill(Color.GRAY.darker(), new CornerRadii(7,0,7,7,false), Insets.EMPTY));
            assistantBubbleBackground = new Background(new BackgroundFill(themeColor, new CornerRadii(0,7,7,7,false), Insets.EMPTY));
            createLabel(message, direction);
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

    public ChatApp(String userName) throws IOException {
        super(7);
        super.setBackground(new Background(new BackgroundFill(themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

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

        if(message.equals("Schedule"))
        {
           String schedule_answer = new Skill_Schedule().getCourse();
           receiveMessage(schedule_answer);
        }
        else if (message.toLowerCase().contains("weather")) {
            MainScreen.setWeatherDisplay("Maastricht", "NL");
        }
    }

    public void receiveMessage(String message) {    //adds assistant's response
        MessageBubble messageBubble = new MessageBubble(message, 0);
        if(messages.isEmpty()) {    //fixing position of first message
            messageBubble.setAlignment(Pos.TOP_LEFT);
        }
        messages.add(messageBubble);
    }
}