package com.wikicoding.outbound.persistence.cache;

import com.wikicoding.core.domainevents.BaseDomainEvent;
import com.wikicoding.core.domainevents.MatchCreatedEvent;
import com.wikicoding.core.domainevents.MatchResultedEvent;
import com.wikicoding.core.domainevents.TeamCreatedEvent;
import com.wikicoding.outbound.persistence.datamodels.EventDataModel;
import com.wikicoding.outbound.persistence.datamodels.MatchDataModel;
import com.wikicoding.outbound.persistence.datamodels.TeamDataModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CacheService {
    private final TeamCache teamCache;
    private final MatchCache matchCache;
    private final Logger logger = LoggerFactory.getLogger(CacheService.class);

    public List<EventDataModel> retrieveFromCache(String aggregateId, String eventType) {
        List<EventDataModel> events = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        if (eventType.equals("TEAM")) {
            return getTeamEventDataModels(aggregateId, events, startTime);
        }

        if (eventType.equals("MATCH")) {
            return getMatchEventDataModels(aggregateId, events, startTime);
        }

        logger.info("Cache miss!");

        return events;
    }

    public void saveToCache(List<EventDataModel> events, String eventType) {
        logger.info("Saving to cache event of type {}", eventType);

        if (eventType.equals("TEAM")) {
            saveTeamToCache(events);
        }

        if (eventType.equals("MATCH")) {
            saveMatchToCache(events);
        }
    }

    public void cacheDeleteEntry(BaseDomainEvent event) {
        logger.info("handleCacheDelete: Deleting cache entry for aggregate id {}", event.getAggregateId());
        if (event instanceof TeamCreatedEvent teamCreatedEvent) {
            teamCache.deleteById(teamCreatedEvent.getAggregateId());
        }

        if (event instanceof MatchCreatedEvent matchCreatedEvent) {
            matchCache.deleteById(matchCreatedEvent.getAggregateId());
        }

        if (event instanceof MatchResultedEvent matchResultedEvent) {
            matchCache.deleteById(matchResultedEvent.getAggregateId());
        }
    }

    private List<EventDataModel> getTeamEventDataModels(String aggregateId, List<EventDataModel> events, long startTime) {
        Optional<TeamDataModel> teamDataModel = teamCache.findById(aggregateId);
        if (teamDataModel.isEmpty()) {
            logger.info("Team Cache miss!");
            return events;
        }

        logger.info("Team Cache hit!");

        EventDataModel eventDataModel = getTeamEventDataModel(teamDataModel.get());

        events.add(eventDataModel);

        long endTime = System.currentTimeMillis();
        logger.info("Team Cache hit: Retrieved data in {} ms", (endTime - startTime));
        return events;
    }

    private EventDataModel getTeamEventDataModel(TeamDataModel team) {
        BaseDomainEvent domainEvent = new TeamCreatedEvent(team.getEventId(), team.getCreatedAt(),
                team.getVersion(), team.getTeamName());
        return new EventDataModel(team.getEventId(), team.getCreatedAt(),
                team.getVersion(), team.getTeamName(), domainEvent);
    }

    private List<EventDataModel> getMatchEventDataModels(String aggregateId, List<EventDataModel> events, long startTime) {
        Optional<MatchDataModel> matchDataModel = matchCache.findById(aggregateId);
        if (matchDataModel.isEmpty()) {
            logger.info("Match Cache miss!");
            return events;
        }

        logger.info("Match Cache hit!");

        EventDataModel eventDataModel = getMatchEventDataModel(matchDataModel.get());

        events.add(eventDataModel);

        long endTime = System.currentTimeMillis();
        logger.info("Match Cache hit: Retrieved data in {} ms", (endTime - startTime));
        return events;
    }

    private EventDataModel getMatchEventDataModel(MatchDataModel match) {
        MatchCreatedEvent domainEvent = new MatchCreatedEvent(match.getEventId(),match.getCreatedAt(),
                match.getVersion(), match.getMatchId(), match.getMatchName(), match.getTeamA(), match.getTeamB(),
                match.getMatchScore());

        return new EventDataModel(match.getEventId(), match.getCreatedAt(),
                match.getVersion(), match.getMatchId(), domainEvent);
    }

    private void saveMatchToCache(List<EventDataModel> events) {
        String matchName = "";
        String teamA = "";
        String teamB = "";
        for (EventDataModel eventDataModel : events) {
            BaseDomainEvent baseDomainEvent = eventDataModel.getBaseDomainEvent();
            if (baseDomainEvent instanceof MatchCreatedEvent matchCreatedEvent) {
                matchName = matchCreatedEvent.getMatchName();
                teamA = matchCreatedEvent.getTeamA();
                teamB = matchCreatedEvent.getTeamB();

                MatchDataModel matchDataModel = new MatchDataModel(matchCreatedEvent.getMatchId(),
                        matchCreatedEvent.getMatchName(), matchCreatedEvent.getTeamA(), matchCreatedEvent.getTeamB(),
                        matchCreatedEvent.getMatchScore(), eventDataModel.getVersion(), eventDataModel.getEventId(),
                        eventDataModel.getCreatedAt());

                matchCache.save(matchDataModel);
            }

            if (baseDomainEvent instanceof MatchResultedEvent matchResultedEvent) {
                MatchDataModel matchDataModel = new MatchDataModel(matchResultedEvent.getMatchId(), matchName,
                        teamA, teamB, matchResultedEvent.getMatchScore(), eventDataModel.getVersion(),
                        eventDataModel.getEventId(), eventDataModel.getCreatedAt());

                matchCache.save(matchDataModel);
            }
        }
    }

    private void saveTeamToCache(List<EventDataModel> events) {
        for (EventDataModel eventDataModel : events) {
            BaseDomainEvent baseDomainEvent = eventDataModel.getBaseDomainEvent();
            if (baseDomainEvent instanceof TeamCreatedEvent teamCreatedEvent) {
                TeamDataModel teamDataModel = new TeamDataModel(teamCreatedEvent.getTeamName(),
                        teamCreatedEvent.getVersion(), eventDataModel.getEventId(), eventDataModel.getCreatedAt());

                teamCache.save(teamDataModel);
            }
        }
    }
}
