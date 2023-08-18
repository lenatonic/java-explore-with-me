package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventByInitiator_Id(Long id, Pageable pageable);

    Event findEventByIdAndInitiator_Id(Long idEvent, Long idUser);

    Page<Event> findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore
            (List<Long> users, List<EventState> states, List<Long> categories,
             LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (UPPER(e.annotation) LIKE UPPER(CONCAT('%',:text,'%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%',:text,'%')) OR :text IS NULL) " +
            "AND e.category.id IN (:categories) " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :start " +
            "AND e.eventDate < :end")
    List<Event> getFilterEvents(String text, List<Long> categories, Boolean paid,
                                LocalDateTime start, LocalDateTime end, Pageable pageable);
}