package socialnetwork.domain;

public class Registration extends Entity<Long>{
    Long userid;
    Long eventid;
    String notific;

    public Registration(Long userid, Long eventid, String notific) {
        this.userid = userid;
        this.eventid = eventid;
        this.notific = notific;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getEventid() {
        return eventid;
    }

    public void setEventid(Long eventid) {
        this.eventid = eventid;
    }

    public String getNotific() {
        return notific;
    }

    public void setNotific(String notific) {
        this.notific = notific;
    }
}
