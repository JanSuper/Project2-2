package Interface.Display.SkillEditorDisplayTools;

import DataBase.Data;
import Interface.Screens.MainScreen;
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

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class AddSkillEditorVBox extends VBox {
    private MainScreen mainScreen;

    private File dataBase = new File("src\\DataBase\\textData.txt");

    private ArrayList<String> questions;
    private ArrayList<String> answers;

    private Label qLabel;
    private TextField question;
    private Label aLabel;
    private TextField answer;
    private Label skillDisplayLabel;
    private HBox qPlus;
    private HBox aPlus;
    private Button enter;

    ObservableList<String> options =
            FXCollections.observableArrayList(
                    Data.getSkills()
            );
    final ComboBox skillDisplay = new ComboBox(options);

    public AddSkillEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        questions = new ArrayList();
        answers = new ArrayList();
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));
        setBackground(new Background(new BackgroundFill(new Color(0.08,0.12, 0.15, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));
        createContent();
        getChildren().addAll(qLabel,question,qPlus,aLabel,answer,aPlus,skillDisplayLabel,skillDisplay,enter);    }

    public void createContent(){

        qLabel = new Label("Question:");
        qLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        qLabel.setTextFill(MainScreen.themeColor.darker());
        qLabel.setAlignment(Pos.CENTER);

        question = new TextField();
        question.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        question.setPrefSize(10,50);

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


        qPlus = new HBox(70);
        setQPlusButton(qPlus);
        aPlus = new HBox(80);
        setAPlusButton(aPlus);

        enter = new Button("Enter");
        enter.setScaleX(2);enter.setScaleY(2);
        enter.setTranslateY(50);
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        enter.setOnAction(e-> {
            //TODO add all the questions and answers when the possibility to add questions and answers is implemented
            questions.add(question.getText());
            if(!answer.getText().equals("Either write an answer for a talk/discussion or select a skill to display")){
                answers.add(answer.getText());
            }
            int result = 0;
            try {
                result = handleAddSkill();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            String response = "";
            if(result == 1)
            {
                response =  "The new skill was successfully added to the database.";
                question.setText("");
                if(!answer.getText().equals("Either write an answer for a talk/discussion or select a skill to display")){
                    answer.setText("");
                }
                skillDisplay.setValue(Data.getSkills().get(0));
                questions.clear();
                answers.clear();
            }
            else if(result==-2){
                response = "Task already implemented.";
                question.setText("");
                if(!answer.getText().equals("Either write an answer for a talk/discussion or select a skill to display")){
                    answer.setText("");
                }
                skillDisplay.setValue(Data.getSkills().get(0));
                questions.clear();
                answers.clear();
            }
            else
            {
                response =  "Sorry something went wrong, the new skill could not be added to the database";
            }
            System.out.println(response);
        });
    }

    public void setQPlusButton(HBox box){
        //TODO add the possibility to add more questions for the same task
    }

    public void setAPlusButton(HBox box){
        //TODO add the possibility to add more responses for the same task
    }

    public int handleAddSkill() throws IOException {
        int success = -1;
        if(skillAlreadyIn(questions)){
            success = -2;
        }
        else
        {

            try{
                BufferedWriter newData = new BufferedWriter(new FileWriter(dataBase, true));
                for(int j = 0; j <= questions.size()-1; j++)
                {
                    newData.append("U " + questions.get(j) + System.lineSeparator());
                }
                if(answers.size()>0){
                    for(int y = 0; y <= answers.size()-1; y++)
                    {
                        newData.append("B " + answers.get(y) + System.lineSeparator());
                    }
                }else{
                    newData.write("B " + skillToDisplay() + System.lineSeparator());
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

    public boolean skillAlreadyIn(ArrayList uQuestion) throws IOException {
        String actual = Files.readString(dataBase.toPath()).toLowerCase();
        for(int j = 0; j <= uQuestion.size()-1; j++)
        {
            if(actual.contains((String) uQuestion.get(j))){
                return true;
            }
        }
        return false;
    }
}

