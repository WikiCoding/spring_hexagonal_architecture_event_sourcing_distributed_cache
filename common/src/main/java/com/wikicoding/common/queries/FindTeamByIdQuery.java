package com.wikicoding.common.queries;

public class FindTeamByIdQuery {
    private final String teamId;

    public FindTeamByIdQuery(String teamId) {
        if (teamId == null || teamId.trim().isEmpty()) throw new IllegalArgumentException("Team Id can't be null");
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }
}
