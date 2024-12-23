package com.wikicoding.common.dtos;

import java.time.LocalDateTime;

public class TeamDto {
    private final String teamName;
    private final int version;
    private final LocalDateTime createdAt;

    public TeamDto(String teamName, int version, LocalDateTime createdAt) {
        this.teamName = teamName;
        this.version = version;
        this.createdAt = createdAt;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
