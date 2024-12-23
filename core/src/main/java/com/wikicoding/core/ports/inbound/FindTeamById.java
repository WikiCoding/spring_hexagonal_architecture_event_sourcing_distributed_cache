package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.common.queries.FindTeamByIdQuery;

public interface FindTeamById {
    TeamDto handle(FindTeamByIdQuery query);
}
