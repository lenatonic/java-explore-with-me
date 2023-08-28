package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.stats.model.ViewStats(" +
            "eh.app as app, eh.uri as uri, COUNT(eh.ip) as hits) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp between :start AND :end " +
            "AND eh.uri in :uris " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStats(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.stats.model.ViewStats(" +
            "eh.app as app, eh.uri as uri, COUNT(DISTINCT eh.ip) as hits) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp between :start AND :end " +
            "AND uri in ( :uris ) " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsUniqueIp(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.stats.model.ViewStats(" +
            "eh.app as app, eh.uri as uri, COUNT(eh.ip) as hits) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp between :start AND :end " +
            "GROUP BY eh.app, eh.uri " +
            "ORDER BY hits DESC")
    List<ViewStats> findStatsWithoutUri(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}