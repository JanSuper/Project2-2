package Interface.Display.SkillEditorDisplayTools;

import CFGrammar.JsonReader;
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

import java.io.IOException;
import java.util.ArrayList;

public class EditRuleEditorVBox extends VBox {
    private MainScreen mainScreen;
    private JsonReader jsonReader;

    private Label titleLabel;
    private Label ruleLabel;
    private Button edit;
    private Button delete;
    private HBox buttons;

    private ObservableList<String> options1;
    private ComboBox lhsC;
    private ObservableList<String> options2;
    private ComboBox rhsC;


    public EditRuleEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        jsonReader=new JsonReader();
        jsonReader.getAllRules();
        jsonReader.splitRules();

        setSpacing(16);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));
        createContent();
        getChildren().addAll(titleLabel,lhsC,ruleLabel,rhsC, buttons);
    }

    private ArrayList<String> allKind(int index){
        ArrayList<String> kind = new ArrayList<>();
        if(index==-1){
            //get all RHS
            for (ArrayList<String> rhs:jsonReader.allRules) {
                kind.add(rhs.get(0));
            }
        }else{
            //get all LHS of RHS of index index
            for (int i = 0; i < jsonReader.allRules.get(index).size(); i++) {
                if(i!=0){
                    kind.add(jsonReader.allRules.get(index).get(i));
                }
            }
        }
        return kind;
    }

    public int getIndexOfRhs(String rhs){
        for (int i=0;i<jsonReader.allRules.size();i++) {
            if(rhs.equals(jsonReader.allRules.get(i).get(0))){
                return i;
            }
        }
        return -1;
    }

    public void createContent(){

        titleLabel = new Label("Title (LHS):");
        titleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        titleLabel.setTextFill(MainScreen.themeColor.darker());
        titleLabel.setAlignment(Pos.CENTER);

        options1 =
                FXCollections.observableArrayList(
                       allKind(-1)
                );
        lhsC = new ComboBox(options1);
        lhsC.setValue(options1.get(0));
        lhsC.setOnAction(event -> {
            options2.setAll(FXCollections.observableArrayList(
                    allKind(getIndexOfRhs((String) lhsC.getValue()))
            ));
            rhsC.setValue(options2.get(0));
        });

        options2 =
                FXCollections.observableArrayList(
                        allKind(getIndexOfRhs((String) lhsC.getValue()))
                );
        rhsC = new ComboBox(options2);
        rhsC.setValue(options2.get(0));

        ruleLabel = new Label("Rule (RHS):");
        ruleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        ruleLabel.setTextFill(MainScreen.themeColor.darker());
        ruleLabel.setAlignment(Pos.CENTER);

        edit = new Button("Add");
        edit.setScaleX(2);
        edit.setScaleY(2);
        edit.setBackground(new Background(new BackgroundFill(Color.ORANGE, new CornerRadii(90,true), Insets.EMPTY)));
        edit.setOnAction(event -> {
            String rule = convertToRule((String)lhsC.getValue(), (String) rhsC.getValue());
            boolean isTerminal = true;
            String newRule = " ";
            try {
                jsonReader.editRule(rule,isTerminal,newRule);
                mainScreen.chat.receiveMessage("Rule " + rule + " has been edited into " + newRule + ".");
                refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        delete = new Button("Delete");
        delete.setScaleX(2);
        delete.setScaleY(2);
        delete.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(90,true), Insets.EMPTY)));
        delete.setOnAction(event -> {
            String rule = convertToRule((String)lhsC.getValue(), (String) rhsC.getValue());
            boolean isTerminal = true;
            try {
                jsonReader.removeRule(rule,isTerminal);
                mainScreen.chat.receiveMessage("Rule " + rule + " has been removed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttons = new HBox(50);
        buttons.setTranslateY(40);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(edit,delete);
    }

    public String convertToRule(String lhs,String rhs1){
        //remove [...]
        String noBrackets = rhs1.substring( 1, rhs1.length() - 1 );
        String[] rhs = noBrackets.split(",");

        String rule = lhs + ":";
        for (int i = 0; i < rhs.length-1; i++) {
            rule+=rhs[i]+",";
        }
        rule+=rhs[rhs.length-1];

        return rule;
    }

    public void refresh(){
        jsonReader.getAllRules();
        jsonReader.splitRules();
        options1 =
                FXCollections.observableArrayList(
                        allKind(-1)
                );
        lhsC = new ComboBox(options1);
        lhsC.setValue(options1.get(0));
        lhsC.setOnAction(event -> {
            options2.setAll(FXCollections.observableArrayList(
                    allKind(getIndexOfRhs((String) lhsC.getValue()))
            ));
            rhsC.setValue(options2.get(0));
        });

        options2 =
                FXCollections.observableArrayList(
                        allKind(getIndexOfRhs((String) lhsC.getValue()))
                );
        rhsC = new ComboBox(options2);
        rhsC.setValue(options2.get(0));
    }
}

