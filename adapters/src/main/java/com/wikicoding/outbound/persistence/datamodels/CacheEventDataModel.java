package com.wikicoding.outbound.persistence.datamodels;

import com.wikicoding.core.domainevents.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Events")
@NoArgsConstructor
//@AllArgsConstructor
@Data
public class CacheEventDataModel implements Serializable {
    private String eventId;
    private LocalDateTime createdAt;
    @Id
    private String aggregateId;
    private int version;
//    private BaseDomainEvent baseDomainEvent;
    private String teamName;
    private String matchName;
    private String teamA;
    private String teamB;
    private String matchScore;

    public CacheEventDataModel(String eventId, LocalDateTime createdAt, String aggregateId,
                               int version, String teamName) {
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.aggregateId = aggregateId;
        this.version = version;
        this.teamName = teamName;
    }

    public CacheEventDataModel(String eventId, LocalDateTime createdAt, String aggregateId, int version,
                               String matchName, String teamA, String teamB, String matchScore) {
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.aggregateId = aggregateId;
        this.version = version;
        this.matchName = matchName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchScore = matchScore;
    }
}
