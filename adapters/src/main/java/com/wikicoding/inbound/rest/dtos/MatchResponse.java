package com.wikicoding.inbound.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MatchResponse {
    private String matchId;
    private String matchName;
    private String teamA;
    private String teamB;
    private String matchScore;
    private LocalDateTime createdAt;
    private int version;
}
