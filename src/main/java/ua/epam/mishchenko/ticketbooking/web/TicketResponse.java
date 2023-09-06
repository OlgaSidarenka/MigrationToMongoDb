package ua.epam.mishchenko.ticketbooking.web;

import ua.epam.mishchenko.ticketbooking.model.Category;

import java.math.BigDecimal;
import java.util.Date;

public record TicketResponse(
        String userId,
        String ticketId,
        Integer place,
        Category category,
        String eventTitle,
        Date date,
        BigDecimal ticketPrice,
        String eventId
) {
}
