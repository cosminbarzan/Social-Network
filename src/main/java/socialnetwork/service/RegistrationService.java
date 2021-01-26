package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.Registration;
import socialnetwork.domain.Tuple;
import socialnetwork.repository.Repository;

import java.sql.*;

public class RegistrationService {
    private Repository<Long, Registration> repoRegistration;

    public RegistrationService(Repository<Long, Registration> repoRegistration) {
        this.repoRegistration = repoRegistration;
    }

    public Registration save(Registration registration) { return repoRegistration.save(registration); }

    public Registration update (Registration registration) { return repoRegistration.update(registration);}

    public Tuple<Boolean, Boolean> existRegistration(Long idUser, Long idEvent) {
        Tuple<Boolean, Boolean> exist = new Tuple<Boolean, Boolean>(false, false);

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM registrations " +
                     "where userid = " + idUser + " and eventid = " + idEvent);
             PreparedStatement statement2 = connection.prepareStatement("SELECT COUNT(*) FROM registrations " +
                     "where userid = " + idUser + " and eventid = " + idEvent + " and notific = 'on'")
        ){

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                if(resultSet.getInt(1) == 1)
                    exist.setLeft(true);
            }

            resultSet.close();

            resultSet = statement2.executeQuery();

            while(resultSet.next()) {
                if(resultSet.getInt(1) == 1)
                    exist.setRight(true);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return exist;
    }
}
