package Interface.Chat;

import DataBase.Data;
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
    private ScrollPane scroller;
    private HBox typeField;
    private String lastMessage;
    private TextField userInput;
    private Image image;

    public static class MessageBubble extends HBox {
        private Background userBubbleBackground;
        private Background assistantBubbleBackground;

        public MessageBubble(String message, int direction) {
            userBubbleBackground = new Background(new BackgroundFill(new Color(0,0.47379,1,1), new CornerRadii(7,0,7,7,false), Insets.EMPTY));
            assistantBubbleBackground = new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(0,7,7,7,false), Insets.EMPTY));
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
        super.setBackground(new Background(new BackgroundFill(new Color(0,0.47379,1,1), CornerRadii.EMPTY, Insets.EMPTY)));
        super.setPrefWidth(590);

        Label userNameLabel = new Label(userName);
        userNameLabel.setAlignment(Pos.CENTER);
        userNameLabel.setTranslateX(10);
        userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 20)));
        userNameLabel.setStyle("-fx-text-fill: white");

        FileInputStream fis = new FileInputStream("src/res/userIcon.jpg");
        image = new Image(fis,26,26,false,true);
        ImageView userIcon = new ImageView(image);

        HBox user = new HBox();
        user.getChildren().addAll(userIcon, userNameLabel);

        createComponents();
        getChildren().setAll(user, scroller, typeField);
        setPadding(new Insets(40));
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
        userInput.setPrefWidth(440);
        userInput.setTranslateY(12);
        userInput.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGRAY.brighter(), new CornerRadii(3,3,3,3,false), Insets.EMPTY)));
        userInput.setFont((Font.font("Cambria", 14)));
        userInput.setStyle("-fx-text-fill: black; -fx-prompt-text-fill: white");
        userInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                sendMessage(userInput.getText());
                userInput.setText("");
            }
        });

        Button sendMessageButton = new Button(">>");
        sendMessageButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        sendMessageButton.setTextFill(Color.LIGHTSEAGREEN);
        sendMessageButton.setBackground(null);
        sendMessageButton.setBorder(null);
        sendMessageButton.setCursor(Cursor.HAND);
        sendMessageButton.setTranslateY(5);

        sendMessageButton.disableProperty().bind(userInput.lengthProperty().isEqualTo(0));
        sendMessageButton.setOnAction(event-> {
            sendMessage(userInput.getText());
            userInput.setText("");
        });

        //For testing
            /*Button receiveMessageButton = new Button("Receive");
            receiveMessageButton.setTranslateY(6);
            receiveMessageButton.disableProperty().bind(userInput.lengthProperty().isEqualTo(0));
            receiveMessageButton.setOnAction(event-> {
                receiveMessage(userInput.getText());
                userInput.setText("");
            });*/

        typeField.getChildren().setAll(userInput, sendMessageButton); //, receiveMessageButton);
    }

    public void sendMessage(String message) {
        messages.add(new MessageBubble(message, 1));
        lastMessage = message;
    }

    public void receiveMessage(String message) {    //adds assistant's response
        messages.add(new MessageBubble(message, 0));
    }

    public String getLastMessage() {    //returns the user's last message
        return lastMessage;
    }
}