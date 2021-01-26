package socialnetwork.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import socialnetwork.FirstPdf;
import socialnetwork.domain.Message;
import socialnetwork.service.*;
import socialnetwork.utils.FriendD;
import socialnetwork.utils.Report;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ReportsController implements Initializable {
    private static UserService serviceUser;
    private static FriendshipService serviceFriend;
    private static MessageService serviceMessage;
    private static EventService serviceEvent;
    private static RegistrationService serviceRegistration;

    private static List<Report> reports = new ArrayList<>();

    private static Long userId;
    private static LocalDate dateStart;
    private static LocalDate dateEnd;

    @FXML
    private AnchorPane anchor_pane3;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis idDay;
    @FXML
    private NumberAxis idActivities;

    @FXML
    private PieChart pieChart;

    public static UserService getServiceUser() {
        return serviceUser;
    }

    public static List<Report> getReports() {
        return reports;
    }

    public static void setData(LocalDate dateStart1, LocalDate dateEnd1, Long userId1){
        dateStart = dateStart1;
        dateEnd = dateEnd1;
        userId = userId1;
    }

    public static void setServices(UserService serviceUser1, FriendshipService serviceFriend1, MessageService serviceMessage1, EventService serviceEvent1, RegistrationService serviceRegistration1) {
        serviceUser = serviceUser1;
        serviceFriend = serviceFriend1;
        serviceMessage = serviceMessage1;
        serviceEvent = serviceEvent1;
        serviceRegistration = serviceRegistration1;
    }

    public void backToDashboard(MouseEvent event) throws IOException {
        FXMLLoader dashboardLoader = new FXMLLoader();
        dashboardLoader.setLocation(getClass().getResource("/views/Dashboard.fxml"));
        Parent fxml = dashboardLoader.load();

        anchor_pane3.getChildren().removeAll();

        anchor_pane3.getChildren().setAll(fxml);

        DashboardController dashboardController = dashboardLoader.getController();
        dashboardController.setServiceUser(serviceUser, serviceFriend, serviceMessage, serviceEvent, serviceRegistration);

        //serviceUser.setLoggedIn(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //dateStart = LocalDate.now().plusDays(-30);
        LocalDate currentDate = dateStart;
        //dateEnd = dateStart.plusDays(3);

        for(int i=dateStart.getDayOfMonth(); i<=dateEnd.getDayOfMonth(); i++) {
            Report report =  new Report(currentDate);
            List<FriendD> list = serviceUser.friendsFromAPeriod(serviceUser.getLoggedIn(), currentDate);
            report.setUsers(list);
            List<Message> list1 = serviceMessage.messagesFromADay(serviceUser.getLoggedIn(), dateStart, dateEnd, currentDate);
            report.setMessages(list1);

            reports.add(report);
            currentDate = currentDate.plusDays(1);
        }

        Collections.sort(reports, Comparator.comparing(report -> report.getDate()));

        XYChart.Series<String, Number> friendsSeries = new XYChart.Series<>();
        friendsSeries.setName("New Friends");

        XYChart.Series<String, Number> messagesSeries = new XYChart.Series<>();
        messagesSeries.setName("Received Messages");

        for(Report report:reports) {
            int nbFriends = 0;
            int nbMessages = 0;
            if(report.getUsers() != null)
                nbFriends = report.getUsers().size();
            if(report.getMessages() != null)
                nbMessages = report.getMessages().size();
            friendsSeries.getData().add(new XYChart.Data<String, Number>(report.getDate().toString(), nbFriends));
            messagesSeries.getData().add(new XYChart.Data<String, Number>(report.getDate().toString(), nbMessages));
        }

        //barChart.getData().addAll(friendsSeries, messagesSeries);
        barChart.getData().setAll(friendsSeries, messagesSeries);


        // Messages Report

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        currentDate = dateStart;
        for(int i=dateStart.getDayOfMonth(); i<=dateEnd.getDayOfMonth(); i++) {
            List<Message> list = serviceMessage.messagesFromADay(serviceUser.getLoggedIn(), dateStart, dateEnd, currentDate);
            List<Message> messages = new ArrayList<>();

            if(list != null) {
                list.stream()
                        .filter(x -> x.getFrom().getId() == userId)
                        .forEach(messages::add);

                pieChartData.add(new PieChart.Data(currentDate.toString(), messages.size()));
            }
            else{
                pieChartData.add(new PieChart.Data(currentDate.toString(), 0));
            }

            currentDate = currentDate.plusDays(1);
        }
        pieChart.setData(pieChartData);

        String[] args = new String[]{};
        FirstPdf.main(args);
    }

    public void defaultReports(MouseEvent event) {
    }
}
