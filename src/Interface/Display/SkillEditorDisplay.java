package Interface.Display;

import Interface.Display.SkillEditorDisplayTools.AddSkillEditorVBox;
import Interface.Display.SkillEditorDisplayTools.EditSkillEditorVBox;
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
    public Button editSkill;

    private HBox tabs;
    public Button prevTab;

    private AddSkillEditorVBox addSkillEditorVBox;
    private EditSkillEditorVBox editSkillEditorVBox;


    public SkillEditorDisplay(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        addSkillEditorVBox = new AddSkillEditorVBox(this.mainScreen);
        editSkillEditorVBox = new EditSkillEditorVBox(this.mainScreen);
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

        editSkill = new Button("Edit Skill");
        designTab(editSkill);

        Button exit = new Button("x");
        exit.setCursor(Cursor.HAND);
        exit.setBackground(Background.EMPTY);
        exit.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        exit.setTextFill(Color.DARKRED);
        exit.setBorder(null);
        exit.setAlignment(Pos.CENTER);
        exit.setTranslateY(-17);
        exit.setTranslateX(-2);
        exit.setOnAction(e -> mainScreen.setOptionsMenu());

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        tabs.getChildren().addAll(addSkill, editSkill, region, exit);
        selectTab(editSkill);
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
            case "Edit Skill": setEditSkillView(); break;
        }
    }

    public void deselectTab(Button prevTab) {
        prevTab.setBackground(Background.EMPTY);
        prevTab.setTextFill(Color.LIGHTGRAY);
        prevTab.setBorder(null);

        switch(prevTab.getText()) {
            case "Add Skill": getChildren().remove(addSkillEditorVBox); break;
            case "Edit Skill": getChildren().remove(editSkillEditorVBox); break;
        }
    }

    private void setAddSkillView(){
        getChildren().add(addSkillEditorVBox);
    }
    private void setEditSkillView(){
        getChildren().add(editSkillEditorVBox);
    }


}
