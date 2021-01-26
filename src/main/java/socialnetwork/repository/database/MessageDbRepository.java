package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Message findOne(Long aLong) {
        Message messagef = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id=?")){
            statement.setBigDecimal(1, BigDecimal.valueOf(aLong));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Long idFrom = resultSet.getLong("fromm");
                Long idTo = resultSet.getLong("too");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("datee").toLocalDateTime();
                Long idReply = resultSet.getLong("reply");

                User from = new User();
                from.setId(idFrom);
                User to = new User();
                to.setId(idTo);

                messagef = new Message();
                messagef.setId(aLong);
                messagef.setFrom(from);
                messagef.setTo(Collections.singletonList(to));
                messagef.setMessage(message);
                messagef.setDate(date);

                if(idReply != null)
                    messagef.setReply(findOne(idReply));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return messagef;
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages")){
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Long idFrom = resultSet.getLong("fromm");
                Long idTo = resultSet.getLong("too");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("datee").toLocalDateTime();
                Long idReply = resultSet.getLong("reply");

                User from = new User();
                from.setId(idFrom);
                User to = new User();
                to.setId(idTo);

                Message messagef = new Message();
                messagef.setId(idMessage);
                messagef.setFrom(from);
                messagef.setTo(Collections.singletonList(to));
                messagef.setMessage(message);
                messagef.setDate(date);

                if(idReply != 0) {
                    Message replyMessage = findOne(idReply);
                    messagef.setReply(replyMessage);
                }
                messages.add(messagef);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return messages;
    }

    @Override
    public Message save(Message entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        if(entity.getReply() != null) {
            for (User user : entity.getTo()) {
                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (fromm,too,message,datee,reply) VALUES (?,?,?,?,?)")) {
                    statement.setBigDecimal(1, BigDecimal.valueOf(entity.getFrom().getId()));
                    statement.setBigDecimal(2, BigDecimal.valueOf(user.getId()));
                    statement.setString(3, entity.getMessage());
                    statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
                    statement.setBigDecimal(5, BigDecimal.valueOf(entity.getReply().getId()));
                    int i = statement.executeUpdate();
                    System.out.println(i + " records inserted");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else {
            for (User user : entity.getTo()) {
                try (Connection connection = DriverManager.getConnection(url, username, password);
                     PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (fromm,too,message,datee) VALUES (?,?,?,?)")) {
                    statement.setBigDecimal(1, BigDecimal.valueOf(entity.getFrom().getId()));
                    statement.setBigDecimal(2, BigDecimal.valueOf(user.getId()));
                    statement.setString(3, entity.getMessage());
                    statement.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
                    int i = statement.executeUpdate();
                    System.out.println(i + " records inserted");

                } catch (SQLException ex) {
                    //ex.printStackTrace();
                    System.out.println(ex);
                }
            }
        }
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
