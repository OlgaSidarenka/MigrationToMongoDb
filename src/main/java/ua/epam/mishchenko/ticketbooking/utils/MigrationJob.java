package ua.epam.mishchenko.ticketbooking.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "migration", name = "enabled")
public class MigrationJob {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public MigrationJob(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        System.out.println("Started migration job");
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ticket_booking",
                "postgres", "pass");
             Statement statement = connection.createStatement()){
            ResultSet eventsSet = statement.executeQuery("select * from events");
            Map<Integer, String> eventPostgresIdToMongoId = new HashMap<>();
            while(eventsSet.next()) {
                String title = eventsSet.getString("title");
                Timestamp date = eventsSet.getTimestamp("date");
                int id = eventsSet.getInt("id");
                BigDecimal ticketPrice = eventsSet.getBigDecimal("ticket_price");
                Event savedEvent = eventRepository.save(new Event(title, Date.from(date.toInstant()), ticketPrice));
                eventPostgresIdToMongoId.put(id, savedEvent.getId());
            }
            ResultSet userSet = statement.executeQuery("select * from users");
            Map<Integer, User> usersMap = new HashMap<>();
            while(userSet.next()) {
                String name = userSet.getString("name");
                String email = userSet.getString("email");
                int id = userSet.getInt("id");
                usersMap.put(id, new User(name, email));
            }
            ResultSet userAccountSet = statement.executeQuery("select * from user_accounts");
            while(userAccountSet.next()) {
                int userId = userAccountSet.getInt("user_id");
                BigDecimal money = userAccountSet.getBigDecimal("money");
                usersMap.get(userId).setUserAccount(new UserAccount(money));
            }
            ResultSet ticketSet = statement.executeQuery("select * from tickets");
            while(ticketSet.next()) {
                int userId = ticketSet.getInt("user_id");
                int eventId = ticketSet.getInt("event_id");
                int place = ticketSet.getInt("place");
                String category = ticketSet.getString("category");
                usersMap.get(userId).getTickets().add(new Ticket(eventPostgresIdToMongoId.get(eventId), place, Category.fromString(category)));
            }
            usersMap.values().forEach(userRepository::save);
            System.out.println("Finished migration job");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
