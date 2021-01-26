package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.Registration;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventService {
    private Repository<Long, Event> repoEvent;

    public EventService(Repository<Long, Event> repoEvent) {
        this.repoEvent = repoEvent;
    }

    public Event save(Event event) { return repoEvent.save(event); }

    public Long findByName(String name) {
        Long id = 0L;

        String newName = "'";
        newName += name;
        newName += "'";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM events where evname = " + newName)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                id = resultSet.getLong(1);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public List<Event> userEvents(Long idLog) {
        List<Event> events = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT e.evname, e.evdate FROM events e inner join registrations r on e.id = r.eventid where r.userid = " + idLog + " and r.notific = 'on'")){
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String evname = resultSet.getString(1);
                LocalDateTime evdate = getDate(resultSet.getString(2));

                Event event = new Event(evname, evdate);

                events.add(event);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return events;
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }
}
