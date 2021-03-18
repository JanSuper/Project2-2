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

public class EditSkillEditorVBox extends VBox {
    private MainScreen mainScreen;
    private HBox top;
    private VBox principal;

    private File dataBase = new File("src\\DataBase\\textData.txt");

    private ArrayList<String> questions;
    private ArrayList<String> answers;

    public EditSkillEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        questions = new ArrayList();
        answers = new ArrayList();
        setBackground(new Background(new BackgroundFill(new Color(0.08,0.12, 0.15, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));
        createContent();
        getChildren().add(principal);
    }

    public void createContent(){
        principal = new VBox(25);
        principal.setBackground(Background.EMPTY);
        principal.setAlignment(Pos.CENTER);
        principal.setPadding(new Insets(15));
        createTop();

        Label qLabel = new Label("Question:");
        qLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        qLabel.setTextFill(MainScreen.themeColor.darker());
        qLabel.setAlignment(Pos.CENTER);

        TextField question = new TextField();
        question.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        question.setPrefSize(10,50);
        question.setScaleX(0.5);question.setScaleY(0.5);

        Label aLabel = new Label("Answer:");
        aLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        aLabel.setTextFill(MainScreen.themeColor.darker());
        aLabel.setAlignment(Pos.CENTER);

        TextField answer = new TextField();
        answer.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        answer.setPrefSize(10,50);
        answer.setScaleX(0.5);answer.setScaleY(0.5);

        Label skillDisplayLabel = new Label("Which skill displays:");
        skillDisplayLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        skillDisplayLabel.setTextFill(MainScreen.themeColor.darker());
        skillDisplayLabel.setAlignment(Pos.CENTER);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        Data.getSkills()
                );
        final ComboBox skillDisplay = new ComboBox(options);
        skillDisplay.setValue(Data.getSkills().get(0));


        HBox qPlus = new HBox(70);
        setQPlusButton(qPlus);
        HBox aPlus = new HBox(80);
        setAPlusButton(aPlus);

        Button enter = new Button("Enter");
        enter.setScaleX(2);enter.setScaleY(2);
        enter.setTranslateY(50);
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        enter.setOnAction(e-> {
            //TODO add all the questions and answers when the possibility to add questions and answers is implemented
            questions.add(question.getText());
            answers.add(answer.getText());
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
                answer.setText("");
                skillDisplay.setValue(Data.getSkills().get(0));
                questions.clear();
                answers.clear();
            }
            else if(result==-2){
                response = "Task already implemented.";
                question.setText("");
                answer.setText("");
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

        principal.getChildren().addAll(top,qLabel,question,qPlus,aLabel,answer,aPlus,skillDisplayLabel,skillDisplay,enter);
    }

    public void createTop(){
        top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.setPrefHeight(80);
        top.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Skill Editor");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);

        Region region1 = new Region();
        HBox.setHgrow(region1, Priority.ALWAYS);

        Region region2 = new Region();
        HBox.setHgrow(region2, Priority.ALWAYS);

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.CENTER_RIGHT);
        exit.setTranslateY(-17);
        exit.setTranslateX(-2);
        exit.setOnAction(e -> mainScreen.setOptionsMenu());

        top.getChildren().addAll(region1,title,region2,exit);
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
                for(int y = 0; y <= answers.size()-1; y++)
                {
                    newData.append("B " + answers.get(y) + System.lineSeparator());
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

