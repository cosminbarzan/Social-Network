package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    Status status;

    public Friendship(Long id1, Long id2,  LocalDateTime date) {
        this.setId(new Tuple<Long, Long>(id1, id2));
        this.date = date;
        status = Status.PENDING;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * determina un cod bazat pe id
     * @return codul
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getId().getLeft(), this.getId().getRight());
    }

    /**
     * verifica daca doua prietenii sunt 'egale'
     * @param obj - un obiect de tipul prietenie
     * @return - true, daca sunt considerate egale
     *         -false, altfel
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Friendship))
            return false;
        Friendship friendship = (Friendship) obj;
        return this.getId().getLeft().equals(friendship.getId().getLeft()) &&
                this.getId().getRight().equals(friendship.getId().getRight());
    }
}
