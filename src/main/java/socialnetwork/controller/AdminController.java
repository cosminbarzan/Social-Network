package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Event;
import socialnetwork.service.EventService;
import socialnetwork.service.RegistrationService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private JFXTextField typeEvent;
    @FXML
    private JFXTextField typeDate;

    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;

    public static void setServices(EventService serviceEvent1, RegistrationService serviceRegistration1) {
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void newEvent(MouseEvent event) {
        LocalDateTime date = getDate(typeDate.getText());
        Event event1 = new Event(typeEvent.getText(), date);

        Event rez = serviceEvent.save(event1);

        if(rez == null)
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Added event!");
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }
}
