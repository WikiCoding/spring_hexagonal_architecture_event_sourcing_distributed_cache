package com.wikicoding.outbound.persistence.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wikicoding.core.domain.events.BaseDomainEvent;
import com.wikicoding.core.domain.events.MatchCreatedEvent;
import com.wikicoding.core.domain.events.TeamCreatedEvent;
import com.wikicoding.core.ports.outbound.EventStoreRepository;
import com.wikicoding.inbound.rest.exceptions.NotFoundException;
import com.wikicoding.outbound.messaging.KafkaProducer;
import com.wikicoding.outbound.persistence.cache.CacheService;
import com.wikicoding.outbound.persistence.datamodels.EventDataModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static reactor.core.publisher.Mono.when;

public class StoreImplUnitTest {
    private CacheService cacheServiceDouble;
    private EventStore eventStoreDouble;
    private EventStoreRepository storeImpl;
    private final String matchEventType = "MATCH";
    private final String teamEventType = "TEAM";

    @BeforeEach
    public void setUp() {
        cacheServiceDouble = Mockito.mock(CacheService.class);
        eventStoreDouble = Mockito.mock(EventStore.class);
        KafkaProducer kafkaProducerDouble = Mockito.mock(KafkaProducer.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        storeImpl = new StoreImpl(cacheServiceDouble, eventStoreDouble, kafkaProducerDouble, objectMapper);
    }

    /**
     * Since we're managing 2 aggregates in the same EventStore we need Testing for the edge case in which the db can
     * find one event with the specified aggregateId, but it's just not the EventType we're expecting.
     *
     * eg. when we make a call to find a match with an existing teamAggregateId the db returns 1 team event,
     * then it would store it in cache and return a response object with a bunch of nulls.
     * To prevent this, a NotFoundException must be thrown when the eventType is not what we're expecting
     *
     * Below test case of sending a Match Request with a teamAggregateId (fail case)
     */
    @Test
    public void foundExistingTeamAggregateForMatchRequest_ShouldThrowNotFoundException() {
        // Arrange
        String eventId = "6fd6586e-c79c-40d7-ad05-261d82a56be9";
        String teamAggregateId = "FCP";
        int version = 0;

        BaseDomainEvent teamDomainEvent = new TeamCreatedEvent(eventId, LocalDateTime.now(), version, teamAggregateId);
        EventDataModel eventDataModel = new EventDataModel(eventId, LocalDateTime.now(), version, teamAggregateId, teamDomainEvent);
        List<EventDataModel> eventsFromDb = List.of(eventDataModel);

        Mockito.when(cacheServiceDouble.retrieveFromCache(teamAggregateId, matchEventType)).thenReturn(List.of());
        Mockito.when(eventStoreDouble.findByAggregateId(teamAggregateId)).thenReturn(eventsFromDb);

        // Act
        String expectedExceptionMessage = "No match events found for aggregateId: " + teamAggregateId;

        // Assert
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> storeImpl.findByAggregateId(teamAggregateId, matchEventType));

        Assertions.assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    /**
     * Since we're managing 2 aggregates in the same EventStore we need Testing for the edge case in which the db can
     * find one event with the specified aggregateId, but it's just not the EventType we're expecting.
     *
     * eg. when we make a call to find a match with an existing teamAggregateId the db returns 1 team event,
     * then it would store it in cache and return a response object with a bunch of nulls.
     * To prevent this, a NotFoundException must be thrown when the eventType is not what we're expecting
     *
     * Below test case of sending a Team Request with a matchAggregateId (fail case)
     */
    @Test
    public void foundExistingMatchAggregateForTeamRequest_ShouldThrowNotFoundException() {
        // Arrange
        String eventId = "6fd6586e-c79c-40d7-ad05-261d82a56be9";
        String matchAggregateId = "7fd6586e-c79c-40d7-ad05-261d82a56be9";
        int version = 0;
        String matchName = "FCP x SLB";
        String teamA = "FCP";
        String teamB = "SLB";
        String matchScore = "0-0";

        BaseDomainEvent matchDomainEvent = new MatchCreatedEvent(
                eventId, LocalDateTime.now(), version, matchAggregateId, matchName, teamA, teamB, matchScore);
        EventDataModel eventDataModel = new EventDataModel(
                eventId, LocalDateTime.now(), version, matchAggregateId, matchDomainEvent);
        List<EventDataModel> eventsFromDb = List.of(eventDataModel);

        Mockito.when(cacheServiceDouble.retrieveFromCache(matchAggregateId, teamEventType)).thenReturn(List.of());
        Mockito.when(eventStoreDouble.findByAggregateId(matchAggregateId)).thenReturn(eventsFromDb);

        // Act
        String expectedExceptionMessage = "No team events found for aggregateId: " + matchAggregateId;

        // Assert
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> storeImpl.findByAggregateId(matchAggregateId, teamEventType));

        Assertions.assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    /**
     * Since we're managing 2 aggregates in the same EventStore we need Testing for the edge case in which the db can
     * find one event with the specified aggregateId, but it's just not the EventType we're expecting.
     *
     * eg. when we make a call to find a match with an existing teamAggregateId the db returns 1 team event,
     * then it would store it in cache and return a response object with a bunch of nulls.
     * To prevent this, a NotFoundException must be thrown when the eventType is not what we're expecting
     *
     * Below test case of sending a Match Request with a matchAggregateId (success case)
     */
    @Test
    public void foundExistingMatchAggregateForMatchRequest_ShouldReturnFoundEvents() {
        // Arrange
        String eventId = "6fd6586e-c79c-40d7-ad05-261d82a56be9";
        String matchAggregateId = "7fd6586e-c79c-40d7-ad05-261d82a56be9";
        int version = 0;
        String matchName = "FCP x SLB";
        String teamA = "FCP";
        String teamB = "SLB";
        String matchScore = "0-0";

        BaseDomainEvent matchDomainEvent =
                new MatchCreatedEvent(eventId, LocalDateTime.now(), version, matchAggregateId, matchName, teamA, teamB, matchScore);
        EventDataModel eventDataModel =
                new EventDataModel(eventId, LocalDateTime.now(), version, matchAggregateId, matchDomainEvent);
        List<EventDataModel> eventsFromDb = List.of(eventDataModel);

        Mockito.when(cacheServiceDouble.retrieveFromCache(matchAggregateId, matchEventType)).thenReturn(List.of());
        Mockito.when(eventStoreDouble.findByAggregateId(matchAggregateId)).thenReturn(eventsFromDb);

        // Act
        List<BaseDomainEvent> expected = List.of(matchDomainEvent);
        List<BaseDomainEvent> result = storeImpl.findByAggregateId(matchAggregateId, matchEventType);

        // Assert
        Assertions.assertEquals(expected.getFirst().toString(), result.getFirst().toString());
    }

    /**
     * Since we're managing 2 aggregates in the same EventStore we need Testing for the edge case in which the db can
     * find one event with the specified aggregateId, but it's just not the EventType we're expecting.
     *
     * eg. when we make a call to find a match with an existing teamAggregateId the db returns 1 team event,
     * then it would store it in cache and return a response object with a bunch of nulls.
     * To prevent this, a NotFoundException must be thrown when the eventType is not what we're expecting
     *
     * Below test case of sending a Team Request with a teamAggregateId (success case)
     */
    @Test
    public void foundExistingTeamAggregateForTeamRequest_ShouldReturnFoundEvents() {
        // Arrange
        String eventId = "6fd6586e-c79c-40d7-ad05-261d82a56be9";
        String teamAggregateId = "FCP";
        int version = 0;

        BaseDomainEvent teamDomainEvent = new TeamCreatedEvent(eventId, LocalDateTime.now(), version, teamAggregateId);
        EventDataModel eventDataModel = new EventDataModel(eventId, LocalDateTime.now(), version, teamAggregateId, teamDomainEvent);
        List<EventDataModel> eventsFromDb = List.of(eventDataModel);

        Mockito.when(cacheServiceDouble.retrieveFromCache(teamAggregateId, teamEventType)).thenReturn(List.of());
        Mockito.when(eventStoreDouble.findByAggregateId(teamAggregateId)).thenReturn(eventsFromDb);

        // Act
        List<BaseDomainEvent> expected = List.of(teamDomainEvent);
        List<BaseDomainEvent> result = storeImpl.findByAggregateId(teamAggregateId, teamEventType);

        // Assert
        Assertions.assertEquals(expected.getFirst().toString(), result.getFirst().toString());
    }
}
