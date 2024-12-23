package com.wikicoding.core.domain.match;

import com.wikicoding.common.commands.CreateMatchCommand;
import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domainevents.BaseDomainEvent;
import com.wikicoding.core.domainevents.MatchCreatedEvent;
import com.wikicoding.core.domainevents.MatchResultedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Match implements AggregateRoot {
    private List<BaseDomainEvent> events = new ArrayList<>();
    private String matchId;
    private String matchName;
    private String teamA;
    private String teamB;
    private String matchScore = "0-0";
    private int version = 0;

    protected Match() {}

    protected Match(CreateMatchCommand command) {
        MatchCreatedEvent event = new MatchCreatedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                version,
                UUID.randomUUID().toString(),
                command.getMatchName(),
                command.getTeamA(),
                command.getTeamB(),
                matchScore
        );

        raiseCreateMatchEvent(event);
    }

    private void raiseCreateMatchEvent(MatchCreatedEvent event) {
        // no business logic needed so calling apply immediately
        applyCreateMatchEvent(event);
    }

    private void applyCreateMatchEvent(MatchCreatedEvent event) {
        matchId = event.getMatchId();
        matchName = event.getMatchName();
        teamA = event.getTeamA();
        teamB = event.getTeamB();
        matchScore = event.getMatchScore();
        // no need to increment version
        events.add(event);
    }

    public void updateScore(String updatedScore) {
        // no business logic needed here so raising event
        MatchResultedEvent event = new MatchResultedEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                version,
                matchId,
                updatedScore
        );

        raiseResultMatchEvent(event);
    }

    private void raiseResultMatchEvent(MatchResultedEvent event) {
        // no business logic needed so calling apply immediately
        applyResultMatchEvent(event);
    }

    private void applyResultMatchEvent(MatchResultedEvent event) {
        matchScore = event.getMatchScore();
        version++;
        events.add(event);
    }

    public void markEventsCommited() {
        events.clear();
    }

    public List<BaseDomainEvent> getEvents() {
        return new ArrayList<>(events);
    }

    public void rebuildState(List<BaseDomainEvent> events) {
        for (BaseDomainEvent event : events) {
            if (event instanceof MatchCreatedEvent matchCreatedEvent) {
                matchId = event.getAggregateId();
                matchName = matchCreatedEvent.getMatchName();
                teamA = matchCreatedEvent.getTeamA();
                teamB = matchCreatedEvent.getTeamB();
            }

            if (event instanceof MatchResultedEvent matchResultedEvent) {
                matchScore = matchResultedEvent.getMatchScore();
                version++;
            }
        }
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public String getMatchScore() {
        return matchScore;
    }

    public int getVersion() {
        return version;
    }
}
