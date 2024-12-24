package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.common.queries.FindAllTeamsQuery;

import java.util.List;

public interface FindAllTeams {
    List<TeamDto> handle(FindAllTeamsQuery query);
}
