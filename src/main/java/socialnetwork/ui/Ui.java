package socialnetwork.ui;

import org.postgresql.util.PSQLException;
import socialnetwork.controller.MessageAlert;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ui {
    private UserService serviceUser;
    private FriendshipService serviceFriend;
    private MessageService serviceMessage;

    public Ui (UserService serviceUser, FriendshipService serviceFriend, MessageService serviceMessage){
        this.serviceUser = serviceUser;
        this.serviceFriend = serviceFriend;
        this.serviceMessage = serviceMessage;
    }

    /**
     * preia de la utilizator un nume si un prenume
     * adauga in aplicatie utilizatorul cu numele si prenumele specificat
     */
    public void uiAddUser (){
        Scanner in = new Scanner(System.in);
        System.out.println("Introduce first name:");
        String firstName;
        firstName = in.nextLine();

        System.out.println("Introduce last name:");
        String lastName;
        lastName = in.nextLine();

        System.out.println("Introduce email:");
        String email;
        email = in.nextLine();

        System.out.println("Introduce password:");
        String passwd;
        passwd = in.nextLine();

        User result =  serviceUser.addUser(new User(firstName, lastName, email, passwd));
        if (result == null)
            System.out.println("Utilizator adaugat cu succes!");
        else
            System.out.println("Utilizatorul nu a putut fi adaugat!");
    }

    /**
     * preia de la utilizator un nume
     * afiseaza toti utilizatorii din aplicatie cu numele dat
     * preia de la utilizator un id
     * sterge din aplicatie utilizatorul cu id-ul specificat
     */
    public void uiDeleteUser (){
        Scanner in = new Scanner(System.in);
        System.out.println("Introduce the last name:");
        String lastName = in.nextLine();
        for (User user : serviceUser.findByName(lastName)) {
            System.out.println(user.getId() + " " + user.getFirstName()
                                + " " + user.getLastName());
        }

        System.out.println("Introduceti id-ul:");
        Long id = in.nextLong();

        User result = serviceUser.deleteUser(id);
        if(result == null)
            System.out.println("Nu exista utilizatorul cu id-ul specificat!");
        else
            System.out.println("Utilizator sters cu succes!");
    }

    /**
     * afiseaza toti utilizatorii din aplicatie
     */
    public void uiGetUsers () {
        for (User user : serviceUser.getAll()) {
            System.out.println(user);
        }
    }

    /**
     * afiseaza toti utilizatorii din aplicatie
     * preia de la utilizator doua id-uri
     * creeaza o prietenie intre utilizatorii cu id-urile specificate
     */
    public void uiAddFriendship () {
        Scanner in = new Scanner(System.in);
        uiGetUsers();
        System.out.println("Introduceti id-ul pentru primul prieten:");
        Long id1 = in.nextLong();
        System.out.println("Introduceti id-ul pentru al doilea prieten:");
        Long id2 = in.nextLong();

        serviceFriend.addFriendship(new Friendship(id1, id2, LocalDateTime.now()));
    }

    /**
     * afiseaza toate prieteniile din aplicatie
     * preia de la utilizator doua id-uri
     * sterge prietenia dintre utilizatorii cu id-urile specificate
     */
    public void uiDeleteFriendship () {
        Scanner in = new Scanner(System.in);
        int i = 1;
        for (Friendship friendship : serviceFriend.getAll()) {
            User user1 = serviceUser.getOne(friendship.getId().getLeft());
            User user2 = serviceUser.getOne(friendship.getId().getRight());
            System.out.println("Prietenia " + i);
            System.out.println(user1);
            System.out.println(user2);
            i++;
        }
        System.out.println("Introduceti id-ul pentru primul prieten:");
        Long id1 = in.nextLong();
        System.out.println("Introduceti id-ul pentru al doilea prieten:");
        Long id2 = in.nextLong();

        serviceFriend.deleteFriendship(new Tuple<>(id1, id2));
    }

    /**
     * afiseaza numarul de comunitati din aplicatie
     */
    public void uiComunitiesNumber() {
        System.out.println("Numarul de comunitati este: " + serviceUser.communitiesNumber());
    }

    /**
     * afiseaza utilizatorii din cea mai sociabila comunitate
     */
    public void uiLargestCommunity() {
        String rez = "";
        for(User user : serviceUser.largestCommunity()) {
            rez += user.getId() + " '" + user.getFirstName() + " " + user.getLastName() + "'" + " - ";
        }
        System.out.println("Cea mai sociabila comunitate este formata din urmatorii utilizatori:");
        System.out.println(rez);
    }

    public void uiUserFriends() {
        Scanner in = new Scanner(System.in);
        System.out.println("Introduce the last name:");
        String lastName = in.nextLine();
        for (User user : serviceUser.findByName(lastName)) {
            System.out.println(user.getId() + " " + user.getFirstName()
                    + " " + user.getLastName());
        }

        System.out.println("Introduceti id-ul:");
        Long id = in.nextLong();

        if( !serviceUser.userFriends(id) ) {
            System.out.println("The user has no friends!");
        }
    }

    public void uiUserFriendsFromMonth() {
        Scanner in = new Scanner(System.in);
        System.out.println("Introduce the last name:");
        String lastName = in.nextLine();
        for (User user : serviceUser.findByName(lastName)) {
            System.out.println(user.getId() + " " + user.getFirstName()
                    + " " + user.getLastName());
        }

        System.out.println("Introduceti id-ul:");
        Long id = in.nextLong();

        System.out.println("Introduceti luna din an:");
        int month = in.nextInt();

        System.out.println("Introduceti anul:");
        int year = in.nextInt();

        if( !serviceUser.userFriendsFromMonth(id, month, year) ) {
            System.out.println("The user has no friendships created in this month!");
        }
    }

    /*public void uiRegister() {
        Scanner in = new Scanner(System.in);
        System.out.println("First name:");
        String firstName = in.nextLine();
        System.out.println("Last Name");
        String lastName = in.nextLine();

        for (User user : serviceUser.findByName(lastName)) {
            if(user.getFirstName().equals(firstName)) {
                System.out.println(user.getId() + " " + user.getFirstName()
                        + " " + user.getLastName());
            }
        }

        System.out.println("Id: ");
        Long id = in.nextLong();

        boolean result = serviceUser.register(id);
        if(result == true)
            System.out.println("Successfully register!");
        else
            System.out.println("Account already exist!");
        serviceUser.getAccounts().forEach(x-> {
            System.out.println(x);
        });
    }*/

    public int uiLogin() {
        Scanner in = new Scanner(System.in);
        System.out.println("First name:");
        String firstName = in.nextLine();
        System.out.println("Last Name");
        String lastName = in.nextLine();

        for (User user : serviceUser.findByName(firstName)) {
            if(user.getFirstName().equals(lastName)) {
                System.out.println(user.getId() + " " + user.getFirstName()
                        + " " + user.getLastName());
            }
        }

        System.out.println("Id: ");
        Long id = in.nextLong();

        if(serviceUser.login(id) == true) {
            System.out.println(serviceUser.getOne(id) + " successfully logged in!");
            return Math.toIntExact(serviceUser.getLoggedIn());
        }

        System.out.println("Account does not exist!");
        return 0;
    }

    public void uiLogout() {
        serviceUser.setLoggedIn(null);
    }

    public void uiShowConversation() {
        Long idFrom = serviceUser.getLoggedIn();
        System.out.println("Introduce the second user's id:");
        Scanner in = new Scanner(System.in);
        Long idTo = in.nextLong();

        serviceMessage.showConversation(idFrom, idTo);
    }

    public void uiSendMessage() {
        Scanner in  = new Scanner(System.in);

        System.out.println("Introduce the message:");
        String message = in.nextLine();

        System.out.println("Introduce the number of users:");
        int nb = in.nextInt();

        User from = serviceUser.getOne(serviceUser.getLoggedIn());

        System.out.println("Introduce the ids:");
        List<User> toList = new ArrayList<>();
        Long idTo;
        for(int i=0; i<nb ;i++) {
            idTo = in.nextLong();
            toList.add(serviceUser.getOne(idTo));
        }

        serviceMessage.save(new Message(from, toList, message));
    }

    public void uiReplyMessage() {
        Scanner in  = new Scanner(System.in);

        System.out.println("Introduce the reply message:");
        String message = in.nextLine();

        User from = serviceUser.getOne(serviceUser.getLoggedIn());

        System.out.println("Introduce the other user's id:");
        Long idTo = in.nextLong();
        User to = serviceUser.getOne(idTo);
        List<User> toList = new ArrayList<>();
        toList.add(to);

        System.out.println("Introduce the id of the message you want to reply to:");
        Long idReply = in.nextLong();
        Message replyMessage = serviceMessage.getOne(idReply);

        Message messageToSend = new Message(from, toList, message);
        messageToSend.setReply(replyMessage);

        serviceMessage.save(messageToSend);
    }

    public void uiAddFriend() {
        Scanner in = new Scanner(System.in);
        Long id1 = serviceUser.getLoggedIn();

        System.out.println("Introduce the user id:");
        Long id2 = in.nextLong();

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
                System.out.println("You have already sent a friend request to this user!");
            if(status == 2)
                System.out.println("This user is already your friend!");
            if(status == 3)
                System.out.println("Request sent again!");
        }
    }

    public void uiFriendReguest() {
        Scanner in = new Scanner(System.in);
        Long idLog = serviceUser.getLoggedIn();
        boolean exist = false;

        List<User> users = serviceFriend.friendRequest(idLog, Status.PENDING);
        for(User user:users) {
            System.out.println(user);
            exist = true;
        }
        if(exist == true) {
            System.out.println();

            System.out.println("Introduce the id:");
            Long idNLog = in.nextLong();
            if (users.contains(serviceUser.getOne(idNLog))) {
                System.out.println("1 - approve");
                System.out.println("0 - reject");

                int nb = in.nextInt();
                if (nb == 1)
                    serviceFriend.changeFriendshipStatus(idLog, idNLog, Status.APPROVED);
                else if (nb == 0) {
                    serviceFriend.changeFriendshipStatus(idLog, idNLog, Status.REJECTED);
                }
            }
            else {
                System.out.println("You have no friend request from this user!");
            }
        }
    }

    /**
     * afiseaza in consola un meniu pentru un utilizator logat
     */
    public void adminMenu(){
        System.out.println("-------------------------------------");
        System.out.println("1 - Adauga utilizator");
        System.out.println("2 - Sterge utilizator");
        System.out.println("3 - Afiseaza utilizatori");
        System.out.println("4 - Adauga prietenie");
        System.out.println("5 - Sterge prietenie");
        System.out.println("6 - Afiseaza prietenii");
        System.out.println("7 - Numarul de comunitati");
        System.out.println("8 - Cea mai sociabila comunitate");
        System.out.println("9 - Prieteniile unui utilizator");
        System.out.println("10 - Prieteniile dintr-o luna");
        System.out.println("11 - Conversatii");
        System.out.println("12 - Trimite mesaj");
        System.out.println("13 - Trimite raspuns");
        System.out.println("14 - Cereri de prietenie");
        System.out.println("15 - Adauga prieten");
        System.out.println("16 - Sterge prieten");
        System.out.println("0 - Logout");
        System.out.println("-------------------------------------");
        System.out.println();
        System.out.println("Introduceti o comanda: ");
    }

    public void userMenu() {
        System.out.println("-------------------------------------");
        System.out.println("1 - Prieteniile unui utilizator");
        System.out.println("2 - Prieteniile dintr-o luna");
        System.out.println("3 - Conversatii");
        System.out.println("4 - Trimite mesaj");
        System.out.println("5 - Trimite raspuns");
        System.out.println("6 - Cereri de prietenie");
        System.out.println("7 - Adauga prieten");
        System.out.println("0 - Logout");
        System.out.println("-------------------------------------");
        System.out.println();
        System.out.println("Introduceti o comanda: ");
    }

    public void defaultMenu() {
        System.out.println("-------------");
        System.out.println("1 - Login");
        System.out.println("2 - Exit");
        System.out.println("-------------");
    }

    public void defaultRun() {
        int cmd;
        while (true) {
            defaultMenu();
            Scanner in = new Scanner(System.in);
            cmd = in.nextInt();
            switch (cmd) {
                case 1:
                    int rez = uiLogin();
                    if(rez == 11)
                        adminRun();
                    else if(rez != 0)
                        userRun();
                    break;
                case 2:
                    return;
            }
        }
    }

    /**
     * preia fiecare comanda de la utilizator si o executa
     */
    public void adminRun(){
        int cmd;
        while (true) {
            adminMenu();
            Scanner in = new Scanner(System.in);
            cmd = in.nextInt();
            try {
                switch (cmd) {
                    case 1:
                        uiAddUser();
                        break;
                    case 2:
                        uiDeleteUser();
                        break;
                    case 3:
                        uiGetUsers();
                        break;
                    case 4:
                        uiAddFriendship();
                        break;
                    case 5:
                        uiDeleteFriendship();
                        break;
                    case 7:
                        uiComunitiesNumber();
                        break;
                    case 8:
                        uiLargestCommunity();
                        break;
                    case 9:
                        uiUserFriends();
                        break;
                    case 10:
                        uiUserFriendsFromMonth();
                        break;
                    case 11:
                        uiShowConversation();
                        break;
                    case 12:
                        uiSendMessage();
                        break;
                    case 13:
                        uiReplyMessage();
                        break;
                    case 14:
                        uiFriendReguest();
                        break;
                    case 15:
                        uiAddFriend();
                        break;
                    case 0:
                        uiLogout();
                        return;
                    default:
                        System.out.println("Comanda invalida");
                }
            } catch (ValidationException ex) {
                System.out.println(ex.getMessage());
            }
            catch (ServiceException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void userRun(){
        int cmd;
        while (true) {
            userMenu();
            Scanner in = new Scanner(System.in);
            cmd = in.nextInt();
            try {
                switch (cmd) {
                    case 1:
                        uiUserFriends();
                        break;
                    case 2:
                        uiUserFriendsFromMonth();
                        break;
                    case 3:
                        uiShowConversation();
                        break;
                    case 4:
                        uiSendMessage();
                        break;
                    case 5:
                        uiReplyMessage();
                        break;
                    case 6:
                        uiFriendReguest();
                        break;
                    case 7:
                        uiAddFriend();
                        break;
                    case 0:
                        uiLogout();
                        return;
                    default:
                        System.out.println("Comanda invalida");
                }
            } catch (ValidationException ex) {
                System.out.println(ex.getMessage());
            }
            catch (ServiceException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
