package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Event;
import socialnetwork.domain.Registration;
import socialnetwork.service.EventService;
import socialnetwork.service.RegistrationService;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class EventsController implements Initializable {
    @FXML
    private Pagination pagination4;
    @FXML
    private TableView<Event> tableView4;
    @FXML
    private TableColumn<Event, String> nameColumn;
    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private JFXButton registerBtn;
    @FXML
    private JFXButton stopBtn;

    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;
    private static Long idLog;

    private ObservableList<Event> eventsList = FXCollections.observableArrayList();

    int itemPerPage = 3;
    int from = 0;

    public static void setServices(EventService serviceEvent1, RegistrationService serviceRegistration1, Long idLog1) {
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
        idLog = idLog1;
    }

    public List<Event> getTableData() {
        eventsList.clear();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM events limit " + itemPerPage + " offset " + from)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String stringDate = resultSet.getString(3);

                Event event = new Event(resultSet.getString(2), getDate(stringDate));

                eventsList.add(event);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return eventsList;
    }

    private Node createPage(int pageIndex) {
        from = pageIndex * itemPerPage;
        tableView4.setItems(FXCollections.observableArrayList(getTableData()));
        return tableView4;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerBtn.setDisable(true);
        stopBtn.setDisable(true);

        tableView4.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            Event selectedEvent = tableView4.getSelectionModel().getSelectedItem();
            Long idEvent = serviceEvent.findByName(selectedEvent.getEvname());

            boolean existEvent = serviceRegistration.existRegistration(idLog, idEvent).getLeft();
            boolean existNotific = serviceRegistration.existRegistration(idLog, idEvent).getRight();

            if(existEvent == true) {
                if(existNotific == true) {
                    stopBtn.setDisable(false);
                }
            }
            else {
                registerBtn.setDisable(false);
            }
        }));

        int nbOfEvents = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM events")){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                nbOfEvents = resultSet.getInt(1);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("evname"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("evdate"));

        int nbOfPages = (nbOfEvents / itemPerPage) + 1;
        pagination4.setPageCount(nbOfPages);

        pagination4.setPageFactory(this::createPage);
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }

    public void register(MouseEvent event) {
        if(tableView4.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Please select an event first!");
        } else {
            Event selectedEvent = tableView4.getSelectionModel().getSelectedItem();
            Long idEvent = serviceEvent.findByName(selectedEvent.getEvname());

            Registration registration = new Registration(idLog, idEvent, "on");
            serviceRegistration.save(registration);
        }
    }

    public void stopNotifications(MouseEvent event) {
        if(tableView4.getSelectionModel().isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Please select an event first!");
        } else {
            Event selectedEvent = tableView4.getSelectionModel().getSelectedItem();
            Long idEvent = serviceEvent.findByName(selectedEvent.getEvname());

            Registration registration = new Registration(idLog, idEvent, "off");
            serviceRegistration.update(registration);
        }
    }
}
