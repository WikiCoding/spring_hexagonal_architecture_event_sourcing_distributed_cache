package com.wikicoding.core.domain.team;

import com.wikicoding.common.commands.CreateTeamCommand;
import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domainevents.BaseDomainEvent;
import com.wikicoding.core.domainevents.TeamCreatedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team implements AggregateRoot {
    private List<BaseDomainEvent> events = new ArrayList<>();
    private String teamName;
    private int version = 0;

    protected Team() {}

    protected Team(CreateTeamCommand command) {
        TeamCreatedEvent event = new TeamCreatedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                version,
                command.getTeamName()
        );

        raiseTeamCreatedEvent(event);
    }

    private void raiseTeamCreatedEvent(TeamCreatedEvent event) {
        // no business logic needed so calling apply immediately
        applyTeamCreatedEvent(event);
    }

    private void applyTeamCreatedEvent(TeamCreatedEvent event) {
        teamName = event.getTeamName();
        // no need to increment version
        events.add(event);
    }

    public void markEventsCommited() {
        events.clear();
    }

    public List<BaseDomainEvent> getEvents() {
        return events;
    }

    public void rebuildState(List<BaseDomainEvent> events) {
        for (BaseDomainEvent event : events) {
            if (event instanceof TeamCreatedEvent teamCreatedEvent) {
                teamName = teamCreatedEvent.getTeamName();
            }
        }
    }

    public String getTeamName() {
        return teamName;
    }

    public int getVersion() {
        return version;
    }
}
