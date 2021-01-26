package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Status;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Friendship findOne(Tuple<Long, Long> longId) {
        Friendship rez = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships WHERE id1=? AND id2=?")){
            statement.setBigDecimal(1, BigDecimal.valueOf(longId.getLeft()));
            statement.setBigDecimal(2, BigDecimal.valueOf(longId.getRight()));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status = resultSet.getString("status");
                String stringDate = resultSet.getString("datee");
                LocalDateTime dateTime = getDate(stringDate);

                Status status1;
                switch (status) {
                    case "approved": status1 = Status.APPROVED; break;
                    case "rejected": status1 = Status.REJECTED; break;
                    default: status1 = Status.PENDING; break;
                }

                Friendship friendship = new Friendship(id1, id2, dateTime);
                friendship.setStatus(status1);

                rez = friendship;
                return rez;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rez;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status = resultSet.getString("status");
                String stringDate = resultSet.getString("datee");

                LocalDateTime dateTime = getDate(stringDate);

                Status status1;
                switch (status) {
                    case "approved": status1 = Status.APPROVED; break;
                    case "rejected": status1 = Status.REJECTED; break;
                    default: status1 = Status.PENDING; break;
                }

                Friendship friendship = new Friendship(id1, id2, dateTime);
                friendship.setStatus(status1);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        int i = 0;
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        String status = "";
        switch (entity.getStatus()) {
            case PENDING : status = "pending"; break;
            case APPROVED: status = "approved"; break;
            case REJECTED: status = "rejected"; break;
        };


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships VALUES (?,?,?)")){
            statement.setBigDecimal(1, BigDecimal.valueOf(entity.getId().getLeft()));
            statement.setBigDecimal(2, BigDecimal.valueOf(entity.getId().getRight()));
            statement.setString(3, status);

            i = statement.executeUpdate();
            System.out.println(i+" records inserted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if(i ==0) {
            return new Friendship(0L,0L, LocalDateTime.now());
        }
        return null;
    }

    @Override
    public Friendship delete(Tuple<Long, Long> longId) {
        if (longId == null)
            throw new IllegalArgumentException("id must be not null");

        //Friendship friendship = findOne(longId);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id1=? AND id2=?")){
            statement.setBigDecimal(1, BigDecimal.valueOf(longId.getLeft()));
            statement.setBigDecimal(2, BigDecimal.valueOf(longId.getRight()));
            int i = statement.executeUpdate();
            System.out.println(i+" records deleted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        String status = "";
        switch (entity.getStatus()) {
            case PENDING : status = "pending"; break;
            case APPROVED: status = "approved"; break;
            case REJECTED: status = "rejected"; break;
        };

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET status=? WHERE id1=? AND id2=?")){
            statement.setString(1, status);
            statement.setBigDecimal(2, BigDecimal.valueOf(entity.getId().getLeft()));
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getId().getRight()));
            int i = statement.executeUpdate();
            System.out.println(i+" records updated");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }
}
