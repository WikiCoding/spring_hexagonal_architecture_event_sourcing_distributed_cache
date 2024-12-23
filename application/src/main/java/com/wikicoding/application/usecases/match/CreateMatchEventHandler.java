package com.wikicoding.application.usecases.match;

import com.wikicoding.common.commands.CreateMatchCommand;
import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.core.domain.match.Match;
import com.wikicoding.core.domain.match.MatchFactory;
import com.wikicoding.core.ports.inbound.CreateMatch;

import java.time.LocalDateTime;

public class CreateMatchEventHandler implements CreateMatch {
    private final MatchFactory matchFactory;
    private final EventSourcingHandler eventSourcingHandler;

    public CreateMatchEventHandler(MatchFactory matchFactory, EventSourcingHandler eventSourcingHandler) {
        this.matchFactory = matchFactory;
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public MatchDto handle(CreateMatchCommand command) {
        // check if teamA and teamB exist, throws runtime exception if it doesn't exist
        eventSourcingHandler.getByAggregateId(command.getTeamA(), "MATCH_CREATED_EVENT");
        eventSourcingHandler.getByAggregateId(command.getTeamB(), "MATCH_CREATED_EVENT");

        // Create the aggregate
        Match match = matchFactory.createMatchAggregate(command);

        // Store the aggregate
        eventSourcingHandler.save(match);

        // Build & return dto
        return new MatchDto(match.getMatchId(), match.getMatchName(), match.getTeamA(), match.getTeamB(),
                match.getMatchScore(), LocalDateTime.now(), match.getVersion());
    }
}
