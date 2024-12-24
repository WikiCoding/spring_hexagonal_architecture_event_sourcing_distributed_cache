package com.wikicoding.core.ports.outbound;

import com.wikicoding.core.domain.events.BaseDomainEvent;

import java.util.List;

public interface EventStoreRepository {
    List<BaseDomainEvent> findByAggregateId(String aggregateId, String eventType);
    void saveEvents(String aggregateId, List<BaseDomainEvent> events, int expectedVersion);
    List<BaseDomainEvent> findAll(String eventType);
}
