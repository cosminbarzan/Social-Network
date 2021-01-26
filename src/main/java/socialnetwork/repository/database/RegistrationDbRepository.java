package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.Registration;
import socialnetwork.domain.User;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RegistrationDbRepository implements Repository<Long, Registration> {
    private String url;
    private String username;
    private String password;

    private static Long idLog;

    public static void setIdLog(Long idLog1) {
        idLog = idLog1;
    }

    public RegistrationDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public RegistrationDbRepository() {
        super();
    }

    @Override
    public Registration findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Registration> findAll() {
        Set<Registration> registrations = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM registrations where userid = " + idLog + " and notific = on")){
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long userid = resultSet.getLong(2);
                Long eventid = resultSet.getLong(3);
                String notific = resultSet.getString(4);

                Registration registration = new Registration(userid, eventid, notific);
                registration.setId(id);

                registrations.add(registration);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return registrations;
    }

    @Override
    public Registration save(Registration entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO registrations (userid, eventid) VALUES (?,?)")
        ){
            statement.setBigDecimal(1, BigDecimal.valueOf(entity.getUserid()));
            statement.setBigDecimal(2, BigDecimal.valueOf(entity.getEventid()));

            int i = statement.executeUpdate();
            System.out.println(i+" record inserted");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Registration delete(Long aLong) {
        return null;
    }

    @Override
    public Registration update(Registration entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("UPDATE registrations SET notific=? WHERE userid=? and eventid=?")){
            statement.setString(1, "off");
            statement.setBigDecimal(2, BigDecimal.valueOf(entity.getUserid()));
            statement.setBigDecimal(3, BigDecimal.valueOf(entity.getEventid()));
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
}
