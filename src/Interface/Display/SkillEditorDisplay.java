package Interface.Display;

import Interface.Display.SkillEditorDisplayTools.AddSkillEditorVBox;
import Interface.Display.SkillEditorDisplayTools.AddRuleEditorVBox;
import Interface.Screens.MainScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SkillEditorDisplay extends VBox {
    private MainScreen mainScreen;

    public Button addSkill;
    public Button addRule;

    private HBox tabs;
    public Button prevTab;

    private AddSkillEditorVBox addSkillEditorVBox;
    private AddRuleEditorVBox addRuleEditorVBox;


    public SkillEditorDisplay(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        addSkillEditorVBox = new AddSkillEditorVBox(this.mainScreen);
        addRuleEditorVBox = new AddRuleEditorVBox(this.mainScreen);
        setBackground(new Background(new BackgroundFill(new Color(0.08,0.12, 0.15, 0.3), CornerRadii.EMPTY, Insets.EMPTY)));
        createContent();
        getChildren().add(tabs);
    }

    public void createContent(){
        tabs = new HBox(10);
        tabs.setAlignment(Pos.CENTER);
        tabs.setPrefHeight(80);
        tabs.setBackground(new Background(new BackgroundFill(MainScreen.themeColor, CornerRadii.EMPTY, Insets.EMPTY)));

        addSkill = new Button("Add Skill");
        designTab(addSkill);

        addRule = new Button("Add Rule");
        designTab(addRule);

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.CENTER);
        exit.setTranslateY(-17);
        exit.setTranslateX(-2);
        exit.setOnAction(e -> {
            try {
                mainScreen.setMenu("MainMenu");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        tabs.getChildren().addAll(addSkill, addRule, region, exit);
    }

    private void designTab(Button tab) {
        tab.setCursor(Cursor.HAND);
        tab.setBackground(Background.EMPTY);
        tab.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        tab.setTextFill(Color.LIGHTGRAY);
        tab.setPrefSize(160, 80);
        tab.setAlignment(Pos.CENTER);
        tab.setOnAction(e -> {deselectTab(prevTab); selectTab(tab);});
    }

    public void selectTab(Button selectedTab) {
        prevTab = selectedTab;
        selectedTab.setBackground(new Background(new BackgroundFill(MainScreen.themeColor.darker(), CornerRadii.EMPTY, Insets.EMPTY)));
        selectedTab.setTextFill(Color.LIGHTGRAY.brighter());
        selectedTab.setBorder(new Border(new BorderStroke(Color.LIGHTSLATEGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        switch(selectedTab.getText()) {
            case "Add Skill": setAddSkillView(); break;
            case "Add Rule": setEditSkillView(); break;
        }
    }

    public void deselectTab(Button prevTab) {
        prevTab.setBackground(Background.EMPTY);
        prevTab.setTextFill(Color.LIGHTGRAY);
        prevTab.setBorder(null);

        switch(prevTab.getText()) {
            case "Add Skill": getChildren().remove(addSkillEditorVBox); break;
            case "Add Rule": getChildren().remove(addRuleEditorVBox); break;
        }
    }

    private void setAddSkillView(){
        mainScreen.chat.assistant_answer.textRecognition.skillEdit = true;
        mainScreen.chat.assistant_answer.textRecognition.ruleEdit = false;
        getChildren().add(addSkillEditorVBox);
        mainScreen.chat.receiveMessage("To add a new skill to the assistant you have to follow these rules:" + System.lineSeparator() +
                "1. Write down the question(s) you will ask to the assistant. If there is more than one question (for the same answer) make sure to separate them with a comma , " + System.lineSeparator() +
                "2. After the question(s) add a semicolon ; " + System.lineSeparator() +
                "3. Write down the answer you want from the assistant, either write a sentence for a chat/talk or the number of an operation (if you which to see all the possible operations, please write \"See all possible operations\")." + System.lineSeparator() +
                "4. Send everything into one message." +System.lineSeparator() +
                "If you don't want to add a skill write: Cancel");
    }
    private void setEditSkillView(){
        mainScreen.chat.assistant_answer.textRecognition.ruleEdit = true;
        mainScreen.chat.assistant_answer.textRecognition.skillEdit = false;
        getChildren().add(addRuleEditorVBox);
        mainScreen.chat.receiveMessage("To add a new rule, write in the form: LeftHandSide:RightHandSide1,RightHandSide2,...,RightHandSideN and decide, whether the rule is terminal or not by writing + for terminal and - for non terminal at the end of the message."+System.lineSeparator() +
                "If you don't want to add a skill write: Cancel");
    }


}
