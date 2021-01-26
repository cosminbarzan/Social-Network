package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.MainUiController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.*;
import socialnetwork.utils.MetaData;

import java.io.IOException;

public class MainApp extends Application {
    Repository<Long, User> userFileRepository;
    Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    Repository<Long, Message> messageRepository;
    Repository<Long, Event> eventRepository;
    Repository<Long, Registration> registrationRepository;

    private UserService serviceUser;
    private FriendshipService serviceFriend;
    private MessageService serviceMessage;
    private EventService serviceEvent;
    private RegistrationService serviceRegistration;

    public static Stage stage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");

        userFileRepository = new UserDbRepository(url, username, password,  new UserValidator());
        friendshipRepository = new FriendshipDbRepository(url, username, password, new FriendshipValidator());
        messageRepository = new MessageDbRepository(url, username, password, new MessageValidator());
        eventRepository = new EventDbRepository(url, username, password);
        registrationRepository = new RegistrationDbRepository(url, username, password);

        serviceUser = new UserService(userFileRepository, friendshipRepository);
        serviceFriend = new FriendshipService(userFileRepository, friendshipRepository);
        serviceMessage = new MessageService(messageRepository, userFileRepository, friendshipRepository);
        serviceEvent = new EventService(eventRepository);
        serviceRegistration = new RegistrationService(registrationRepository);

        initView(primaryStage);

        this.stage = primaryStage;

        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();

        loginLoader.setLocation(getClass().getResource("/views/MainUI.fxml"));

        Scene scene = new Scene(loginLoader.load());

        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        MainUiController mainUiController = loginLoader.getController();
        mainUiController.setServiceUser(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
