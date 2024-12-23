package com.wikicoding.outbound.persistence.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikicoding.core.domain.events.BaseDomainEvent;
import com.wikicoding.core.domain.events.MatchCreatedEvent;
import com.wikicoding.core.domain.events.TeamCreatedEvent;
import com.wikicoding.core.ports.outbound.EventStoreRepository;
import com.wikicoding.inbound.rest.exceptions.ConcurrencyException;
import com.wikicoding.inbound.rest.exceptions.NotFoundException;
import com.wikicoding.outbound.messaging.KafkaProducer;
import com.wikicoding.outbound.persistence.cache.CacheService;
import com.wikicoding.outbound.persistence.datamodels.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class StoreImpl implements EventStoreRepository {
    private final CacheService cacheService;
    private final EventStore eventStore;
    private final Logger logger = LoggerFactory.getLogger(StoreImpl.class);
    private final String topic = "hexagonal-architecture-topic";
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Override
    public List<BaseDomainEvent> findByAggregateId(String aggregateId, String eventType) {
        List<EventDataModel> events = cacheService.retrieveFromCache(aggregateId, eventType);
        
        if (events.isEmpty()) {
            long startTime = System.currentTimeMillis();
            events = eventStore.findByAggregateId(aggregateId);

            eventsFoundValidation(aggregateId, eventType, events, startTime);

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
            throw new ConcurrencyException("Concurrency Exception");
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
            produceMessageToKafka(event, eventDataModel);
        }
    }

    private void produceMessageToKafka(BaseDomainEvent event, EventDataModel eventDataModel) {
        try {
            String message = objectMapper.writeValueAsString(eventDataModel);
            logger.info("saveEvents: Publishing event {}", event.getEventId());
            kafkaProducer.sendMessage(topic, message);
        } catch (IOException e) {
            logger.error("saveEvents: Failed to publish event {} due to {}", event.getEventId(), e.getMessage());
            // rollback transaction or instead make use of the outbox pattern to make sure
            // that data is store and messages are produced as well
        }
    }

    private void eventsFoundValidation(String aggregateId, String eventType, List<EventDataModel> events, long startTime) {
        if (events.isEmpty()) {
            logger.error("No events found for aggregateId {}", aggregateId);
            long endTime = System.currentTimeMillis();
            logger.info("findByAggregateId: No events found - Retrieved data in {} ms", (endTime - startTime));
            throw new NotFoundException("No events found for aggregateId: " + aggregateId);
        }

        if (events.get(0).getBaseDomainEvent() instanceof TeamCreatedEvent && eventType.equals("MATCH")) {
            logger.error("Events found for aggregateId {} but not match ones", aggregateId);
            long endTime = System.currentTimeMillis();
            logger.info("findByAggregateId: No events found for match - Retrieved data in {} ms", (endTime - startTime));
            throw new NotFoundException("No match events found for aggregateId: " + aggregateId);
        }

        if (events.get(0).getBaseDomainEvent() instanceof MatchCreatedEvent && eventType.equals("TEAM")) {
            logger.error("Events found for aggregateId {} but not team ones", aggregateId);
            long endTime = System.currentTimeMillis();
            logger.info("findByAggregateId: No events found for team - Retrieved data in {} ms", (endTime - startTime));
            throw new NotFoundException("No team events found for aggregateId: " + aggregateId);
        }
    }
}
