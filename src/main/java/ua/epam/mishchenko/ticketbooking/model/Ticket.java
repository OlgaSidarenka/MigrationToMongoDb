package ua.epam.mishchenko.ticketbooking.model;

import java.util.Objects;

/**
 * The type Ticket.
 */
public class Ticket {

    /**
     * The Id.
     */
    private String id;

    /**
     * The Event entity.
     */
    private String eventId;

    /**
     * The Place.
     */
    private Integer place;

    /**
     * The Category.
     */
    private Category category;

    /**
     * Instantiates a new Ticket.
     */
    public Ticket() {
    }

    /**
     * Instantiates a new Ticket.
     *
     * @param eventId   the event entity id
     * @param place    the place
     * @param category the category
     */
    public Ticket(String eventId, int place, Category category) {
        this();
        this.eventId = eventId;
        this.place = place;
        this.category = category;
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
     * Gets category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public int getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(int place) {
        this.place = place;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) && Objects.equals(eventId, ticket.eventId) && Objects.equals(place, ticket.place) && category == ticket.category;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    public int hashCode() {
        return Objects.hash(id, eventId, place, category);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    public String toString() {
        return "{" +
                "'id' : " + id +
                ", 'eventId' : " + eventId +
                ", 'place' : " + place +
                ", 'category' : '" + category +
                "'}";
    }
}
