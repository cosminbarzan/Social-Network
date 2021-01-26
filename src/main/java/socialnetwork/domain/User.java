package socialnetwork.domain;

import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private ImageView photo;
    private String firstName;
    private String lastName;
    private List<User> friends;
    private String email;
    private String password;

    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        //this.setId((long)this.hashCode());
        friends = new ArrayList<User>();
        this.email = email;
        this.password = password;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    /**
     * adauga utilizatorul ot in lista de prieteni a celui curent
     * @param ot - utilizatorul de adaugat
     * @return - ot, daca acesta apare in lista
     *         - null, daca este adaugat
     */
    public User addFriend(User ot) {
        if(friends.contains(ot))
            return ot;
        friends.add(ot);
        return null;
    }

    /**
     * sterge utilizator ot din lista de prieteni a celui curent
     * @param ot - utilizatorul de sters
     * @return - null, daca nu exista in lista
     *         - ot, daca acesta a fost sters
     */
    public User deleteFriend(User ot) {
        if ( ! friends.contains(ot))
            return null;
        friends.remove(ot);
        return ot;
    }

    /**
     *
     * @return - un utilizator intr-un format de string
     */
    @Override
    public String toString() {
        return "Utilizator{" +
                "id= " + this.getId() + ", " +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + listaToString() +
                '}';
    }

    /**
     *
     * @return - lista de prieteni intr-un format de string
     */
    private String listaToString() {
        String rez = new String();
        for(User user : friends){
            rez += "'" + user.firstName + " " + user.lastName + "', ";
        }
        return  rez;
    }

    /**
     * verifica daca doi utilizatori sunt 'egali'
     * @param o - un obiect de tipul utilizator
     * @return - true, daca sunt considerati egali
     *         - false, altfel
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    /**
     * determina un cod bazat pe nume, prenume si prieteni
     * @return - codul
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}