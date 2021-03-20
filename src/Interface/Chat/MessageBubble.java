package Interface.Chat;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MessageBubble extends HBox {
    private final ChatApp chatApp;
    private Background userBubbleBackground;
    private Background assistantBubbleBackground;

    public MessageBubble(ChatApp chatApp, String message, int direction) {
        this.chatApp = chatApp;
        if (direction == 0) {
            chatApp.assistantMessages.add(message);
        } else {
            chatApp.userMessages.add(message);
        }
        userBubbleBackground = new Background(new BackgroundFill(Color.GRAY.darker(), new CornerRadii(7, 0, 7, 7, false), Insets.EMPTY));
        assistantBubbleBackground = new Background(new BackgroundFill(chatApp.themeColor, new CornerRadii(0, 7, 7, 7, false), Insets.EMPTY));
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

        if (direction == 0) {
            messageLabel.setBackground(assistantBubbleBackground);
            messageLabel.setAlignment(Pos.CENTER_LEFT);
            messageLabel.setTranslateX(10);
            setAlignment(Pos.CENTER_LEFT);
        } else {
            messageLabel.setBackground(userBubbleBackground);
            messageLabel.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.setTranslateX(-10);
            setAlignment(Pos.CENTER_RIGHT);
        }
        getChildren().setAll(messageLabel);
    }
}
