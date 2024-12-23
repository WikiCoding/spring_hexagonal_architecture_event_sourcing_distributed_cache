package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.commands.CreateTeamCommand;
import com.wikicoding.common.dtos.TeamDto;

public interface CreateTeam {
    TeamDto handle(CreateTeamCommand command);
}
