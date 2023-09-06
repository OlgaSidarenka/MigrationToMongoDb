package ua.epam.mishchenko.ticketbooking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type User.
 */
@Document("users")
public class User {

    /**
     * The Id.
     */
    @MongoId(value = FieldType.OBJECT_ID)
    private String id;

    /**
     * The Name.
     */
    private String name;

    /**
     * The Email.
     */
    private String email;

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * The list of user tickets.
     */
    private List<Ticket> tickets = new ArrayList<>();

    private UserAccount userAccount = new UserAccount();

    /**
     * Instantiates a new User.
     */
    public User() {}

    /**
     * Instantiates a new User.
     *
     * @param id    the id
     * @param name  the name
     * @param email the email
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Instantiates a new User.
     *
     * @param name  the name
     * @param email the email
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets user tickets.
     * @return tickets
     */
    public List<Ticket> getTickets() {
        return tickets;
    }

    /**
     * Gets user account.
     * @return userAccount
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * Sets user account.
     * @param userAccount the user account
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "{" +
                "'id' : " + id +
                ", 'name' : '" + name + '\'' +
                ", 'email' : '" + email + '\'' +
                '}';
    }
}

