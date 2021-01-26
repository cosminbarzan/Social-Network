package socialnetwork.utils;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;

import java.time.LocalDate;
import java.util.List;

public class Report {
    private LocalDate date;
    private List<FriendD> friends;
    private List<Message> messages;

    public Report(LocalDate date) {
        this.date = date;
        this.friends = null;
        this.messages = null;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<FriendD> getUsers() {
        return friends;
    }

    public void setUsers(List<FriendD> friends) {
        this.friends = friends;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
