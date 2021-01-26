package socialnetwork.utils;

import socialnetwork.domain.User;

import java.time.LocalDate;

public class FriendD {
    private LocalDate date;
    private User user;

    public FriendD(LocalDate date, User user) {
        this.date = date;
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
