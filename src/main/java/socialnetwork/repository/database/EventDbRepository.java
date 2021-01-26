package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.util.List;

public class EventDbRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;


    public EventDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public EventDbRepository() {
        super();
    }

    @Override
    public Event findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Event> findAll() { return null; }

    @Override
    public Event save(Event entity) {
        int i= 0;

        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO events (evname, evdate) VALUES (?,?)")
        ){
            statement.setString(1, entity.getEvname());
            statement.setTimestamp(2, Timestamp.valueOf(entity.getEvdate()));

            i = statement.executeUpdate();
            System.out.println(i+" record inserted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if(i == 0)
            return entity;
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
