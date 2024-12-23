package com.wikicoding.inbound.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamResponse {
    private String teamName;
    private int version;
    private LocalDateTime createdAt;
}
