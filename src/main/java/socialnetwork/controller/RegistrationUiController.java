package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import socialnetwork.MainApp;
import socialnetwork.domain.User;
import socialnetwork.repository.serialize.TrippleDes;
import socialnetwork.service.*;
import socialnetwork.utils.MetaData;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationUiController implements Initializable {
    private static UserService serviceUser;
    private static FriendshipService serviceFriend;
    private static MessageService serviceMessage;
    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;

    @FXML
    private javafx.scene.control.TextField textFieldFirstName;
    @FXML
    private javafx.scene.control.TextField textFieldLastName;
    @FXML
    private javafx.scene.control.TextField textFieldEmail;
    @FXML
    private javafx.scene.control.TextField textFieldPasswd;

    public static void setServiceUser(UserService serviceUser1, FriendshipService serviceFriend1, MessageService serviceMessage1, EventService serviceEvent1, RegistrationService serviceRegistration1) {
        serviceUser = serviceUser1;
        serviceFriend = serviceFriend1;
        serviceMessage = serviceMessage1;
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
    }

    public void closeApp(javafx.scene.input.MouseEvent event) {
        System.exit(0);
    }

    public void backToMenu(javafx.scene.input.MouseEvent event) throws IOException {
        MainUiController.setServiceUser1(serviceUser,serviceFriend,serviceMessage,serviceEvent,serviceRegistration);
        Parent loginLoader = FXMLLoader.load(getClass().getResource("/views/MainUI.fxml"));
        MainApp.stage.getScene().setRoot(loginLoader);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleRegister(MouseEvent event) throws Exception {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String passwd = textFieldPasswd.getText();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || passwd.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Fields cannot be empty!");
            return;
        }

        TrippleDes td= new TrippleDes();

        String encrypted=td.encrypt(passwd);

        User result = serviceUser.addUser(new User(firstName, lastName, email, encrypted));
        if (result == null)
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Successfully register!");
        else {
            MessageAlert.showErrorMessage(null, "Already exist an acount with this email address!");
            return;
        }

        serviceUser.setLoggedIn(serviceUser.findIdByEmail(email, passwd));
        System.out.println(serviceUser.getLoggedIn());
    }
}
