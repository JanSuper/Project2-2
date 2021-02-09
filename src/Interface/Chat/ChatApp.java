package Interface.Chat;

import DataBase.Data;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class ChatApp {
    public Parent root;

    public ChatApp() {
        root = new ChatAppComponents(Data.getUsername());
    }

    public static class MessageBubble extends HBox {
        private Background userBubbleBackground;
        private Background assistantBubbleBackground;

        public MessageBubble(String message, int direction) {
            userBubbleBackground = new Background(new BackgroundFill(Color.GRAY, new CornerRadii(7,0,7,7,false), Insets.EMPTY));
            assistantBubbleBackground = new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(0,7,7,7,false), Insets.EMPTY));
            createLabel(message, direction);
        }

        private void createLabel(String message, int direction) {
            Label messageLabel = new Label(message);
            messageLabel.setPadding(new Insets(6));
            messageLabel.setTextFill(Color.WHITE);
            messageLabel.setWrapText(true);
            messageLabel.setFont((Font.font("Cambria", 15)));
            messageLabel.maxWidthProperty().bind(widthProperty().multiply(0.75));

            if(direction == 0){
                messageLabel.setBackground(assistantBubbleBackground);
                messageLabel.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setTranslateX(7);
                setAlignment(Pos.CENTER_LEFT);
            }
            else{
                messageLabel.setBackground(userBubbleBackground);
                messageLabel.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setTranslateX(-7);
                setAlignment(Pos.CENTER_RIGHT);
            }
            getChildren().setAll(messageLabel);
        }
    }

    public static class ChatAppComponents extends VBox {
        private ObservableList<Node> messages = FXCollections.observableArrayList();
        private ScrollPane scroller;
        private HBox typeField;
        private String lastMessage;

        private TextField userInput;

        public ChatAppComponents(String userName) {
            super(7);
            super.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            super.setPrefSize(480, 680);

            Label userNameLabel = new Label(userName);
            userNameLabel.setAlignment(Pos.CENTER);
            userNameLabel.setTranslateX(10);
            userNameLabel.setFont((Font.font("Cambria", FontWeight.EXTRA_BOLD, 17)));
            userNameLabel.setStyle("-fx-text-fill: white");

            createComponents();
            getChildren().setAll(userNameLabel, scroller, typeField);
            setPadding(new Insets(10));
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
            scroller.setPrefHeight(590);
            scroller.setFitToWidth(true);
            scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scroller.vvalueProperty().bind(messagesBox.heightProperty());   //updating scroller
        }

        private void createInputView() {
            typeField = new HBox(7);

            userInput = new TextField();
            userInput.setPromptText("Enter message");
            userInput.setPrefWidth(390);
            userInput.setTranslateY(6);
            userInput.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ENTER){
                    sendMessage(userInput.getText());
                    userInput.setText("");
                }
            });

            Button sendMessageButton = new Button(">>");
            sendMessageButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 15));
            sendMessageButton.setTextFill(Color.LIGHTSEAGREEN);
            sendMessageButton.setBackground(null);
            sendMessageButton.setBorder(null);
            sendMessageButton.setCursor(Cursor.HAND);
            sendMessageButton.setTranslateY(6);

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
}