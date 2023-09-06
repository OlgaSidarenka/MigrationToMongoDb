package ua.epam.mishchenko.ticketbooking.service.impl;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;
import ua.epam.mishchenko.ticketbooking.service.TicketService;
import ua.epam.mishchenko.ticketbooking.web.TicketResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Ticket service.
 */
@Service
public class TicketServiceImpl implements TicketService {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final MongoTemplate mongoTemplate;

    public TicketServiceImpl(UserRepository userRepository, EventRepository eventRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Book ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    @Override
    public TicketResponse bookTicket(String userId, String eventId, int place, Category category) {
        log.info("Start booking a ticket for user with id {}, event with id event {}, place {}, category {}",
                userId, eventId, place, category);
        try {
            return processBookingTicket(userId, eventId, place, category);
        } catch (RuntimeException e) {
            log.warn("Can not to book a ticket for user with id {}, event with id {}, place {}, category {}",
                    userId, eventId, place, category, e);
            log.warn("Transaction rollback");
            return null;
        }
    }

    private TicketResponse processBookingTicket(String userId, String eventId, int place, Category category) {
        throwRuntimeExceptionIfUserNotExist(userId);
        throwRuntimeExceptionIfEventNotExist(eventId);
        User user = userRepository.findById(userId).get();
        throwRuntimeExceptionIfTicketAlreadyBooked(eventId, place, category, user);
        UserAccount userAccount = user.getUserAccount();
        Event event = getEvent(eventId);
        throwRuntimeExceptionIfUserNotHaveEnoughMoney(userAccount, event, userId);
        buyTicket(userAccount, event);
        Ticket ticket = saveBookedTicket(user, eventId, place, category);
        log.info("Successfully booking of the ticket: {}", ticket);
        return new TicketResponse(userId, ticket.getId(), ticket.getPlace(), ticket.getCategory(), event.getTitle(), event.getDate(), event.getTicketPrice(), eventId);
    }

    private Ticket saveBookedTicket(User user, String eventId, int place, Category category) {
        Ticket ticket = new Ticket(eventId, place, category);
        user.getTickets().add(ticket);
        userRepository.save(user);
        return ticket;
    }

    private void buyTicket(UserAccount userAccount, Event event) {
        userAccount.setMoney(subtractTicketPriceFromUserMoney(userAccount, event));
    }

    private BigDecimal subtractTicketPriceFromUserMoney(UserAccount userAccount, Event event) {
        return userAccount.getMoney().subtract(event.getTicketPrice());
    }

    private void throwRuntimeExceptionIfUserNotHaveEnoughMoney(UserAccount userAccount, Event event, String userId) {
        if (!userHasEnoughMoneyForTicket(userAccount, event)) {
            throw new RuntimeException(
                    "The user with id " + userId +
                            " does not have enough money for ticket with event id " + event.getId()
            );
        }
    }

    private void throwRuntimeExceptionIfTicketAlreadyBooked(String eventId, int place, Category category, User user) {
        if (user.getTickets().stream().anyMatch(ticket ->
                eventId.equals(ticket.getEventId()) && place == ticket.getPlace()
                        && category == ticket.getCategory())) {
            throw new RuntimeException("This ticket already booked");
        }
    }

    private Event getEvent(String eventId) {
        return eventRepository.findById(new ObjectId(eventId))
                .orElseThrow(() -> new RuntimeException("Can not to find an event by id: " + eventId));
    }

    private void throwRuntimeExceptionIfEventNotExist(String eventId) {
        if (!eventRepository.existsById(new ObjectId(eventId))) {
            throw new RuntimeException("The event with id " + eventId + " does not exist");
        }
    }

    private void throwRuntimeExceptionIfUserNotExist(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("The user with id " + userId + " does not exist");
        }
    }

    private boolean userHasEnoughMoneyForTicket(UserAccount userAccount, Event event) {
        return userAccount.getMoney().compareTo(event.getTicketPrice()) > -1;
    }

    /**
     * Create new ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    private Ticket createNewTicket(String userId, String eventId, int place, Category category) {
        User user = userRepository.findById(userId).get();
        Event event = eventRepository.findById(new ObjectId(eventId)).get();
        //todo
        return new Ticket(eventId, place, category);
    }

    /**
     * Is user null boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean isUserNull(User user) {
        return user == null;
    }

    /**
     * Gets booked tickets.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<TicketResponse> getBookedTickets(Event event, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by event {} with page size {} and number of page {}",
                event, pageSize, pageNum);
        try {
            if (isEventNull(event)) {
                log.warn("The event can not be a null");
                return new ArrayList<>();
            }
            MatchOperation matchStage = Aggregation.match(new Criteria("tickets.eventId").is(event.getId()));
            UnwindOperation unwindOperation = Aggregation.unwind("tickets");
            ProjectionOperation projectionOperation = Aggregation.project(
                    Fields.from(
                            Fields.field("ticketId", "tickets._id"),
                            Fields.field("userId", "_id"),
                            Fields.field("place", "tickets.place"),
                            Fields.field("category", "tickets.category")
                    ));
            long skip = (long) pageSize * pageNum;
            Aggregation aggregation = Aggregation.newAggregation(
                    unwindOperation, matchStage, Aggregation.skip(skip), Aggregation.limit(pageSize), projectionOperation);
            List<TicketResponse> aggregate = mongoTemplate.aggregate(aggregation, "users", TicketAggregationItem.class)
                    .getMappedResults()
                    .stream()
                    .map(ticket -> new TicketResponse(ticket.userId(), ticket.ticketId(), ticket.place(), ticket.category(), event.getTitle(), event.getDate(), event.getTicketPrice(), event.getId()))
                    .collect(Collectors.toList());;
            if (aggregate.isEmpty()) {
                throw new RuntimeException("Can not to fina a list of booked tickets by event with id: " + event.getId());
            }
            log.info("All booked tickets successfully found by event {} with page size {} and number of page {}",
                    event, pageSize, pageNum);
            return aggregate;
        } catch (RuntimeException e) {
            log.warn("Can not to find a list of booked tickets by event '{}'", event, e);
            return new ArrayList<>();
        }
    }

    /**
     * Is event null boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean isEventNull(Event event) {
        return event == null;
    }

    /**
     * Cancel ticket boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    @Override
    public boolean cancelTicket(String ticketId, String userId) {
        log.info("Start canceling a ticket with id: {}", ticketId);
        try {
            User user = userRepository.findById(userId).orElseThrow();
            boolean removed = user.getTickets()
                    .removeIf(ticket -> ticketId.equals(ticket.getId()));
            if (!removed) {
                throw new RuntimeException("Ticket not found");
            }
            userRepository.save(user);
            log.info("Successfully canceling of the ticket with id: {}", ticketId);
            return true;
        } catch (RuntimeException e) {
            log.warn("Can not to cancel a ticket with id: {}", ticketId, e);
            return false;
        }
    }
}
