package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDbRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public User findOne(Long aLong) {
        User userf = null;
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id=?")){
             statement.setBigDecimal(1,BigDecimal.valueOf(aLong));
             ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long newId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String passwd = resultSet.getString("passwd");

                User user = new User(firstName, lastName, email, passwd);
                user.setId(newId);

                userf = user;
                return userf;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return userf;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        Set<Friendship> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
        ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String passwd = resultSet.getString("passwd");

                User user = new User(firstName, lastName, email, passwd);
                user.setId(id);

                try (Connection connection1 = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement1 = connection1.prepareStatement("SELECT * FROM friendships");
                     ResultSet resultSet1 = statement1.executeQuery()) {

                    while (resultSet1.next()) {
                        Long id1 = resultSet1.getLong("id1");
                        Long id2 = resultSet1.getLong("id2");
                        String stringDate = resultSet1.getString("datee");
                        LocalDateTime dateTime = getDate(stringDate);

                        Friendship friendship = new Friendship(id1, id2, dateTime);
                        friendships.add(friendship);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                users.add(user);
            }
            for(User user:users) {
                for (Friendship friendship : friendships) {
                    if (user.getId() == friendship.getId().getLeft()) {
                        User user2 = findOne(friendship.getId().getRight());
                        user.addFriend(user2);
                    } else if (user.getId() == friendship.getId().getRight()) {
                        User user2 = findOne(friendship.getId().getLeft());
                        user.addFriend(user2);
                    }
                }
            }
            List<User> frList = new ArrayList<>();
            for(User user:users) {
                for(User u:user.getFriends()) {
                    frList.add(findId(users, u.getId()));
                }
                user.getFriends().clear();
                for(User user1:frList) {
                    user.addFriend(user1);
                }
                frList.clear();
            }

            return users;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return users;
    }

    private User findId(Set<User> users, Long id) {
        for(User user:users) {
            if(user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    private void innerJoin(Set<User> users) {

    }

    private User findById(Set<User> users, Long id) {
        return null;
    }

    @Override
    public User save(User entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users (first_name, last_name, email, passwd) VALUES (?,?,?,?)")
            ){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());

            int i = statement.executeUpdate();
            System.out.println(i+" record inserted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");

        User user = findOne(id);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id=?")){
            statement.setBigDecimal(1, BigDecimal.valueOf(id));
            int i = statement.executeUpdate();
            System.out.println(i+" records deleted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public User update(User entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name=?, last_name=? WHERE id=?")){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getId()));
            int i = statement.executeUpdate();
            System.out.println(i+" records updated");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() {
        Set<User> users = new HashSet<>();
        findAll().forEach(user -> {
            users.add(user);
        });
        return users.size();
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }
}
