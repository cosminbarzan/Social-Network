package socialnetwork.domain;

import java.time.LocalDateTime;

public class Event extends Entity<Long> {
    private String evname;
    LocalDateTime evdate;

    public Event(String evname, LocalDateTime evdate) {
        this.evname = evname;
        this.evdate = evdate;
    }

    public String getEvname() {
        return evname;
    }

    public void setEvname(String evname) {
        this.evname = evname;
    }

    public LocalDateTime getEvdate() {
        return evdate;
    }

    public void setEvdate(LocalDateTime evdate) {
        this.evdate = evdate;
    }
}
