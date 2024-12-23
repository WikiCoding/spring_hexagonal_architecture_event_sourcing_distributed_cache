package com.wikicoding.common.commands;

public class CreateTeamCommand {
    private final String teamName;

    public CreateTeamCommand(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) throw new IllegalArgumentException("Team name can't be empty");
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}
