package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.commands.CreateMatchCommand;
import com.wikicoding.common.dtos.MatchDto;

public interface CreateMatch {
    MatchDto handle(CreateMatchCommand command);
}
