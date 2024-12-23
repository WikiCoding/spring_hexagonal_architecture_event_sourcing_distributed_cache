package com.wikicoding.common.queries;

public class FindMatchByIdQuery {
    private final String matchId;

    public FindMatchByIdQuery(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) throw new IllegalArgumentException("Match Id can't be null");
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }
}
