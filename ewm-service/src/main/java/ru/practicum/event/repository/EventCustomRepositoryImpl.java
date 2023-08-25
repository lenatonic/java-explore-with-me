package ru.practicum.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.util.Patterns;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

@Repository
@RequiredArgsConstructor
public class EventCustomRepositoryImpl implements EventCustomRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Event> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                           String rangeStart, String rangeEnd, int from, int size) {
        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        var builder = em.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        if (text != null) {
            criteria = builder.and(criteria, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (categories != null && categories.size() > 0) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (paid != null) {
            Predicate predicate;
            if (paid) predicate = builder.isTrue(root.get("paid"));
            else predicate = builder.isFalse(root.get("paid"));
            criteria = builder.and(criteria, predicate);
        }

        if (end != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }

        if (start != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        List<Event> events = em.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        return events;
    }

    @Override
    public List<Event> findEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, int from, int size) {
        var builder = em.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        var start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, ofPattern(Patterns.DATE_PATTERN));
        var end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, ofPattern(Patterns.DATE_PATTERN));

        if (rangeStart != null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
        }

        if (rangeEnd != null) {
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));
        }

        if (categories != null && categories.size() > 0) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (users != null && users.size() > 0) {
            criteria = builder.and(criteria, root.get("initiator").in(users));
        }

        if (states != null) {
            criteria = builder.and(criteria, root.get("state").in(states));
        }

        query.select(root).where(criteria);

        var events = em.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        return events;
    }
}