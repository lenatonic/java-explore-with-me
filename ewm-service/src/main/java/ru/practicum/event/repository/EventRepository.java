package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findEventByInitiator_Id(Long id, Pageable pageable);

    Event findEventByIdAndInitiator_Id(Long idEvent, Long idUser);

    boolean existsByCategory_Id(Long id);
}