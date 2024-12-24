package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.common.queries.FindAllMatchesQuery;

import java.util.List;

public interface FindAllMatches {
    List<MatchDto> handle(FindAllMatchesQuery query);
}
