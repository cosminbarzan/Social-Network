package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Request;
import socialnetwork.domain.Status;
import socialnetwork.domain.User;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RequestsController implements Initializable {
    private static UserService serviceUser;
    private static FriendshipService serviceFriend;

    @FXML
    private TableView<Request> tableView2;
    @FXML
    private TableColumn<Request, String> nameColumn;
    @FXML
    private TableColumn<Request, String> statusColumn;
    @FXML
    private TableColumn<Request, String> dateColumn;

    @FXML
    private JFXButton approveReqBtn, rejectReqBtn;

    @FXML
    private Pagination pagination2;

    private final ObservableList<Request> requestsList = FXCollections.observableArrayList();

    int itemPerPage = 3;
    int from = 0;

    public static void setServices(UserService serviceUser1, FriendshipService serviceFriend1) {
        serviceUser = serviceUser1;
        serviceFriend = serviceFriend1;
    }

    public List<Request> getTableData() {
        requestsList.clear();

        Long id = serviceUser.getLoggedIn();

        List<Friendship> friendships = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships where" +
                     " id2 = " + id + " and status = 'pending' limit " + itemPerPage + " offset " + from)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
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

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        friendships.forEach(friendship -> {
            User user = serviceUser.getOne(friendship.getId().getLeft());
            requestsList.add(new Request(user.getId(), user.getFirstName() + " " + user.getLastName(), friendship.getStatus(), friendship.getDate()));
        });

        return requestsList;
    }

    private Node createPage(int pageIndex) {
        from = pageIndex * itemPerPage;
        tableView2.setItems(FXCollections.observableArrayList(getTableData()));
        return tableView2;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Long idFrom = serviceUser.getLoggedIn();

        int nbOfRequests = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM friendships " +
                     "where id2 = " + idFrom + " and status = 'pending'")){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                nbOfRequests = resultSet.getInt(1);
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        int nbOfPages = (nbOfRequests / itemPerPage) + 1;
        pagination2.setPageCount(nbOfPages);

        pagination2.setPageFactory(this::createPage);

//        List<Request> stringList = serviceFriend.allFriendRequests(serviceUser.getLoggedIn());
//
//        requestsList.addAll(stringList);
//
//        tableView2.setItems(requestsList);
    }

    public void approveRequest(MouseEvent event) {
        Long idLog = serviceUser.getLoggedIn();
        Long idNLog = tableView2.getSelectionModel().getSelectedItem().getId();

        serviceFriend.changeFriendshipStatus(idLog, idNLog, Status.APPROVED);

        Request selectedRequest = tableView2.getSelectionModel().getSelectedItem();
        tableView2.getItems().remove(selectedRequest);
        tableView2.getSelectionModel().clearSelection();
    }

    public void rejectRequest(MouseEvent event) {
        Long idLog = serviceUser.getLoggedIn();
        Long idNLog = tableView2.getSelectionModel().getSelectedItem().getId();

        serviceFriend.changeFriendshipStatus(idLog, idNLog, Status.REJECTED);

        Request selectedRequest = tableView2.getSelectionModel().getSelectedItem();
        tableView2.getItems().remove(selectedRequest);
        tableView2.getSelectionModel().clearSelection();
    }

    private LocalDateTime getDate(String stringDate) {
        String[] strings =  stringDate.split(" ");

        LocalDate date = LocalDate.parse(strings[0]);

        LocalTime time = LocalTime.parse(strings[1]);

        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return dateTime;
    }
}
