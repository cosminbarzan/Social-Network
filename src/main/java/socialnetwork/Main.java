package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.ui.Ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        /*String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        //String fileName1 = "data/users.csv";
        Repository<Long, User> userFileRepository = new UserFile(fileName, new UserValidator());

        String fileName2 = "data/friendships.csv";
        Repository<Tuple<Long, Long>, Friendship> friendshipFileRepository = new FriendshipFile(fileName2, new FriendshipValidator());

        UserService serviceUser = new UserService(userFileRepository, friendshipFileRepository);
        FriendshipService serviceFriend = new FriendshipService(userFileRepository, friendshipFileRepository);
        Ui ui = new Ui(serviceUser, serviceFriend);
        ui.run();*/


        System.out.println("Reading data from database");
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");

        /*
        // User Repository
        Repository<Long,User> userFileRepository3 =
                new UserDbRepository(url, username, password,  new UserValidator());


        // Friendship Repository
        Repository<Tuple<Long, Long>, Friendship> friendshipRepository =
                new FriendshipDbRepository(url, username, password, new FriendshipValidator());

        // Message Repository
        Repository<Long, Message> messageRepository = new MessageDbRepository(url, username, password, new MessageValidator());

        // Services
        UserService serviceUser1 = new UserService(userFileRepository3, friendshipRepository);
        FriendshipService serviceFriend1 = new FriendshipService(userFileRepository3, friendshipRepository);
        MessageService messageService = new MessageService(messageRepository, userFileRepository3, friendshipRepository);

        // Ui
        Ui ui2 = new Ui(serviceUser1, serviceFriend1, messageService);
        ui2.defaultRun(); */


//        LocalDate date = LocalDate.now().plusDays(-30);
//        System.out.println(date);
//
//        LocalDate date1 = LocalDate.now();
//        System.out.println(date1);
//
//        System.out.println(date1.compareTo(date1));
        MainApp.main(args);
//        String date = "2020-11-17";
//        LocalDate localDate = LocalDate.parse(date);
//        System.out.println(localDate);
    }
}
