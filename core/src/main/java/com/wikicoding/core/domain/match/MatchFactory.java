package com.wikicoding.core.domain.match;

import com.wikicoding.common.commands.CreateMatchCommand;

public class MatchFactory {
    public Match createNoArgsMatchAggregate() {
        return new Match();
    }

    public Match createMatchAggregate(CreateMatchCommand command) {
        return new Match(command);
    }
}
