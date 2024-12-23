package com.wikicoding.common.commands;

public class ResultMatchCommand {
    private final String matchId;
    private final String matchScore;

    public ResultMatchCommand(String matchId, String matchScore) {
        if (matchId == null || matchId.trim().isEmpty()) throw new IllegalArgumentException("Match id can't be empty");
        if (matchScore == null || matchScore.trim().isEmpty()) throw new IllegalArgumentException("Match Score can't be empty");
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
