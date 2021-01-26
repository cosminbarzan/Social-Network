package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Registration;
import socialnetwork.service.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainUiController implements Initializable {
    double x = 0;
    double y = 0;

    private static UserService serviceUser;
    private static FriendshipService serviceFriend;
    private static MessageService serviceMessage;
    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;

    @FXML
    private AnchorPane anchor_pane;
    @FXML
    private Pane content_area;
    @FXML
    private javafx.scene.control.TextField textFieldEmail;
    @FXML
    private javafx.scene.control.TextField textFieldPasswd;

    @FXML
    private Label errorLabel;

    public void setServiceUser(UserService serviceUser, FriendshipService serviceFriend, MessageService serviceMessage, EventService serviceEvent, RegistrationService serviceRegistration) {
        this.serviceUser = serviceUser;
        this.serviceFriend = serviceFriend;
        this.serviceMessage = serviceMessage;
        this.serviceEvent = serviceEvent;
        this.serviceRegistration = serviceRegistration;
    }

    public static void setServiceUser1(UserService serviceUser1, FriendshipService serviceFriend1, MessageService serviceMessage1, EventService serviceEvent1, RegistrationService serviceRegistration1) {
        serviceUser = serviceUser1;
        serviceFriend = serviceFriend1;
        serviceMessage = serviceMessage1;
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
    }

    @FXML
    void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void openRegistration(MouseEvent event) throws IOException {
//        FXMLLoader fxml1 = new FXMLLoader();
//        fxml1.setLocation(getClass().getResource("/views/RegistrationUI.fxml"));
//
//        RegistrationUiController registrationUiController = fxml1.getController();
        RegistrationUiController.setServiceUser(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);

        Parent fxml = FXMLLoader.load(getClass().getResource("/views/RegistrationUI.fxml"));

        content_area.getChildren().removeAll();

        content_area.getChildren().setAll(fxml);
    }

    @FXML
    void closeApp(MouseEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleLogin(MouseEvent event) throws Exception {
        String email = textFieldEmail.getText();
        String passwd = textFieldPasswd.getText();

        Long id = serviceUser.findIdByEmail(email, passwd);
        if(id == null) {
            errorLabel.setText("Invalid email or password!");
            //MessageAlert.showErrorMessage(null, "Invalid email or password!");
        }
        else {
            if(serviceUser.login(id) == true) {
                //MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Successfully login!");
                System.out.println(serviceUser.getLoggedIn());
                goToDashboard(id);
            }
            else {
                MessageAlert.showErrorMessage(null, "There is another user logged in");
                return;
            }
        }
    }

    private void goToDashboard(Long id) throws IOException {
        DashboardController.setServiceUser(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);
        if(id == 11)
            DashboardController.setIsAdmin(true);

        Parent fxml = FXMLLoader.load(getClass().getResource("/views/Dashboard.fxml"));

        anchor_pane.getChildren().removeAll();

        anchor_pane.getChildren().setAll(fxml);
    }
}
