package com.wikicoding.application.usecases.team;

import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.common.queries.FindTeamByIdQuery;
import com.wikicoding.core.domain.team.Team;
import com.wikicoding.core.ports.inbound.FindTeamById;

import java.time.LocalDateTime;

public class FindTeamByIdEventHandler implements FindTeamById {
    private final EventSourcingHandler eventSourcingHandler;

    public FindTeamByIdEventHandler(EventSourcingHandler eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public TeamDto handle(FindTeamByIdQuery query) {
        Team team = (Team) eventSourcingHandler.getByAggregateId(query.getTeamId());

        return new TeamDto(team.getTeamName(), team.getVersion(), LocalDateTime.now());
    }
}
