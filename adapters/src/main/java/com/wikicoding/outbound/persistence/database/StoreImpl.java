package com.wikicoding.outbound.persistence.database;

import com.wikicoding.core.domainevents.BaseDomainEvent;
import com.wikicoding.core.domainevents.MatchCreatedEvent;
import com.wikicoding.core.domainevents.MatchResultedEvent;
import com.wikicoding.core.domainevents.TeamCreatedEvent;
import com.wikicoding.core.ports.outbound.EventStoreRepository;
import com.wikicoding.outbound.persistence.cache.RedisCache;
import com.wikicoding.outbound.persistence.datamodels.CacheEventDataModel;
import com.wikicoding.outbound.persistence.datamodels.EventDataModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class StoreImpl implements EventStoreRepository {
    private final RedisCache redisCache;
    private final EventStore eventStore;
    private final Logger logger = LoggerFactory.getLogger(StoreImpl.class);

    @Override
    public List<BaseDomainEvent> findByAggregateId(String aggregateId) {
//        Optional<CacheEventDataModel> cachedEvent = redisCache.findById(aggregateId);

        long startTime = System.currentTimeMillis();
        List<EventDataModel> events = eventStore.findByAggregateId(aggregateId);
        long endTime = System.currentTimeMillis();
        logger.info("findByAggregateId: Retrieved data in {} ms", (endTime - startTime));
//        List<EventDataModel> events = getEventDataModels(aggregateId, cachedEvent);

//        logger.info("findByAggregateId: Found Events from aggregateId {}", aggregateId);

        List<BaseDomainEvent> foundEvents = new ArrayList<>();

        events.forEach(eventDataModel -> foundEvents.add(eventDataModel.getBaseDomainEvent()));

        return foundEvents;
    }

//    private List<EventDataModel> getEventDataModels(String aggregateId, Optional<CacheEventDataModel> cachedEvent) {
//
//
//        if (cachedEvent.isPresent()) {
//            logger.info("findByAggregateId: Cache hit");
//            long startTime = System.currentTimeMillis();
//
//            CacheEventDataModel cache = cachedEvent.get();
//
//
//
//            for (CacheEventDataModel ev : cachedEvents) {
//                BaseDomainEvent domainEvent = ev.getBaseDomainEvent();
//
//                if (domainEvent instanceof TeamCreatedEvent teamCreatedEvent) {
//                    events.add(new EventDataModel(
//                            ev.getEventId(), ev.getCreatedAt(), ev.getVersion(), ev.getAggregateId(), teamCreatedEvent));
//                }
//
//                if (domainEvent instanceof MatchCreatedEvent matchCreatedEvent) {
//                    events.add(new EventDataModel(
//                            ev.getEventId(), ev.getCreatedAt(), ev.getVersion(), ev.getAggregateId(), matchCreatedEvent));
//                }
//
//                if (domainEvent instanceof MatchResultedEvent matchResultedEvent) {
//                    events.add(new EventDataModel(
//                            ev.getEventId(), ev.getCreatedAt(), ev.getVersion(), ev.getAggregateId(), matchResultedEvent));
//                }
//            }
//
//            long endTime = System.currentTimeMillis();
//            logger.info("findByAggregateId: Retrieved data in {} ms", (endTime - startTime));
//        }
//
//        if (cachedEvents.isEmpty()) {
//            logger.info("findByAggregateId: Cache miss");
//            long startTime = System.currentTimeMillis();
//            events = eventStore.findByAggregateId(aggregateId);
//            long endTime = System.currentTimeMillis();
//            logger.info("findByAggregateId: Retrieved data in {} ms", (endTime - startTime));
//
//            String matchName = "";
//            String matchTeamA = "";
//            String matchTeamB = "";
//            String matchScore = "";
//
//            for (EventDataModel eventDataModel : events) {
//                if (eventDataModel.getBaseDomainEvent() instanceof TeamCreatedEvent teamCreatedEvent) {
//                    redisCache.save(new CacheEventDataModel(eventDataModel.getEventId(), eventDataModel.getCreatedAt(),
//                            eventDataModel.getAggregateId(), eventDataModel.getVersion(), teamCreatedEvent.getTeamName()));
//                    logger.info("findByAggregateId: Stored query data for aggregateId {} in cache", aggregateId);
//                }
//
//                if (eventDataModel.getBaseDomainEvent() instanceof MatchCreatedEvent matchCreatedEvent) {
//                    matchName = matchCreatedEvent.getMatchName();
//                    matchTeamA = matchCreatedEvent.getTeamA();
//                    matchTeamB = matchCreatedEvent.getTeamB();
//                    matchScore = matchCreatedEvent.getMatchScore();
//
//                    redisCache.save(new CacheEventDataModel(eventDataModel.getEventId(), eventDataModel.getCreatedAt(),
//                            eventDataModel.getAggregateId(), eventDataModel.getVersion(), matchName,
//                            matchTeamA, matchTeamB, matchScore));
//                    logger.info("findByAggregateId: Stored query data for aggregateId {} in cache", aggregateId);
//                }
//
//                if (eventDataModel.getBaseDomainEvent() instanceof MatchResultedEvent matchResultedEvent) {
//                    redisCache.save(new CacheEventDataModel(eventDataModel.getEventId(), eventDataModel.getCreatedAt(),
//                            eventDataModel.getAggregateId(), eventDataModel.getVersion(), matchName,
//                            matchTeamA, matchTeamB, matchResultedEvent.getMatchScore()));
//                    logger.info("findByAggregateId: Stored query data for aggregateId {} in cache", aggregateId);
//                }
//            }
//
//        }
//
//        if (events.isEmpty()) {
//            logger.error("findByAggregateId: Events not found for aggregateId {}", aggregateId);
//            throw new RuntimeException("Not found");
//        }
//        return events;
//    }

    @Override
    public void saveEvents(String aggregateId, List<BaseDomainEvent> events, int expectedVersion) {
        List<EventDataModel> eventStream = eventStore.findByAggregateId(aggregateId);

        List<EventDataModel> stream = eventStream.stream()
                .sorted(Comparator.comparingInt(EventDataModel::getVersion)).toList();

        if (!stream.isEmpty() && stream.get(stream.size() - 1).getVersion() >= expectedVersion) {
            logger.error("saveEvents: Expected version was {} and the latest version on the aggregate was {}",
                    expectedVersion, stream.get(stream.size() - 1).getVersion());
            throw new RuntimeException("Concurrency Exception");
        }

        int version = expectedVersion;

        for (BaseDomainEvent event : events) {
            EventDataModel eventDataModel = new EventDataModel(event.getEventId(), event.getCreatedAt(), version,
                    event.getAggregateId(), event);

            version++;

            eventStore.save(eventDataModel);

            logger.info("saveEvents: Deleting cache entry for aggregate id {} ", event.getAggregateId());
//            redisCache.deleteById(event.getAggregateId());

            logger.info("saveEvents: Saved Event with eventId {}", event.getEventId());

            // we can produce messages here
            logger.info("saveEvents: Publishing event {}", event.getEventId());
        }
    }
}
