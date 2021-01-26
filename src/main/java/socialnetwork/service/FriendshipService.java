package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService {
    private Repository<Long, User> repoUser;
    private Repository<Tuple<Long, Long>, Friendship> repoFriend;

    public FriendshipService(Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriend) {
        this.repoUser = repoUser;
        this.repoFriend = repoFriend;

        for(Friendship friendship : repoFriend.findAll()) {
            setFriendship(friendship);
        }
    }

    public Friendship getOne(Tuple<Long, Long> id) {
        return repoFriend.findOne(id);
    }

    /**
     * seteaza pentru cei doi utilizatori din cadrul prieteniei listele de prieteni
     * @param friendship
     * @throws ServiceException
     *          daca cel putin unul dintre cei doi utilizatori nu exista in aplicatie
     */
    private void setFriendship(Friendship friendship) {
        User user1 = repoUser.findOne(friendship.getId().getLeft());
        User user2 = repoUser.findOne(friendship.getId().getRight());

        String message = "";
        if (user1 == null)
            message += "Utilizatorul cu id-ul " + friendship.getId().getLeft() + " nu exista\n";
        if(user2 == null)
            message += "Utilizatorul cu id-ul " + friendship.getId().getRight() + " nu exista\n";
        if( ! message.isEmpty())
            throw new ServiceException(message);

        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    /**
     * pentru fiecare din cei doi utilizatori se sterge prietenia dintre ei
     * @param id
     */
    private void unsetFriendship(Tuple<Long, Long> id) {
        User user1 = repoUser.findOne(id.getLeft());
        User user2 = repoUser.findOne(id.getRight());

        String message = "";
        if (user1 == null)
            message += "Utilizatorul cu id-ul " + id.getLeft() + " nu exista\n";
        if(user2 == null)
            message += "Utilizatorul cu id-ul " + id.getRight() + " nu exista\n";
        if( ! message.isEmpty())
            throw new ServiceException(message);

        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
    }

    /**
     * adauga o prietenie si seteaza listele de prieteni celor doi utilizatori
     * @param friendship
     * @return - null, daca prietenia a fost adaugata
     *         - prietenia care exista deja, altfel
     */
    public Friendship addFriendship(Friendship friendship) {
        setFriendship(friendship);
        return repoFriend.save(friendship);
    }

    /**
     * sterge o prietenie si reseteaza listele de prieteni celor doi utilizatori
     * @param id
     * @return - null, daca nu exista
     *         - prietenia stearsa, altfel
     */
    public Friendship deleteFriendship(Tuple<Long, Long> id) {
        unsetFriendship(id);
        return repoFriend.delete(id);
    }

    public List<User> friendRequest(Long id, Status status) {
        List<Friendship> friendshipList = new ArrayList<>();
        repoFriend.findAll().forEach(friendshipList::add);
        List<User> userList = new ArrayList<>();

        friendshipList.stream()
                .filter(friendship -> friendship.getId().getRight().equals(id))
                .filter(friendship -> friendship.getStatus() == status)
                .forEach(friendship-> {
                    User user = repoUser.findOne(friendship.getId().getLeft());
                    userList.add(user);
                });
        return userList;
    }

    public List<Request> allFriendRequests(Long id) {
        List<Friendship> friendshipList = new ArrayList<>();
        repoFriend.findAll().forEach(friendshipList::add);
        List<Request> requests = new ArrayList<>();

        friendshipList.stream()
                .filter(friendship -> friendship.getId().getRight().equals(id) && friendship.getStatus() == Status.PENDING)
                .forEach(friendship-> {
                    User user = repoUser.findOne(friendship.getId().getLeft());
                    requests.add(new Request(user.getId(), user.getFirstName() + " " + user.getLastName(), friendship.getStatus(), friendship.getDate()));
                });
        return requests;
    }

    public void changeFriendshipStatus(Long idLog, Long idNLog, Status status) {
        Friendship friendship = new Friendship(idNLog, idLog, LocalDateTime.now());
        friendship.setStatus(status);
        repoFriend.update(friendship);
    }

    public int sendFriendship(Friendship friendship) {
        Friendship newFriendship = repoFriend.findOne(friendship.getId());
        if(newFriendship.getStatus().equals(Status.PENDING)) {
            //exista, dar nu a acceptat
            return 1;
        }
        else {
            if(newFriendship.getStatus().equals(Status.APPROVED)) {
                //exista deja
                return 2;
            }
            else {
                //exista, dar a refuzat
                changeFriendshipStatus(friendship.getId().getRight(), friendship.getId().getLeft(), Status.PENDING);
                return 3;
            }
        }
    }

    public void removeFriend(Tuple<Long, Long> id) {
        repoFriend.delete(id);
        repoFriend.delete(new Tuple<>(id.getRight(), id.getLeft()));
    }

    /**
     *
     * @return o colectie iterabila de utilizatori
     */
    public Iterable<Friendship> getAll() { return repoFriend.findAll(); }
}
