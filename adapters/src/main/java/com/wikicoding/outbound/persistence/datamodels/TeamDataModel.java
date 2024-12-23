package com.wikicoding.outbound.persistence.datamodels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash("team")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamDataModel {
    @Id
    private String teamName;
    private int version = 0;
    private String eventId;
    private LocalDateTime createdAt;
}
