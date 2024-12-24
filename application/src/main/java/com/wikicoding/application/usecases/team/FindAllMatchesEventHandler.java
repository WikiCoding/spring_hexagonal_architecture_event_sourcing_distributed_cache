package com.wikicoding.application.usecases.team;

import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.common.queries.FindAllMatchesQuery;
import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domain.match.Match;
import com.wikicoding.core.ports.inbound.FindAllMatches;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FindAllMatchesEventHandler implements FindAllMatches {
    private final EventSourcingHandler eventSourcingHandler;

    public FindAllMatchesEventHandler(EventSourcingHandler eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public List<MatchDto> handle(FindAllMatchesQuery query) {
        List<AggregateRoot> aggregates = eventSourcingHandler.findAllByEventType("MATCH");
        List<MatchDto> matchDtos = new ArrayList<>();
        aggregates.forEach(aggregateRoot -> {
            Match match = (Match) aggregateRoot;
            matchDtos.add(new MatchDto(match.getMatchId(), match.getMatchName(), match.getTeamA(), match.getTeamB(),
                    match.getMatchScore(), LocalDateTime.now(), match.getVersion()));
        });

        return matchDtos;
    }
}
