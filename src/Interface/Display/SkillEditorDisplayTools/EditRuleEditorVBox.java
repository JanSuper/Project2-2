package Interface.Display.SkillEditorDisplayTools;

import CFGrammar.JsonReader;
import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class EditRuleEditorVBox extends VBox {
    private MainScreen mainScreen;
    private JsonReader jsonReader;
    private VBox principal;

    private File dataBase = new File("src\\DataBase\\textData.txt");

    private ArrayList<String> questions;
    private ArrayList<String> answers;

    public EditRuleEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        jsonReader=new JsonReader();

        questions = new ArrayList();
        answers = new ArrayList();
        createContent();
        getChildren().add(principal);
    }

    public void createContent(){
        principal = new VBox(40);
        principal.setBackground(Background.EMPTY);
        principal.setAlignment(Pos.CENTER);
        principal.setPadding(new Insets(15));

        Label qLabel = new Label("Rule:");
        qLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        qLabel.setTextFill(MainScreen.themeColor.darker());
        qLabel.setAlignment(Pos.CENTER);

        TextField rule = new TextField();
        rule.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        rule.setPrefSize(10,50);


        CheckBox isTerminal = new CheckBox("Is terminal");
        isTerminal.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        isTerminal.setTextFill(MainScreen.themeColor.darker());
        isTerminal.setAlignment(Pos.CENTER);

        Button enter = new Button("Add");
        enter.setScaleX(2);enter.setScaleY(2);
        enter.setTranslateY(50);
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        enter.setOnAction(e-> {
            try {
                jsonReader.addRules(rule.getText(),isTerminal.isSelected());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        principal.getChildren().addAll(qLabel,rule,isTerminal,enter);
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

