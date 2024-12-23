package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.commands.ResultMatchCommand;
import com.wikicoding.common.dtos.MatchDto;

public interface ResultMatch {
    MatchDto handle(ResultMatchCommand command);
}
