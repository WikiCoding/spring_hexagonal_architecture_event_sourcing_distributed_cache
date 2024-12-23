package com.wikicoding.common.commands;

public class CreateMatchCommand {
    private final String matchName;
    private final String teamA;
    private final String teamB;

    public CreateMatchCommand(String matchName, String teamA, String teamB) {
        if (matchName == null || matchName.trim().isEmpty()) throw new IllegalArgumentException("Match name can't be empty");
        if (teamA == null || teamA.trim().isEmpty()) throw new IllegalArgumentException("Team A can't be empty");
        if (teamB == null || teamB.trim().isEmpty()) throw new IllegalArgumentException("Team B can't be empty");
        this.matchName = matchName;
        this.teamA = teamA;
        this.teamB = teamB;
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
}
