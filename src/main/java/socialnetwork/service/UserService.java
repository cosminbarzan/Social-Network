package socialnetwork.service;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.MainUiController;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Status;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.UserDbRepository;
import socialnetwork.repository.serialize.TrippleDes;
import socialnetwork.utils.FriendD;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private Repository<Long, User> repoUser;
    private Repository<Tuple<Long, Long>, Friendship> repoFriend;
    private Long loggedIn;

    public UserService(Repository<Long, User> repoUser, Repository<Tuple<Long, Long>, Friendship> repoFriend) {
        this.repoUser = repoUser;
        this.repoFriend = repoFriend;
        loggedIn = null;
    }

    public Long getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Long id) {
        loggedIn = id;
    }

    /**
     * adauga un utilizator
     * @param user
     * @return - null, daca a fost adaugat
     *         - utilizatorul care exista deja, altfel
     */
    public User addUser(User user) {
        user.setId(findId());
        User task = repoUser.save(user);
        return task;
    }

    /**
     * sterge un utilizator si prieteniile corespunzatoare acestuia
     * @param id
     * @return null, daca nu exista
     *         utilizatorul sters, altfel
     * @throws ServiceException
     *          daca utilizatorul cu id-ul specificat nu exista
     */
    public User deleteUser(Long id) {
        User user = repoUser.findOne(id);
        if(user == null)
            throw new ServiceException("Utilizatorul cu id-ul " + id + " nu exista!");

        for(User u : user.getFriends()) {
            u.deleteFriend(user);
        }

        Iterable<Friendship> friends = repoFriend.findAll();
        List<Friendship> friendships = new ArrayList<>();
        for(Friendship fr : friends) {
            friendships.add(fr);
        }

        for(Friendship fr : friendships) {
            if(fr.getId().getLeft().equals(id)) {
                Long id2 = fr.getId().getRight();
                repoFriend.delete(new Tuple<>(id, id2));
            }
            if(fr.getId().getRight().equals(id)) {
                Long id2 = fr.getId().getLeft();
                repoFriend.delete(new Tuple<>(id2, id));
            }
        }
        User task = repoUser.delete(id);
        return task;
    }

    /**
     * parcurge in adancime graful
     * @param user - punctul de plecare
     * @param visited - starea fiecarui nod din graf (vizitat sau nevizitat)
     */
    public void dfs(User user, boolean[] visited) {
        visited[Math.toIntExact(user.getId())] = true;
        for(User friend : user.getFriends()) {
            if( ! visited[Math.toIntExact(friend.getId())])
                dfs(friend, visited);
        }
    }

    /**
     * determina numarul de componente conexe ale grafului
     * @return numarul de comunitati
     */
    public int communitiesNumber() {
        boolean[] visited = new boolean[15];
        int count = 0;
        for(User user : repoUser.findAll()) {
            if( ! visited[Math.toIntExact(user.getId())]) {
                count ++;
                dfs(user, visited);
            }
        }
        return count;
    }

    /**
     * coloreaza fiecare componenta conexa cu o culoare diferita (culori = [1,nrNoduri])
     * @param start - punctul de plecare
     * @param colors - colors[i] = culoarea nodului i
     * @param color - culoarea curenta
     * @param graph - graful
     * @param n - numarul de noduri
     */
    public void coloring(int start, int[] colors, int color, int[][] graph, int n) {
        colors[start] = color;
        for(int i=1; i <= n; i++) {
            if(graph[start][i] == 1 && colors[i] == 0)
                coloring(i, colors, color, graph, n);
        }
    }

    /**
     * verifica daca solutie partiala respecta constrangerile unui drum elementar
     * @param k - numarul de noduri din drumul curent
     * @param graph - graful
     * @param road - drumul curent
     * @return - true, daca sunt indeplinite conditile
     *         - false, altfel
     */
    public boolean condition(int k, int[][] graph, int[] road) {
        for(int i=1; i<k; i++)
            if(road[i] == road[k])
                return false;
        for(int i=2; i<=k; i++)
            if(graph[road[i-1]][road[i]] == 0)
                return false;
        return true;
    }

    /**
     * salveaza drumul maxim obtinut
     * @param max - lungimea drumului
     * @param road - drumul
     * @param roadMax - drumul maxim
     */
    public void saveRoad(int max, int[] road, int[] roadMax) {
        for (int i=1; i<=max; i++)
            roadMax[i] = road[i];
    }

    /**
     * determina drumul elementar de lungime maxim care incepe din nodul start
     * @param start - punctul de plecare
     * @param road - drumul
     * @param max - lungimea maxima a drumului
     * @param n - numarul de noduri
     * @param graph - graful
     * @param roadMax - drumul maxim
     * @return - lungimea maxima a drumului
     */
    public int bkt(int start, int[] road, int max, int n, int[][] graph, int[] roadMax) {
        max = 0;
        int index = 2;
        road[1] = start;
        while (index > 1) {
            if (road[index] < n) {
                road[index] ++;
                if(condition(index, graph, road)) {
                    if(index > max) {
                        max = index;
                        saveRoad(max, road, roadMax);
                    }
                    road[++index] = 0;
                }
            }
            else
                index--;
        }
        return max;
    }

    /**
     * determina comunitatea cea mai sociabila
     * @return o colectie iterabila cu utilizatorii care fac parte din comunitate
     */
    public Iterable<User> largestCommunity() {
        //int n = repoUser.size();
        int n=5;
        int[][] graph = new int[n*4][n*42];
        int[] v = new int[n*4];
        int k = 1;
        int[] colors = new int[n*4];
        for(int i=1; i<=n ; i++)
            colors[i] = 0;
        int comp = 1;
        int[] road = new int[n*4];
        int[] roadMax = new int[n*4];
        int max = 0;
        int idMax=0;

        for(User user : repoUser.findAll()) {
            int id = Math.toIntExact(user.getId());
            v[id] = k;
            if(id > idMax) {
                idMax = id;
            }
            k++;
        }

        for(Friendship fr : repoFriend.findAll()) {
            int i = Math.toIntExact(fr.getId().getLeft());
            int j = Math.toIntExact(fr.getId().getRight());
            graph[v[i]][v[j]] = graph[v[j]][v[i]] = 1;

        }

        for(int i=1; i<=n; i++) {
            if(colors[i] == 0) {
                coloring(i, colors, comp, graph, n);
                comp++;
            }
        }
        int copy_max = 0;
        int copy_i = 0;
        for(int i=1; i<=n; i++) {
            max = bkt(i, road, max, n, graph, roadMax);
            for(int j=1; j<=n; j++)
                road[j] = 0;
            if(max > copy_max) {
                copy_max = max;
                copy_i = i;
            }
        }
        max = bkt(copy_i, road, max, n, graph, roadMax);

        int color = colors[roadMax[1]];
        ArrayList<User> users = new ArrayList<>();
        for(int i=1; i<=n; i++) {
            if(colors[i] == color) {
                for(int j=1; j<=idMax; j++) {
                    if(v[j] == i) {
                        users.add(repoUser.findOne(Long.valueOf(j)));
                    }
                }
            }
        }
        return users;
    }

    /**
     * genereaza o lista de utilizatori cu numele specificat
     * @param name
     * @return o colectie iterabila de utilizatori
     */
    public Iterable<User> findByName(String name) {
        List<User> uts = new ArrayList<User>();
        for (User user : repoUser.findAll()) {
            if (user.getLastName().equals(name)) {
                uts.add(user);
            }
        }
        return uts;
    }

    public User getOne(Long id) { return repoUser.findOne(id); };

    public Iterable<User> getAll(){
        return repoUser.findAll();
    }

    /**
     *
     * @return numarul de utilizatori din aplicatie
     */
    public int size () { return repoUser.size(); }

    /**
     * determina un id nefolosit pentru un nou utilizator
     * @return id-ul
     */
    public Long findId() {
        Long id = Long.valueOf(0);
        for(User user : repoUser.findAll()) {
            if (user.getId() > id)
                id = user.getId();
        }
        return id+1;
    }

    public void setExist(boolean[] exist) {
        exist[0] = true;
    }

    public List<User> userFriendsGUI(Long id) {
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);
        List<User> users = new ArrayList<>();

        friendships.stream()
                .filter(x-> x.getId().getLeft().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .forEach(x-> {
                    users.add(repoUser.findOne(x.getId().getRight()));
                });
        friendships.stream()
                .filter(x-> x.getId().getRight().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .forEach(x-> {
                    users.add(repoUser.findOne(x.getId().getLeft()));
                });
        return users;
    }

    public List<FriendD> friendsFromAPeriod(Long id, LocalDate date) {
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);
        List<FriendD> friends = new ArrayList<>();

        friendships.stream()
                .filter(x-> x.getId().getLeft().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .filter(x-> x.getDate().toLocalDate().equals(date))
                .forEach(x-> {
                    friends.add(new FriendD(x.getDate().toLocalDate(), repoUser.findOne(x.getId().getRight())));
                });
        friendships.stream()
                .filter(x-> x.getId().getRight().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .filter(x-> x.getDate().toLocalDate().equals(date))
                .forEach(x-> {
                    friends.add(new FriendD(x.getDate().toLocalDate(), repoUser.findOne(x.getId().getRight())));
                });
        if(friends.size() > 0)
            return friends;
        return null;
    }


    public boolean userFriends(Long id) {
        boolean exist[] = new boolean[1];
        exist[0] = false;
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);

        friendships.stream()
                .filter(x-> x.getId().getLeft().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .forEach(x-> {
                    User friend = repoUser.findOne(x.getId().getRight());
                    System.out.println(friend.getFirstName() + "|" + friend.getLastName() + "|" + x.getDate());
                    exist[0]=true;
                });
        friendships.stream()
                .filter(x-> x.getId().getRight().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .forEach(x-> {
                    User friend = repoUser.findOne(x.getId().getLeft());
                    System.out.println(friend.getFirstName() + "|" + friend.getLastName() + "|" + x.getDate());
                    exist[0]=true;
                });
        return exist[0];
    }

    public boolean userFriendsFromMonth(Long id, int month, int year) {
        boolean exist[] = new boolean[1];
        exist[0] = false;
        //int currentYear = LocalDateTime.now().getYear();
        int currentYear = year;
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);

        friendships.stream()
                .filter(x-> x.getId().getLeft().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .filter(x-> x.getDate().getYear() == currentYear)
                .filter(x-> x.getDate().getMonthValue() == month)
                .forEach(x-> {
                    User friend = repoUser.findOne(x.getId().getRight());
                    System.out.println(friend.getFirstName() + "|" + friend.getLastName() + "|" + x.getDate());
                    exist[0]=true;
                });
        friendships.stream()
                .filter(x-> x.getId().getRight().equals(id))
                .filter(x-> x.getStatus().equals(Status.APPROVED))
                .filter(x-> x.getDate().getYear() == currentYear)
                .filter(x-> x.getDate().getMonthValue() == month)
                .forEach(x-> {
                    User friend = repoUser.findOne(x.getId().getLeft());
                    System.out.println(friend.getFirstName() + "|" + friend.getLastName() + "|" + x.getDate());
                    exist[0]=true;
                });
        return exist[0];
    }

    public boolean login(Long id) {
        if(repoUser.findOne(id) != null && loggedIn == null) {
            setLoggedIn(id);
            return true;
        }
        return false;
    }

    public Long findIdByEmail(String email, String passwd) throws Exception {
        Long id = null;
        TrippleDes td= new TrippleDes();

        for(User user: repoUser.findAll()) {
           if(user.getEmail().equals(email) && td.decrypt(user.getPassword()).equals(passwd)) {
               id = user.getId();
               break;
           }
        }
        return id;
    }

    public boolean areFriends(Long id1, Long id2) {
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);

        for(Friendship fr:friendships) {
            if(fr.getStatus().equals(Status.APPROVED)) {
                if(fr.getId().getLeft() == id1 && fr.getId().getRight() == id2)
                    return true;
                if(fr.getId().getLeft() == id2 && fr.getId().getRight() == id1)
                    return true;
            }
        }
        return false;
    }

    public boolean requestSent(Long id1, Long id2) {
        List<Friendship> friendships = new ArrayList<>();
        repoFriend.findAll().forEach(friendships::add);

        for(Friendship fr:friendships) {
            if(fr.getStatus().equals(Status.PENDING)) {
                if(fr.getId().getLeft() == id1 && fr.getId().getRight() == id2)
                    return true;
            }
        }
        return false;
    }
    /*public boolean register(Long id) {
        for(Long l:accounts) {
            if(l.equals(id))
                return false;
        }
        accounts.add(id);
        return true;
    }*/

    ///TO DO: add other methods
}