package com.wikicoding.application.usecases.team;

import com.wikicoding.common.commands.CreateTeamCommand;
import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.core.domain.team.Team;
import com.wikicoding.core.domain.team.TeamFactory;
import com.wikicoding.core.ports.inbound.CreateTeam;

import java.time.LocalDateTime;

public class CreateTeamEventHandler implements CreateTeam {
    private final TeamFactory teamFactory;
    private final EventSourcingHandler eventSourcingHandler;

    public CreateTeamEventHandler(TeamFactory teamFactory, EventSourcingHandler eventSourcingHandler) {
        this.teamFactory = teamFactory;
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public TeamDto handle(CreateTeamCommand command) {
        Team team = teamFactory.createTeamAggregate(command);
        eventSourcingHandler.save(team);

        return new TeamDto(team.getTeamName(), team.getVersion(), LocalDateTime.now());
    }
}
