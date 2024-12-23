package com.wikicoding.core.domain.team;

import com.wikicoding.common.commands.CreateTeamCommand;

public class TeamFactory {
    public Team createNoArgsTeamAggregate() {
        return new Team();
    }

    public Team createTeamAggregate(CreateTeamCommand command) {
        return new Team(command);
    }
}
