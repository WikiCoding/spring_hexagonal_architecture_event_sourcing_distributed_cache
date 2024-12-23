package com.wikicoding.core.domainevents;

import java.time.LocalDateTime;

public class MatchResultedEvent extends BaseDomainEvent {
    private final String matchId;
    private final String matchScore;

    public MatchResultedEvent(String eventId, LocalDateTime createdAt, int version, String matchId,
                              String matchScore) {
        super(eventId, createdAt, matchId, version);
        this.matchId = matchId;
        this.matchScore = matchScore;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchScore() {
        return matchScore;
    }
}
