package socialnetwork.utils;

import socialnetwork.domain.User;

import java.time.LocalDateTime;

public class MessageE {
    private Long id;
    private String from;
    private String to;
    private String message;
    private LocalDateTime date;
    private String reply;

    public MessageE(Long id, String from, String to, String message, LocalDateTime date, String reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
