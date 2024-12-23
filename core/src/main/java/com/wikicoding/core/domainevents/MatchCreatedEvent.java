package com.wikicoding.core.domainevents;

import java.time.LocalDateTime;

public class MatchCreatedEvent extends BaseDomainEvent {
    private final String matchId;
    private final String matchName;
    private final String teamA;
    private final String teamB;
    private final String matchScore;

    public MatchCreatedEvent(String eventId, LocalDateTime createdAt, int version, String matchId, String matchName,
                             String teamA, String teamB, String matchScore) {
        super(eventId, createdAt, matchId, version);
        this.matchId = matchId;
        this.matchName = matchName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchScore = matchScore;
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
}
