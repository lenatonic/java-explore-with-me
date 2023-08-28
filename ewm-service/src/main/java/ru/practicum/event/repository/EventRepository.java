package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import javax.persistence.LockModeType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventCustomRepository {
    Page<Event> findEventByInitiatorId(Long id, Pageable pageable);

    Event findEventByIdAndInitiatorId(Long idEvent, Long idUser);

    boolean existsByCategoryId(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query(value = "select e from Event e where e.id = :id")
    Event lockById(Long id);
}