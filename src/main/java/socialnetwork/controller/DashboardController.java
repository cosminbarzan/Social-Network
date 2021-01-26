package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.service.*;
import socialnetwork.utils.FriendD;
import socialnetwork.utils.MessageE;
import socialnetwork.utils.Report;

import javax.swing.text.Element;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DashboardController implements Initializable {
    @FXML
    private VBox leftBox;
    @FXML
    private VBox rightBox;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField textFieldSearchUser;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> imageColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableView<User> tableView1;
    @FXML
    private TableColumn<User, String> firstNameColumn1;
    @FXML
    private TableColumn<User, String> lastNameColumn1;
    @FXML
    private TableColumn<User, String> emailColumn1;

    public static Long userId;
    public static LocalDate dateStart;
    public static LocalDate dateEnd;

    @FXML
    private JFXButton sendReqBtn, cancelReqBtn, removeBtn, messagesBtn, logoutBtn;

    @FXML
    private AnchorPane anchor_pane2;

    @FXML
    private AnchorPane changing_anchor_pane;

    @FXML
    private AnchorPane upPane;
    @FXML
    private AnchorPane downPane;

    @FXML
    JFXButton showReportsBtn;

    @FXML
    private Pagination pagination;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    private ObservableList<User> usersList1 = FXCollections.observableArrayList();

    private final ObservableList<User> allUsersList = FXCollections.observableArrayList();


    private final ObservableList<MessageE> emptyMessagesList = FXCollections.observableArrayList();

    private static UserService serviceUser;
    private static FriendshipService serviceFriend;
    private static MessageService serviceMessage;
    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;

    private static boolean isAdmin = false;

    private Button button;

    int itemPerPage = 3;
    int from = 0;

    public static void setIsAdmin(boolean value) {
        isAdmin = value;
    }

    public static void setServiceUser(UserService serviceUser1, FriendshipService serviceFriend1, MessageService serviceMessage1, EventService serviceEvent1, RegistrationService serviceRegistration1) {
        serviceUser = serviceUser1;
        serviceFriend = serviceFriend1;
        serviceMessage = serviceMessage1;
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
    }

    @FXML
    public int activeButtons(TableView<User> table) {
        if( !table.getSelectionModel().isEmpty() ) {
            User user = table.getSelectionModel().getSelectedItem();

            // If they are friends
            if(serviceUser.areFriends(user.getId(), serviceUser.getLoggedIn())) {
                return 1;
            }
            else {
                // Daca user-ul logat a trimis o cerere deja
                if(serviceUser.requestSent(serviceUser.getLoggedIn(), user.getId())) {
                    return 3;
                }
                return 2;
            }
            //tableView.getSelectionModel().clearSelection();
        }
        return 0;
    }

    private void populateTable(boolean value, ObservableList<User> users) {
        FilteredList<User> filteredList = new FilteredList<>(users, b -> value);

        // Set the filter Predicate whenever the filter changes
        textFieldSearchUser.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(user -> {
                // If filter text is empty, display all the friends
                if(newValue == null || newValue.isEmpty()) {
                    sendReqBtn.setVisible(false);
                    cancelReqBtn.setVisible(false);
                    return value;
                }

                // Compare first name and last name of every person with filter text
                String lowerCaseFilter = newValue.toLowerCase();

                if(user.getFirstName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    sendReqBtn.setVisible(true);
                    cancelReqBtn.setVisible(true);
                    return true; // Filter matches first name
                }
                else if(user.getLastName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    sendReqBtn.setVisible(true);
                    cancelReqBtn.setVisible(true);
                    return true; // Filter matches last name
                }
                else {
                    return false; // Does not match
                }
            });
        });

        SortedList<User> sortedList = new SortedList<>(filteredList);

        sortedList.comparatorProperty().bind(tableView1.comparatorProperty());

        usersList1 = sortedList;

        tableView1.setItems(usersList1);
    }

    public List<User> getTableData() {
        usersList.clear();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users limit " + itemPerPage + " offset " + from)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                User user = new User(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
                user.setId(resultSet.getLong(1));
                usersList.add(user);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        for(User user : usersList) {
            ImageView imageView = new ImageView(new Image("images/profile.png"));
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            user.setFirstName(user.getFirstName() + " " + user.getLastName() + "\n" + user.getEmail());
            user.setPhoto(imageView);
        }

        return usersList;
    }

    private Node createPage(int pageIndex) {
        from = pageIndex * itemPerPage;
        tableView.setItems(FXCollections.observableArrayList(getTableData()));
        return tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            int nbOfUsers = 0;
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
                 PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users")) {
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    nbOfUsers = resultSet.getInt(1);
                }

                resultSet.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            showReportsBtn.setVisible(false);

            User logUser = serviceUser.getOne(serviceUser.getLoggedIn());
            welcomeLabel.setText(logUser.getFirstName() + " " + logUser.getLastName());
            contactLabel.setText("Contact info \n" + logUser.getEmail());

            imageColumn.setPrefWidth(30);
            imageColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            //lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            //emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

//        serviceUser.userFriendsGUI(serviceUser.getLoggedIn()).forEach(usersList::add);
//        serviceUser.getAll().forEach(allUsersList::add);

//        tableView.setOnScroll(new EventHandler<ScrollEvent>() {
//            @Override
//            public void handle(ScrollEvent event) {
//                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", String.valueOf(event.getMultiplierY()));
//            }
//        });

            int nbOfPages = (nbOfUsers / itemPerPage) + 1;
            pagination.setPageCount(nbOfPages);

            pagination.setPageFactory(this::createPage);

            //populateTable(true, usersList);
//        checkBox.setSelected(true);
            //showButtons();

            removeBtn.setDisable(true);
            tableView.getSelectionModel().selectedItemProperty().addListener((this::changed));

            sendReqBtn.setVisible(false);
            cancelReqBtn.setVisible(false);

            sendReqBtn.setDisable(true);
            cancelReqBtn.setDisable(true);
            tableView1.getSelectionModel().selectedItemProperty().addListener((this::changed1));

            //tableView1
            firstNameColumn1.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastNameColumn1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            emailColumn1.setCellValueFactory(new PropertyValueFactory<>("email"));

            serviceUser.getAll().forEach(allUsersList::add);
            populateTable(false, allUsersList);


            if (isAdmin == true) {
                AdminController.setServices(serviceEvent, serviceRegistration);
                Parent fxml = null;
                try {
                    fxml = FXMLLoader.load(getClass().getResource("/views/Admin.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                changing_anchor_pane.getChildren().removeAll();

                changing_anchor_pane.getChildren().setAll(fxml);
            }
        }
        catch (NullPointerException ex){}
    }

    private void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
        try {
            if (serviceUser.areFriends(serviceUser.getLoggedIn(), newValue.getId()))
                removeBtn.setDisable(false);
            else
                removeBtn.setDisable(true);

        }
        catch (NullPointerException ex) {}
    }

    private void changed1(ObservableValue<? extends User> observable, User oldValue, User newValue) {
        int result = activeButtons(tableView1);
        if(result == 3) {
            cancelReqBtn.setDisable(false);
        }
        else
            sendReqBtn.setDisable(false);
    }

    public void unchecked(MouseEvent event) {
        rightBox.getChildren().clear();
        rightBox.getChildren().addAll(new Button("Ciau"), new JFXButton("ala"));
//        if(checkBox.isSelected() == false) {
//            tableView.setItems(null);
//
//            populateTable(false, allUsersList);
//        }
//        else {
//            populateTable(true, usersList);
//        }
    }

    public void sendRequest(MouseEvent event) {
        int result = activeButtons(tableView1);
        if(result == 2 || result == 3) {
            User user = tableView1.getSelectionModel().getSelectedItem();

            Long id1 = serviceUser.getLoggedIn();
            Long id2 = tableView1.getSelectionModel().getSelectedItem().getId();

            Friendship friendship = new Friendship(id1, id2, LocalDateTime.now());
            Friendship friendship1 = new Friendship(id2, id1, LocalDateTime.now());

            Friendship aFr = friendship;

            if(serviceFriend.getOne(friendship.getId()) == null) {
                if(serviceFriend.getOne(friendship1.getId()) == null) {
                    serviceFriend.addFriendship(friendship);
                }
                else
                    aFr = friendship1;
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Request sent!");
            }
            else {
                int status = serviceFriend.sendFriendship(aFr);
                if (status == 1)
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "You have already sent a friend request to this user!");
                if (status == 2)
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "This user is already your friend!");
                if (status == 3)
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Request sent again!");
            }
        }
        else System.out.println("33333");
    }

    public void cancelRequest(MouseEvent event) {
        if(activeButtons(tableView1) == 3) {
            User user = tableView1.getSelectionModel().getSelectedItem();
            serviceFriend.removeFriend(new Tuple<>(serviceUser.getLoggedIn(), user.getId()));
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Request canceled!");
        }
    }

    public void remove(MouseEvent event) {
        if(activeButtons(tableView) == 1) {
            User user = tableView.getSelectionModel().getSelectedItem();
            serviceFriend.removeFriend(new Tuple<>(serviceUser.getLoggedIn(), user.getId()));
            //tableView.getItems().clear();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend removed!");

            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            tableView.getItems().remove(selectedUser);

//            ObservableList<User> usersList = FXCollections.observableArrayList();
//            serviceUser.userFriendsGUI(serviceUser.getLoggedIn()).forEach(usersList::add);
//            populateTable(true, usersList);
        }
    }

    public void messages(MouseEvent event) {
        if(activeButtons(tableView) == 1) {
            Long idTo = tableView.getSelectionModel().getSelectedItem().getId();

            MessagesController.setServiceUser(serviceUser, serviceMessage, idTo);

            Parent fxml = null;
            try {
                fxml = FXMLLoader.load(getClass().getResource("/views/Messages.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            changing_anchor_pane.getChildren().removeAll();

            changing_anchor_pane.getChildren().setAll(fxml);
        }
    }

    public void loadProfile(MouseEvent event) throws IOException {
        FXMLLoader dashboardLoader = new FXMLLoader();
        dashboardLoader.setLocation(getClass().getResource("/views/Dashboard.fxml"));
        Parent fxml = dashboardLoader.load();

        anchor_pane2.getChildren().removeAll();

        anchor_pane2.getChildren().setAll(fxml);
    }

    public void ShowRequests(MouseEvent event) {
        RequestsController.setServices(serviceUser, serviceFriend);

        Parent fxml = null;
        try {
            fxml = FXMLLoader.load(getClass().getResource("/views/Requests.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        changing_anchor_pane.getChildren().removeAll();

        changing_anchor_pane.getChildren().setAll(fxml);
    }



    public void sendMessage(MouseEvent event) {
        if(activeButtons(tableView) == 1) {
            User from = serviceUser.getOne(serviceUser.getLoggedIn());
            User to = serviceUser.getOne(tableView.getSelectionModel().getSelectedItem().getId());

            List<User> toList = new ArrayList<>();
            toList.add(to);

            VBox vBox = new VBox();
            TextField typeMessage = new TextField("Type message");
            Button enterBtn = new Button("Enter");
            vBox.getChildren().addAll(typeMessage, enterBtn);

            Stage stage = new Stage();
            Scene scene = new Scene(vBox, 300, 200);
            stage.setTitle("Send Message");
            stage.setScene(scene);
            stage.show();

            enterBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                public void handle(MouseEvent event) {
                    serviceMessage.save(new Message(from, toList, typeMessage.getText()));
                    stage.close();
                    MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Message sent!");
                }
            });
        }
    }

//    public void sendReply(MouseEvent event) {
//        User from = serviceUser.getOne(serviceUser.getLoggedIn());
//        Long idReply = tableView3.getSelectionModel().getSelectedItem().getId();
//        User to = serviceMessage.getOne(idReply).getFrom();
//
//        List<User> toList = new ArrayList<>();
//        toList.add(to);
//
//        Message replyMessage = serviceMessage.getOne(idReply);
//
//        VBox vBox = new VBox();
//        TextField typeMessage = new TextField("Type message");
//        Button enterBtn = new Button("Enter");
//        vBox.getChildren().addAll(typeMessage, enterBtn);
//
//        Stage stage = new Stage();
//        Scene scene = new Scene(vBox, 300, 200);
//        stage.setTitle("Send Reply");
//        stage.setScene(scene);
//        stage.show();
//
//        enterBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            public void handle(MouseEvent event) {
//                Message messageToSend = new Message(from, toList, typeMessage.getText());
//                messageToSend.setReply(replyMessage);
//
//                serviceMessage.save(messageToSend);
//
//                stage.close();
//                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Message sent!");
//
//                List<MessageE> messageEs = serviceMessage.anotherUserConv(from.getId(), to.getId());
//
//                messageEList.clear();
//                messageEList.addAll(messageEs);
//
//                tableView3.setItems(messageEList);
//            }
//        });
//    }

    public void handleLogout(MouseEvent event) throws IOException {
        //DashboardController.setServiceUser(serviceUser, serviceFriend, serviceMessage);

        //Parent fxml = FXMLLoader.load(getClass().getResource("/views/MainUI.fxml"));

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/views/MainUI.fxml"));
        Parent fxml = loginLoader.load();

        anchor_pane2.getChildren().removeAll();

        anchor_pane2.getChildren().setAll(fxml);

        MainUiController mainUiController = loginLoader.getController();
        mainUiController.setServiceUser(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);

        serviceUser.setLoggedIn(null);
    }


    public void goToReports(MouseEvent event) throws IOException {
        showReportsBtn.setVisible(true);
        ReportDateController.setUsers(serviceUser.userFriendsGUI(serviceUser.getLoggedIn()));

        Parent fxml = null;
        try {
            fxml = FXMLLoader.load(getClass().getResource("/views/ReportDate.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //changing_anchor_pane.getChildren().removeAll();

        //changing_anchor_pane.getChildren().setAll(fxml);

        upPane.getChildren().removeAll();
        upPane.getChildren().setAll(fxml);
    }

    public static void reports(Long id, LocalDate dateStart1, LocalDate dateEnd1) {
        userId = id;
        System.out.println(userId);
        dateStart = dateStart1;
        dateEnd = dateEnd1;
    }

    public void loadReports() {
        showReportsBtn.setVisible(false);
        ReportsController.setServices(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);
        ReportsController.setData(dateStart, dateEnd, userId);

        Parent fxml = null;
        try {
            fxml = FXMLLoader.load(getClass().getResource("/views/Reports.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        anchor_pane2.getChildren().removeAll();

        anchor_pane2.getChildren().setAll(fxml);
    }

    /**
     * 
     * @param enterBtn
     * @param stage
     * @param typeMessage
     */
    private void loadReportsModalWindow(Button enterBtn, Stage stage, TextField typeMessage) {
        VBox vBox = new VBox();
        Label mesLabel = new Label("Type user");
        Label date1Label = new Label("Type user");
        TextField startDate = new TextField();
        Label date2Label = new Label("End Date");
        TextField endDate = new TextField();
        enterBtn.setText("Show Report");
        vBox.getChildren().addAll(mesLabel, typeMessage, date1Label, startDate, date2Label, endDate, enterBtn);

        Scene scene = new Scene(vBox, 300, 200);
        stage.setTitle("Send Reply");
        stage.setScene(scene);
        stage.show();
    }


    public void events(MouseEvent event) {
        EventsController.setServices(serviceEvent, serviceRegistration, serviceUser.getLoggedIn());

        Parent fxml = null;
        try {
            fxml = FXMLLoader.load(getClass().getResource("/views/Events.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        changing_anchor_pane.getChildren().removeAll();

        changing_anchor_pane.getChildren().setAll(fxml);
    }

    public void notifications(MouseEvent event) {
        List<Event> events = serviceEvent.userEvents(serviceUser.getLoggedIn());

        if (events.size() > 0) {
            String notification = "";

            for (Event event1 : events) {
                long days = ChronoUnit.DAYS.between(LocalDateTime.now(), event1.getEvdate());
                notification += days + " days left till " + event1.getEvname() + "\n";
            }
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Notification", notification);
        }
        else {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Notification", "You have no notifications");
        }
    }
}
