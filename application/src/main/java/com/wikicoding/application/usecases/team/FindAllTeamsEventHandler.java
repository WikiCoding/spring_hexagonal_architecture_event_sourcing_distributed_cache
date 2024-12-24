package com.wikicoding.application.usecases.team;

import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.common.queries.FindAllTeamsQuery;
import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domain.team.Team;
import com.wikicoding.core.ports.inbound.FindAllTeams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FindAllTeamsEventHandler implements FindAllTeams {
    private final EventSourcingHandler eventSourcingHandler;

    public FindAllTeamsEventHandler(EventSourcingHandler eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public List<TeamDto> handle(FindAllTeamsQuery query) {
        List<AggregateRoot> aggregates = eventSourcingHandler.findAllByEventType("TEAM");
        List<TeamDto> teamDtos = new ArrayList<>();
        aggregates.forEach(aggregateRoot -> {
            Team team = (Team) aggregateRoot;
            teamDtos.add(new TeamDto(team.getTeamName(), team.getVersion(), LocalDateTime.now()));
        });
        return teamDtos;
    }
}
