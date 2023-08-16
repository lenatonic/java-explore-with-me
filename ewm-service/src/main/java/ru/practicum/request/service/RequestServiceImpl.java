package ru.practicum.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.error.exceptions.WrongEventDateException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        if (requestRepository.existsByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new WrongEventDateException("Вы уже подали заявку на участие в этом событии.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Такого события не существует"));
        if (event.getInitiator().getId() == userId) {
            throw new WrongEventDateException("Вы не можете подать заявку в событии, которое организовали");
        }
        if (event.getState() == EventState.PENDING) {
            throw new WrongEventDateException("Заявки не принимаются на ещё не опубликованное событие");
        }
        Integer count = requestRepository.countAllByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);
        if ((event.getParticipantLimit() != null ||
                event.getParticipantLimit() != 0) && count >= event.getParticipantLimit()) {
            throw new WrongEventDateException("Лимит заявок превышен. Приём заявок приостановлен");
        }
        if (event.getRequestModeration()) {
            return RequestMapper.toParticipationRequestDto(requestRepository.save(ParticipationRequest.builder()
                    .created(LocalDateTime.now())
                    .event(event)
                    .requester(user)
                    .status(RequestStatus.PENDING).build()));
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(RequestStatus.CONFIRMED).build()));
    }

    @Override
    @Transactional
    public ParticipationRequestDto canceledRequest(Long userId, Long requestId) {
        ParticipationRequest ans = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос отсутствует или недоступен для редактирования."));
        ans.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(ans));
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> findRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return requestRepository.findByRequester_Id(userId).stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}