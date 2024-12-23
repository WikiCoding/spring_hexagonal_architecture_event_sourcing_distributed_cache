package com.wikicoding.application.usecases.match;

import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.common.queries.FindMatchByIdQuery;
import com.wikicoding.core.domain.match.Match;
import com.wikicoding.core.ports.inbound.FindMatchById;

import java.time.LocalDateTime;

public class FindMatchByIdEventHandler implements FindMatchById {
    private final EventSourcingHandler eventSourcingHandler;

    public FindMatchByIdEventHandler(EventSourcingHandler eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public MatchDto handle(FindMatchByIdQuery query) {
        Match match = (Match) eventSourcingHandler.getByAggregateId(query.getMatchId(), "MATCH");

        return new MatchDto(match.getMatchId(), match.getMatchName(), match.getTeamA(), match.getTeamB(),
                match.getMatchScore(), LocalDateTime.now(), match.getVersion());
    }
}
