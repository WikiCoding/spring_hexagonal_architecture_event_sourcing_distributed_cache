package com.wikicoding.core.domain.events;

import java.time.LocalDateTime;

public class TeamCreatedEvent extends BaseDomainEvent {
    private final String teamName;

    public TeamCreatedEvent(String eventId, LocalDateTime createdAt, int version, String teamName) {
        super(eventId, createdAt, teamName, version);
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}
