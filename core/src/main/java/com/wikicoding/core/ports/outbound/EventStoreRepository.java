package com.wikicoding.core.ports.outbound;

import com.wikicoding.core.domainevents.BaseDomainEvent;

import java.util.List;

public interface EventStoreRepository {
    List<BaseDomainEvent> findByAggregateId(String aggregateId);
    void saveEvents(String aggregateId, List<BaseDomainEvent> events, int expectedVersion);
}