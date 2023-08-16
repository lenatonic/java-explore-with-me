package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Boolean existsByRequester_IdAndEvent_Id(Long userId, Long eventId);

    Integer countAllByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByRequester_Id(Long userId);
}