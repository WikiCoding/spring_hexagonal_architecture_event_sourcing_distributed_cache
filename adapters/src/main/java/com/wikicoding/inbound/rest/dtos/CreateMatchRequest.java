package com.wikicoding.inbound.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateMatchRequest {
    private String matchName;
    private String teamA;
    private String teamB;
}
