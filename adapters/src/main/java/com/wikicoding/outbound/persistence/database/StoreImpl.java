package com.wikicoding.outbound.persistence.database;

import com.wikicoding.core.domainevents.BaseDomainEvent;
import com.wikicoding.core.ports.outbound.EventStoreRepository;
import com.wikicoding.outbound.persistence.cache.CacheService;
import com.wikicoding.outbound.persistence.datamodels.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class StoreImpl implements EventStoreRepository {
    private final CacheService cacheService;
    private final EventStore eventStore;
    private final Logger logger = LoggerFactory.getLogger(StoreImpl.class);

    @Override
    public List<BaseDomainEvent> findByAggregateId(String aggregateId, String eventType) {
        List<EventDataModel> events = cacheService.retrieveFromCache(aggregateId, eventType);
        
        if (events.isEmpty()) {
            long startTime = System.currentTimeMillis();
            events = eventStore.findByAggregateId(aggregateId);

            cacheService.saveToCache(events, eventType);
    
            long endTime = System.currentTimeMillis();
            logger.info("findByAggregateId: Retrieved data in {} ms", (endTime - startTime));
    
            logger.info("findByAggregateId: Found Events from aggregateId {}", aggregateId);
        }

        List<BaseDomainEvent> foundEvents = new ArrayList<>();

        events.forEach(eventDataModel -> foundEvents.add(eventDataModel.getBaseDomainEvent()));

        return foundEvents;
    }

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

        handleDomainEvents(events, expectedVersion);
    }

    private void handleDomainEvents(List<BaseDomainEvent> events, int expectedVersion) {
        int version = expectedVersion;

        for (BaseDomainEvent event : events) {
            event.setVersion(version);
            EventDataModel eventDataModel = new EventDataModel(event.getEventId(), event.getCreatedAt(), version,
                    event.getAggregateId(), event);

            version++;

            eventStore.save(eventDataModel);

            logger.info("saveEvents: Deleting cache entry for aggregate id {} ", event.getAggregateId());
            cacheService.cacheDeleteEntry(event);

            logger.info("saveEvents: Saved Event with eventId {}", event.getEventId());

            // we can produce messages here
            logger.info("saveEvents: Publishing event {}", event.getEventId());
        }
    }
}
