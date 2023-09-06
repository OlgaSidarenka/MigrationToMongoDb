package ua.epam.mishchenko.ticketbooking.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.epam.mishchenko.ticketbooking.model.Event;

import java.util.Date;

@Repository
public interface EventRepository extends MongoRepository<Event, ObjectId> {
    Page<Event> getAllByTitle(PageRequest pageRequest, String title);
    Page<Event> getAllByDate(PageRequest pageRequest, Date day);
}
