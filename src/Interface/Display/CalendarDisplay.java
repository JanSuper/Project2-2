package Interface.Display;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CalendarDisplay extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TableView tableView = new TableView();
        final ObservableList<Agenda> data = FXCollections.observableArrayList(
                new Agenda("07:00"," 1","211", "Doe","1","hhh","1","1"),
                new Agenda("08:00","", "","", "","", "",""),
                new Agenda("08:00","", "","", "","", "",""),
                new Agenda("09:00","", "","", "","", "",""),
                new Agenda("10:00","", "","", "","", "",""),
                new Agenda("11:00","", "","", "","", "",""),
                new Agenda("12:00","", "","", "","", "",""),
                new Agenda("13:00","", "","", "","", "",""),
                new Agenda("14:00","", "","", "","", "",""),
                new Agenda("15:00","", "","", "","", "",""),
                new Agenda("16:00","", "","", "","", "",""),
                new Agenda("17:00","", "","r", "","", "",""),
                new Agenda("18:00","", "","", "","", "",""),
                new Agenda("19:00","", "","", "","", "",""),
                new Agenda("20:00","", "","", "","", "",""),
                new Agenda("21:00","", "","", "","", "",""),
                new Agenda("22:00","", "","", "","", "",""),
                new Agenda("23:00","", "","", "","", "",""),
                new Agenda("24:00","", "","", "","", "","")
        );
        final HBox hb = new HBox();
        tableView.setEditable(true);

        Scene scene = new Scene(new Group());
        final Label label = new Label("Week Calendar");
        label.setFont(new Font("Arial", 20));
        tableView.setEditable(true);
        primaryStage.setTitle("Calendar");
        primaryStage.setWidth(800);
        primaryStage.setHeight(500);

        tableView.setEditable(true);
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new Agenda.EditingCell();
                    }
                };


        TableColumn column0 = new TableColumn("Hours");
        column0.setCellValueFactory(new PropertyValueFactory<Agenda, String>("hours"));

        TableColumn column1 = new TableColumn("Monday");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<Agenda, String>("monday"));
        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        column1.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Agenda, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Agenda, String> t) {
                        ((Agenda) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setMonday(t.getNewValue());
                    }
                }
        );

        TableColumn column2 = new TableColumn("Tuesday");
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<Agenda, String>("tuesday"));
        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        column1.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Agenda, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Agenda, String> t) {
                        ((Agenda) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setTuesday(t.getNewValue());
                    }
                }
        );

        TableColumn column3 = new TableColumn("Wednesday");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<Agenda, String>("wednesday"));

        TableColumn column4 = new TableColumn("Thursday");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<Agenda, String>("thursday"));

        TableColumn column5 = new TableColumn("Friday");
        column5.setMinWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory<Agenda, String>("friday"));

        TableColumn column6 = new TableColumn("Saturday");
        column6.setMinWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<Agenda, String>("saturday"));

        TableColumn column7 = new TableColumn("Sunday");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<Agenda, String>("sunday"));




        tableView.setItems(data);
        tableView.getColumns().addAll(column0,column1,column2,column3,column4,column5,column6,column7);

        final TextField addHours = new TextField();
        addHours.setPromptText("hour");
        addHours.setMaxWidth(column0.getPrefWidth());

        final TextField addMonday = new TextField();
        addMonday.setMaxWidth(column1.getPrefWidth());
        addMonday.setPromptText("Monday");

        final TextField addTuesday = new TextField();
        addTuesday.setMaxWidth(column2.getPrefWidth());
        addTuesday.setPromptText("Tuesday");

        final TextField addWednesday = new TextField();
        addWednesday.setPromptText("Wednesday");
        addWednesday.setMaxWidth(column3.getPrefWidth());

        final TextField addThursday = new TextField();
        addThursday.setMaxWidth(column4.getPrefWidth());
        addThursday.setPromptText("Thursday");

        final TextField addFriday = new TextField();
        addFriday.setMaxWidth(column5.getPrefWidth());
        addFriday.setPromptText("Friday");

        final TextField addSaturday = new TextField();
        addSaturday.setMaxWidth(column6.getPrefWidth());
        addSaturday.setPromptText("Saturday");

        final TextField addSunday = new TextField();
        addSunday.setMaxWidth(column5.getPrefWidth());
        addSunday.setPromptText("Sunday");


        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                data.add(new Agenda(addHours.getText(), addMonday.getText(), addTuesday.getText(),addWednesday.getText(),
                        addThursday.getText(),addFriday.getText(), addSaturday.getText(),addSunday.getText()));
                addHours.clear();
                addMonday.clear();
                addTuesday.clear();
                addWednesday.clear();
                addThursday.clear();
                addFriday.clear();
                addSaturday.clear();
                addSunday.clear();

            }
        });
        hb.getChildren().addAll(addHours, addMonday, addTuesday,addWednesday,addThursday,addFriday,
                addSaturday,addSunday, addButton);
        hb.setSpacing(3);



        tableView.getSortOrder().add(column0);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label,tableView ,hb);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        primaryStage.setScene(scene);

        primaryStage.show();

           /* VBox vbox = new VBox(tableView);
            vbox.setSpacing(500);
            vbox.setPadding(new Insets(0, 0, 0, 0));
            vbox.getChildren().addAll(label, tableView);
            // Scene scene = new Scene(vbox);
            ((Group) scene.getRoot()).getChildren().addAll(vbox); */
    }

}

