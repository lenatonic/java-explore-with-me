package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.dto.EventFoolDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventByInitiator_Id(Long id, Pageable pageable);

    Event findEventByIdAndInitiator_Id(Long idEvent, Long idUser);

    Page<Event> findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsAfterAndEventDateIsBefore(List<Long> users, List<EventState> states, List<Long> categories,
                                                                                                             LocalDateTime start, LocalDateTime end, Pageable pageable);

//    Page<EventFoolDto> findByInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(List<Long> users, List<EventState> states, List<Long> categories,
//                                                                                                             LocalDateTime start, LocalDateTime end, Pageable pageable);
//    Page<EventFoolDto>findEventWhereInitiator_IdInAndStateInAndCategory_IdInAndEventDateIsBetween(List<Long> users, List<EventState> states, List<Long> categories,
//                                                                                                   LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "WHERE ((:users) IS NULL OR e.initiator.id IN :users) " +
            "AND ((:states) IS NULL OR e.state IN :states) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :start) " +
            "AND ( e.eventDate <= :end)")
    List<Event> searchEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Event> findByStateInAndCategory_IdInAndEventDateIsBetween(List<EventState> states, List<Long> categories,
                                                                           LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Event>findByStateInAndEventDateIsBetween(List<EventState> states, LocalDateTime start, LocalDateTime end, Pageable pageable);

}