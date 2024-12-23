package com.wikicoding.core.domain.events;

import java.time.LocalDateTime;

public abstract class BaseDomainEvent {
    private String eventId;
    private LocalDateTime createdAt;
    private String aggregateId;
    private int version;

    public BaseDomainEvent(String eventId, LocalDateTime createdAt, String aggregateId, int version) {
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.aggregateId = aggregateId;
        this.version = version;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getVersion() {
        return version;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
