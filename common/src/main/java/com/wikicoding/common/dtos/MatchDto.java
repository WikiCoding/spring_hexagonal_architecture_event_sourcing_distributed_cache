package com.wikicoding.common.dtos;

import java.time.LocalDateTime;

public class MatchDto {
    private final String matchId;
    private final String matchName;
    private final String teamA;
    private final String teamB;
    private final String matchScore;
    private final LocalDateTime createdAt;
    private final int version;

    public MatchDto(String matchId, String matchName, String teamA, String teamB, String matchScore,
                    LocalDateTime createdAt, int version) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchScore = matchScore;
        this.createdAt = createdAt;
        this.version = version;
    }

    public String getMatchScore() {
        return matchScore;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getVersion() {
        return version;
    }
}
