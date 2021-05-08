package Interface.Display.SkillEditorDisplayTools;

import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextField;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class AddSkillEditorVBox extends VBox {
    private MainScreen mainScreen;

    private File dataBase = new File("src\\DataBase\\textData.txt");

    private ArrayList<String> questions;

    private VBox allQuestions;
    private ScrollPane qScroll;

    private HBox howManyQ;
    private Spinner<Integer> spinner;
    private int oldValue;
    private Label qLabel;
    private Label aLabel;
    private TextField answer;
    private Label skillDisplayLabel;
    private Button enter;
    ObservableList<String> options =
            FXCollections.observableArrayList(
                    Data.getSkills()
            );
    final ComboBox skillDisplay = new ComboBox(options);

    public AddSkillEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        questions = new ArrayList();
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));
        createContent();
        getChildren().addAll(howManyQ,qScroll,aLabel,answer,skillDisplayLabel,skillDisplay,enter);
    }

    public void createContent(){

        spinner = new Spinner<Integer>();
        int initialValue = 0;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, initialValue);
        spinner.setValueFactory(valueFactory);
        oldValue = initialValue;
        spinner.setOnMouseClicked(event -> {
            int newVal = spinner.getValue();
            if(newVal>oldValue){
                addQuestion();
            }else if(newVal<oldValue){
                allQuestions.getChildren().remove(allQuestions.getChildren().size()-1);
            }
            if(oldValue==5){
                qScroll.setMaxHeight(qScroll.getHeight());
            }
            oldValue = newVal;
        });

        qLabel = new Label("Question:");
        qLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        qLabel.setTextFill(MainScreen.themeColor.darker());
        qLabel.setAlignment(Pos.CENTER);

        howManyQ = new HBox();
        howManyQ.setSpacing(20);
        howManyQ.setPadding(new Insets(0,100,0,300));
        howManyQ.getChildren().addAll(qLabel,spinner);

        allQuestions = new VBox();
        allQuestions.setSpacing(10);
        allQuestions.setAlignment(Pos.CENTER);

        qScroll = new ScrollPane(allQuestions);
        qScroll.setBackground(getBackground());

        aLabel = new Label("Answer:");
        aLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        aLabel.setTextFill(MainScreen.themeColor.darker());
        aLabel.setAlignment(Pos.CENTER);

        answer = new TextField("Either write an answer for a talk/discussion or select a skill to display");
        answer.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        answer.setPrefSize(10,50);

        skillDisplayLabel = new Label("Which skill displays:");
        skillDisplayLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        skillDisplayLabel.setTextFill(MainScreen.themeColor.darker());
        skillDisplayLabel.setAlignment(Pos.CENTER);

        skillDisplay.setValue(Data.getSkills().get(0));

        enter = new Button("Enter");
        enter.setScaleX(2);enter.setScaleY(2);
        enter.setTranslateY(50);
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        enter.setOnAction(e-> {
            for (Node node:allQuestions.getChildren()) {
                TextField question = (TextField) node;
                if (question.getText().equals("If you wish to include a variable, please replace it by <VARIABLE>")) {
                    mainScreen.chat.receiveMessage("Please write a correct question");
                } else {
                    questions.add(question.getText());
                    int result = 0;
                    try {
                        result = handleAddSkill(question.getText());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    String response = "";
                    if (result == 1) {
                        response = "The new skill was successfully added to the database.";
                        question.setText("If you wish to include a variable, please replace it by <VARIABLE>");
                        answer.setText("Either write an answer for a talk/discussion or select a skill to display");
                        skillDisplay.setValue(Data.getSkills().get(0));
                        questions.clear();
                    } else {
                        response = "Sorry something went wrong, the new skill could not be added to the database";
                    }
                    mainScreen.chat.receiveMessage(response);
                }
            }
        });
    }

    private void addQuestion(){
        TextField question = new TextField("If you wish to include a variable, please replace it by <VARIABLE>");
        question.setPrefSize(qScroll.getWidth()-20,20);
        question.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        allQuestions.getChildren().add(question);
    }

    public int handleAddSkill(String question) throws IOException {
        int success = -1;
        try{
            BufferedWriter newData = new BufferedWriter(new FileWriter(dataBase, true));
            newData.append("U " + question + System.lineSeparator());
            if(answer.getText().equals("Either write an answer for a talk/discussion or select a skill to display")){
                newData.append("B " + skillToDisplay() + System.lineSeparator());
            }else{
                newData.append("B " + answer.getText() + System.lineSeparator());
            }
            success = 1;
            newData.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }
    //"Talk/Discussion","Weather","Clock","Calendar","Media Player","Skill Editor"
    public String skillToDisplay(){
        String displayNbr = "";
        if(skillDisplay.getValue().equals("Weather")){
            displayNbr = "1";
        }else if(skillDisplay.getValue().equals("Clock")){
            displayNbr = "20";
        }else if(skillDisplay.getValue().equals("Calendar")){
            displayNbr = "10";
        }else if(skillDisplay.getValue().equals("Media Player")){
            displayNbr = "50";
        }else if(skillDisplay.getValue().equals("Skill Editor")){
            displayNbr = "30";
        }
        return displayNbr;
    }
}

