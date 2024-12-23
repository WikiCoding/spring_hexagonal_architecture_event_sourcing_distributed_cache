package com.wikicoding.core.ports.inbound;

import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.common.queries.FindMatchByIdQuery;

public interface FindMatchById {
    MatchDto handle(FindMatchByIdQuery query);
}
