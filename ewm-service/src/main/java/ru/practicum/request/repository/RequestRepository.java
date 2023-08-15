package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Boolean existsByRequester_IdAndEvent_Id(Long userId, Long eventId);

    Integer countDistinctByEventAndStatus(Long eventId, RequestStatus status);
}