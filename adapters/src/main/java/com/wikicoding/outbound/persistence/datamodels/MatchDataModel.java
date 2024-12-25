package com.wikicoding.outbound.persistence.datamodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "match", timeToLive = 60L) // 60secs
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MatchDataModel {
    @Id
    private String matchId;
    private String matchName;
    private String teamA;
    private String teamB;
    private String matchScore = "0-0";
    private int version = 0;
    private String eventId;
    private LocalDateTime createdAt;
}
