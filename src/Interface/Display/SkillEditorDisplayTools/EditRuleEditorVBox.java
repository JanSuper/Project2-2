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

    private Label titleLabel1;
    private Label ruleLabel;
    private Button edit;
    private Button delete;
    private HBox buttons;

    private ObservableList<String> options1;
    private ComboBox lhsC;
    private ObservableList<String> options2;
    private ComboBox rhsC;

    private VBox allQuestions;
    private ScrollPane qScroll;
    private HBox howManyQ;
    private Spinner<Integer> spinner;
    private int oldValue;
    private Label titleLabel;
    private TextField lhs;
    private CheckBox isTerminal;
    private Button enter;

    private VBox editor = null;


    public EditRuleEditorVBox(MainScreen mainScreen){
        this.mainScreen = mainScreen;
        jsonReader=new JsonReader();
        jsonReader.getAllRules();
        jsonReader.splitRules();

        setSpacing(16);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40,0,0,0));
        createContent();
        getChildren().addAll(titleLabel1,lhsC,ruleLabel,rhsC, buttons);
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

        titleLabel1 = new Label("Title (LHS):");
        titleLabel1.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        titleLabel1.setTextFill(MainScreen.themeColor.darker());
        titleLabel1.setAlignment(Pos.CENTER);

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

        edit = new Button("Edit");
        edit.setScaleX(2);
        edit.setScaleY(2);
        edit.setBackground(new Background(new BackgroundFill(Color.ORANGE, new CornerRadii(90,true), Insets.EMPTY)));
        edit.setOnAction(event -> {
            if(editor==null){
                String rhs1 = (String) rhsC.getValue();
                String noBrackets = rhs1.substring( 1, rhs1.length() - 1 );
                String[] rhs = noBrackets.split(",");
                showEditor((String)lhsC.getValue(),rhs);
            }else if(getChildren().contains(editor)){
                getChildren().remove(editor);
            }else{
                getChildren().add(editor);
            }
        });

        delete = new Button("Delete");
        delete.setScaleX(2);
        delete.setScaleY(2);
        delete.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(90,true), Insets.EMPTY)));
        delete.setOnAction(event -> {
            String rule = convertToRule((String)lhsC.getValue(), (String) rhsC.getValue());
            try {
                jsonReader.removeRule(rule,isTerminal(rule));
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
        rhsC.setValue(options2.get(0));
    }

    public boolean isTerminal(String rule){
        String rhs = rule.split(":")[1];
        if(rhs.contains(",")){
            return false;
        }
        return true;
    }

    public void showEditor(String lhs1,String[]rhs){
        editor = new VBox(10);
        editor.setTranslateY(50);
        editor.setAlignment(Pos.CENTER);

        allQuestions = new VBox();
        allQuestions.setSpacing(10);
        allQuestions.setAlignment(Pos.CENTER);

        qScroll = new ScrollPane(allQuestions);
        qScroll.setMaxWidth(710);
        qScroll.setBackground(getBackground());
        qScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        titleLabel = new Label("New title (LHS):");
        titleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        titleLabel.setTextFill(MainScreen.themeColor.darker());
        titleLabel.setAlignment(Pos.CENTER);

        lhs = new TextField(lhs1);
        lhs.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        lhs.setPrefSize(10,36);
        lhs.setMaxWidth(710);

        spinner = new Spinner<Integer>();
        spinner.setMaxWidth(60);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, oldValue);
        spinner.setValueFactory(valueFactory);
        spinner.setOnMouseClicked(event -> {
            int newVal = spinner.getValue();
            if(newVal>oldValue){
                if(newVal<=rhs.length){
                    addRule(rhs[newVal-1]);
                }else {
                    addRule("-1");
                }
            }else if(newVal<oldValue){
                allQuestions.getChildren().remove(allQuestions.getChildren().size()-1);
            }
            if(oldValue==4){
                qScroll.setMaxHeight(qScroll.getHeight());
            }
            oldValue = newVal;
        });

        Label ruleLabel = new Label("New rule (RHS):");
        ruleLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        ruleLabel.setTextFill(MainScreen.themeColor.darker());
        ruleLabel.setAlignment(Pos.CENTER);

        howManyQ = new HBox();
        howManyQ.setSpacing(20);
        howManyQ.setAlignment(Pos.CENTER);
        howManyQ.getChildren().addAll(ruleLabel,spinner);

        isTerminal = new CheckBox("Is terminal");
        isTerminal.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        isTerminal.setTextFill(MainScreen.themeColor.darker());
        isTerminal.setAlignment(Pos.CENTER);

        enter = new Button("Add");
        enter.setScaleX(2);enter.setScaleY(2);
        enter.setTranslateY(50);
        enter.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(90,true), Insets.EMPTY)));
        enter.setOnAction(e-> {
            try {
                String rhs1 = "";
                for (int i=0;i<allQuestions.getChildren().size()-1;i++) {
                    TextField question = (TextField) allQuestions.getChildren().get(i);
                    rhs1 += question.getText()+",";
                }
                TextField question = (TextField) allQuestions.getChildren().get(allQuestions.getChildren().size()-1);
                rhs1+=question.getText();
                try {
                    String newRule = lhs.getText()+":"+rhs1;
                    jsonReader.editRule(lhs1,isTerminal(newRule),newRule);
                    mainScreen.chat.receiveMessage("Rule " + lhs1 + " has been edited into " + newRule + ".");
                    refresh();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                jsonReader.addRules(lhs.getText()+":"+rhs1,isTerminal.isSelected());
            } catch (Exception exception) {
                mainScreen.chat.receiveMessage("The rule could not be added for some reason, please recheck its format");
            }
            lhs.setText("");
            isTerminal.setSelected(false);
            mainScreen.chat.receiveMessage("The rule has been added in the database");
        });

        editor.getChildren().addAll(titleLabel,lhs,howManyQ,qScroll,isTerminal,enter);

        getChildren().add(editor);
    }

    private void addRule(String txt){
        TextField rule = new TextField();
        if(txt.equals("-1")){
            rule.setPromptText("If you wish to include a variable, please replace it by <VARIABLE>");
        }else{
            rule.setText(txt);
        }
        rule.setPrefSize(qScroll.getWidth()-20,25);
        rule.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        allQuestions.getChildren().add(rule);
    }
}

