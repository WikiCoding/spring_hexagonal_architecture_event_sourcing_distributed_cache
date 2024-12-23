package com.wikicoding.application.usecases.match;

import com.wikicoding.common.commands.ResultMatchCommand;
import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domain.match.Match;
import com.wikicoding.core.ports.inbound.ResultMatch;

import java.time.LocalDateTime;

public class ResultMatchEventHandler implements ResultMatch {
    private final EventSourcingHandler eventSourcingHandler;

    public ResultMatchEventHandler(EventSourcingHandler eventSourcingHandler) {
        this.eventSourcingHandler = eventSourcingHandler;
    }

    @Override
    public MatchDto handle(ResultMatchCommand command) {
        Match match = (Match) eventSourcingHandler.getByAggregateId(command.getMatchId(), "MATCH_RESULTED_EVENT");

        match.updateScore(command.getMatchScore());

        eventSourcingHandler.save(match);

        return new MatchDto(match.getMatchId(), match.getMatchName(), match.getTeamA(), match.getTeamB(),
                match.getMatchScore(), LocalDateTime.now(), match.getVersion());
    }
}
