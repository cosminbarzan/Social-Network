package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.MessageE;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MessagesController implements Initializable {
    @FXML
    private TableView<MessageE> tableView3;
    @FXML
    private TableColumn<MessageE, String> fromColumn;
    @FXML
    private TableColumn<MessageE, String> toColumn;
    @FXML
    private TableColumn<MessageE, String> messageColumn;
    @FXML
    private TableColumn<MessageE, String> dateMesColumn;
    @FXML
    private TableColumn<MessageE, String> replyColumn;

    @FXML
    JFXTextField typeMessage;

    @FXML
    JFXButton sendMesBtn, sendReplyBtn;

    @FXML
    Pagination pagination3;

    private static UserService serviceUser;
    private static MessageService serviceMessage;
    private static Long idTo;

    private final ObservableList<MessageE> messageEList = FXCollections.observableArrayList();

    int itemPerPage = 3;
    int from = 0;


    public static void setServiceUser(UserService serviceUser1, MessageService serviceMessage1, Long idTo1) {
        serviceUser = serviceUser1;
        serviceMessage = serviceMessage1;
        idTo = idTo1;
    }

    public List<MessageE> getTableData() {
        messageEList.clear();

        List<Message> messages = new ArrayList<>();

        Long idFrom = serviceUser.getLoggedIn();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "015569");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages where" +
                     " fromm = " + idFrom + " and too = " + idTo + " or fromm = " + idTo +
                     " and too = " + idFrom + " limit " + itemPerPage + " offset " + from)){
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Long idMessage = resultSet.getLong("id");
                Long idFrom2 = resultSet.getLong("fromm");
                Long idTo2 = resultSet.getLong("too");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("datee").toLocalDateTime();
                Long idReply = resultSet.getLong("reply");

                User from = new User();
                from.setId(idFrom2);
                User to = new User();
                to.setId(idTo2);

                Message messagef = new Message();
                messagef.setId(idMessage);
                messagef.setFrom(from);
                messagef.setTo(Collections.singletonList(to));
                messagef.setMessage(message);
                messagef.setDate(date);

                if(idReply != 0) {
                    Message replyMessage = serviceMessage.getOne(idReply);
                    messagef.setReply(replyMessage);
                }

                messages.add(messagef);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return serviceMessage.toMessageE(messages, idFrom, idTo);
    }

    private Node createPage(int pageIndex) {
        from = pageIndex * itemPerPage;
        tableView3.setItems(FXCollections.observableArrayList(getTableData()));
        return tableView3;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Long idFrom = serviceUser.getLoggedIn();

        int nbOfMessages = serviceMessage.anotherUserConv(idFrom, idTo).size();

        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateMesColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        replyColumn.setCellValueFactory(new PropertyValueFactory<>("reply"));

        int nbOfPages = (nbOfMessages / itemPerPage) + 1;
        pagination3.setPageCount(nbOfPages);

        pagination3.setPageFactory(this::createPage);

//        List<MessageE> messageEs = serviceMessage.anotherUserConv(idFrom, idTo);
//
//        messageEList.clear();
//        messageEList.addAll(messageEs);
//
//        tableView3.setItems(messageEList);
    }


    public void sendMessage(MouseEvent event) {
        User from = serviceUser.getOne(serviceUser.getLoggedIn());
        User to = serviceUser.getOne(idTo);

        List<User> toList = new ArrayList<>();
        toList.add(to);

        serviceMessage.save(new Message(from, toList, typeMessage.getText()));
        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Message sent!");

    }

    public void sendReply(MouseEvent event) {
        User from = serviceUser.getOne(serviceUser.getLoggedIn());
        Long idReply = tableView3.getSelectionModel().getSelectedItem().getId();
        User to = serviceMessage.getOne(idReply).getFrom();

        List<User> toList = new ArrayList<>();
        toList.add(to);

        Message replyMessage = serviceMessage.getOne(idReply);

        Message messageToSend = new Message(from, toList, typeMessage.getText());
        messageToSend.setReply(replyMessage);

        serviceMessage.save(messageToSend);

        MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Message sent!");

        List<MessageE> messageEs = serviceMessage.anotherUserConv(from.getId(), to.getId());

        messageEList.clear();
        messageEList.addAll(messageEs);

        tableView3.setItems(messageEList);
    }
}
