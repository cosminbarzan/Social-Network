package socialnetwork;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.MessageAlert;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

public class MainFX extends Application {
    boolean[] result = new boolean[2];
    Repository<Long, User> userFileRepository;
    Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    Repository<Long, Message> messageRepository;


    private UserService serviceUser;
    private FriendshipService serviceFriend;
    private MessageService serviceMessage;

    Button loginBtn = new Button("Login");
    Button exitBtn = new Button("Exit");
    GridPane gridPane = new GridPane();

    @Override
    public void start(Stage stage) throws Exception {
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");

        userFileRepository = new UserDbRepository(url, username, password,  new UserValidator());
        friendshipRepository = new FriendshipDbRepository(url, username, password, new FriendshipValidator());
        messageRepository = new MessageDbRepository(url, username, password, new MessageValidator());

        serviceUser = new UserService(userFileRepository, friendshipRepository);
        serviceFriend = new FriendshipService(userFileRepository, friendshipRepository);
        serviceMessage = new MessageService(messageRepository, userFileRepository, friendshipRepository);

        //hBox.getChildren().addAll(loginBtn, exitBtn);
        gridPane.setPadding(new Insets(20));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(loginBtn, 0, 0);
        gridPane.add(exitBtn, 1, 0);

        Group group = new Group();

        Scene scene = new Scene(gridPane, 400, 300, Color.CYAN);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();

        handleLogin(stage);

    }

    private void handleLogin(Stage stageMain) {
        loginBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TextField textFieldFirstName = new TextField();
                TextField textFieldLastName = new TextField();
                TextField textFieldId = new TextField();

                Button login = new Button("Login");
                Button logout = new Button("Logut");


                Stage logStage = new Stage();

                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(20));
                gridPane.setAlignment(Pos.CENTER);

                gridPane.add(new Label("First Name"), 0, 0);
                gridPane.add(textFieldFirstName, 1, 0);
                gridPane.add(new Label("Last Name"), 0, 1);
                gridPane.add(textFieldLastName, 1, 1);
                gridPane.add(new Label("Id"), 0, 2);
                gridPane.add(textFieldId, 1, 2);
                gridPane.add(login, 0, 3);

                login.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Stage userStage = new Stage();

                        String firstName = textFieldFirstName.getText();
                        String lastName = textFieldLastName.getText();
                        String id = textFieldId.getText();
                        Long newId = Long.parseLong(id);

                        if(serviceUser.login(newId) == true) {
                            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "User logat cu succes!");
                            logStage.close();
                            stageMain.close();
                            userInterface(newId, logout, userStage);
                            handleLogout(stageMain, logout, userStage);
                        }
                        else {
                            MessageAlert.showErrorMessage(null, "Nu ati introdus un user!");
                        }
                    }
                });

                Scene logScene = new Scene(gridPane, 350, 300);
                logStage.setTitle("Login");
                logStage.initModality(Modality.WINDOW_MODAL);
                logStage.setScene(logScene);
                logStage.show();
            }
        });
    }

    public void handleLogout(Stage stage, Button logout, Stage userStage) {
        logout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                userStage.close();
                stage.show();
                serviceUser.setLoggedIn(null);
            }
        });
    }

    private void userInterface(Long id, Button logout, Stage userStage) {
        Button addBtn = new Button("Add");
        Button remBtn = new Button("Remove");
        Button reqBtn = new Button("Friend Requests");

        HBox btnsBox = new HBox();
        btnsBox.getChildren().addAll(addBtn, remBtn, reqBtn, logout);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        ListView<User> listView = new ListView<>();
        vBox.getChildren().addAll(new Label("Your Friends"), listView, btnsBox);
        List<User> users = serviceUser.userFriendsGUI(id);

        users.forEach(user -> listView.getItems().add(user));

        handleRemove(remBtn, listView);
        handleAdd(addBtn, listView);
        handleViewRequests(reqBtn);

        Scene userScene = new Scene(vBox, 350, 300);
        userStage.setTitle("User commands");
        userStage.setScene(userScene);
        userStage.show();
    }

    private void handleViewRequests(Button reqBtn) {
        reqBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = new Stage();
                VBox vBox = new VBox();

                List<Request> stringList = serviceFriend.allFriendRequests(serviceUser.getLoggedIn());

                ObservableList<Request> strings = FXCollections.observableArrayList(stringList);
                TableView<Request> tableView = new TableView<Request>();
                TableColumn<Request, String> columnFrom = new TableColumn<>("From");
                TableColumn<Request, Status> columnStatus = new TableColumn<>("Status");
                TableColumn<Request, LocalDateTime> columnDate = new TableColumn<>("Date");

                tableView.getColumns().addAll(columnFrom, columnStatus, columnDate);

                columnFrom.setCellValueFactory(new PropertyValueFactory<Request, String >("Name"));
                columnStatus.setCellValueFactory(new PropertyValueFactory<Request, Status >("status"));
                columnDate.setCellValueFactory(new PropertyValueFactory<Request, LocalDateTime >("date"));
                tableView.setItems(strings);



                ListView<String> listView = new ListView<>();
                vBox.getChildren().add(tableView);
                Scene scene = new Scene(vBox, 300, 250);
                stage.setTitle("Friends Requests");
                stage.setScene(scene);
                stage.show();

                //serviceFriend.friendRequests(serviceUser.getLoggedIn()).forEach(s -> listView.getItems().add(s));


            }
        });
    }

    private void handleRemove(Button remBtn, ListView<User> listView) {
        remBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                User user = listView.getSelectionModel().getSelectedItem();
                serviceFriend.removeFriend(new Tuple<>(serviceUser.getLoggedIn(), user.getId()));
                listView.getItems().remove(user);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Friend removed!");
            }
        });
    }

    private void handleAdd(Button addBtn, ListView<User> listView) {
        addBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(20));
                gridPane.setAlignment(Pos.CENTER);

                TextField textField = new TextField();
                Button button = new Button("Save");

                gridPane.add(new Label("Id"), 0, 0);
                gridPane.add(textField, 1, 0);
                gridPane.add(button, 1, 1);

                button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Long id1 = serviceUser.getLoggedIn();
                        Long id2 = Long.parseLong(textField.getText());

                        Friendship friendship = new Friendship(id1, id2, LocalDateTime.now());
                        Friendship friendship1 = new Friendship(id2, id1, LocalDateTime.now());

                        Friendship aFr = friendship;

                        if(serviceFriend.getOne(friendship.getId()) == null) {
                            if(serviceFriend.getOne(friendship1.getId()) == null) {
                                serviceFriend.addFriendship(friendship);
                            }
                            else
                                aFr = friendship1;
                        }
                        else {
                            int status = serviceFriend.sendFriendship(aFr);
                            if(status == 1)
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "You have already sent a friend request to this user!");
                            if(status == 2)
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "This user is already your friend!");
                            if(status == 3)
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Information", "Request sent again!");
                        }
                    }
                });

                Stage addStage = new Stage();
                Scene addScene = new Scene(gridPane, 300, 200);
                addStage.setTitle("Add friend");
                addStage.setScene(addScene);
                addStage.show();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

}