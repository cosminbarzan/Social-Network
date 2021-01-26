package socialnetwork.domain;

import java.time.LocalDateTime;

public class Request {
    Long id;
    private String Name;
    Status status;
    LocalDateTime date;

    public Request(Long id, String name, Status status, LocalDateTime date) {
        this.id = id;
        Name = name;
        this.status = status;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
