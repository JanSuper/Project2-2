package Interface.Display.SkillEditorDisplayTools;

import Interface.Screens.MainScreen;
import SkillEditor.SkillEditorHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;

public class EditSkillEditorVBox extends VBox {
    private MainScreen mainScreen;
    private SkillEditorHandler skillEditor;

    private Label skillDisplayLabel;
    private ObservableList<String> options1;
    private ComboBox skills;
    private ObservableList<String> options2;
    private ComboBox tasks;
    private ObservableList<String> options3;
    private ComboBox sentences;
    private HBox options;
    private Label editLabel;
    private TextField editTextField;
    private Button edit;
    private Button delete;
    private HBox buttons;

    public EditSkillEditorVBox(MainScreen mainScreen) throws IOException {
        this.mainScreen = mainScreen;
        skillEditor = new SkillEditorHandler();

        setSpacing(25);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));
        createContent();
        getChildren().addAll(skillDisplayLabel, options, editLabel, editTextField, buttons);
    }

    public void createContent() throws IOException {
        skillDisplayLabel = new Label("Select Skill-Task-Sentence to edit/delete:");
        skillDisplayLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        skillDisplayLabel.setTextFill(MainScreen.themeColor.darker());
        skillDisplayLabel.setAlignment(Pos.CENTER);
        skillDisplayLabel.setPadding(new Insets(35, 0, 15, 0));

        options1 = FXCollections.observableArrayList(skillEditor.getMainSkills());
        skills = new ComboBox(options1);
        skills.setValue(options1.get(0));
        skills.setOnAction(event -> {
            options2.setAll(FXCollections.observableArrayList(skillEditor.getTasks((String) skills.getValue())));
            tasks.setValue(options2.get(0));
        });

        options2 = FXCollections.observableArrayList(skillEditor.getTasks((String) skills.getValue()));
        tasks = new ComboBox(options2);
        tasks.setValue(options2.get(0));
        tasks.setOnAction(event -> {
            try {
                options3.setAll(FXCollections.observableArrayList(skillEditor.getSentences((String) tasks.getValue())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            sentences.setValue(options3.get(0));
        });

        options3 = FXCollections.observableArrayList(skillEditor.getSentences((String) tasks.getValue()));
        sentences = new ComboBox(options3);
        sentences.setValue(options3.get(0));

        options = new HBox();
        options.setSpacing(20);
        options.setAlignment(Pos.CENTER);
        options.getChildren().addAll(skills, tasks, sentences);
        options.setPadding(new Insets(0, 0, 28, 0));

        editLabel = new Label("Edit:");
        editLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        editLabel.setTextFill(MainScreen.themeColor.darker());
        editLabel.setAlignment(Pos.CENTER);

        editTextField = new TextField();
        editTextField.setText(sentences.getValue().toString());
        sentences.setOnAction(e -> editTextField.setText(sentences.getValue().toString()));
        editTextField.setMinSize(780, 36);
        editTextField.setMaxSize(780, 36);
        editTextField.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

        edit = new Button("Edit");
        edit.setPrefSize(100, 60);
        edit.setMinSize(100, 60);
        edit.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        edit.setCursor(Cursor.HAND);
        edit.setTextFill(Color.LIGHTGRAY);
        edit.setBackground(new Background(new BackgroundFill(Color.GREEN.darker(), new CornerRadii(90, true), Insets.EMPTY)));
        edit.setOnAction(e -> {
        });   //TODO

        Label or = new Label("or");
        or.setFont(Font.font("Tahoma", FontWeight.BOLD, 23));
        or.setTextFill(MainScreen.themeColor.darker());

        delete = new Button("Delete");
        delete.setPrefSize(100, 60);
        delete.setMinSize(100, 60);
        delete.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));
        delete.setCursor(Cursor.HAND);
        delete.setTextFill(Color.LIGHTGRAY);
        delete.setBackground(new Background(new BackgroundFill(Color.RED.darker(), new CornerRadii(90, true), Insets.EMPTY)));
        delete.setOnAction(e -> {
        }); //TODO

        buttons = new HBox(50);
        buttons.setTranslateY(40);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(edit, or, delete);
    }
}