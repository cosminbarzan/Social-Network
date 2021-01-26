package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class ReportDateController implements Initializable {

    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private DatePicker datePicker1;
    @FXML
    private DatePicker datePicker2;


    public static List<User> users;
    public static Long userId;
    public static LocalDate dateStart;
    public static LocalDate dateEnd;


    public static void setUsers(List<User> users1) {
        users = users1;
    }

    public static Long getUser() {
        return userId;
    }

    public static LocalDate getDate1() {
        return dateStart;
    }

    public static LocalDate getDate2() {
        return dateEnd;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> usersStr = new ArrayList<>();
        for (User user : users){
            usersStr.add(user.getId() + ":" + user.getFirstName() + " " + user.getLastName());
        }

        comboBox.getItems().addAll(usersStr);
    }

    public void reports(MouseEvent event) {
        String[] fields;
        System.out.println(comboBox.getValue());
        fields = comboBox.getValue().split(":");
        System.out.println(comboBox.getValue());
        userId = Long.parseLong(fields[0]);

        dateStart = datePicker1.getValue();

        dateEnd = datePicker2.getValue();

        DashboardController.reports(userId, dateStart, dateEnd);
    }
}
