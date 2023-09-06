package ua.epam.mishchenko.ticketbooking.model;

import org.springframework.data.mongodb.core.mapping.Field;

public record TicketAggregationItem(
        String ticketId,
        String userId,

        Integer place,
        Category category
) {

}
