package com.wikicoding.outbound.persistence.datamodels;

import com.wikicoding.core.domainevents.BaseDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value = "events")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDataModel {
    @Id
    private String eventId;
    private LocalDateTime createdAt;
    private int version;
    private String aggregateId;
    private BaseDomainEvent baseDomainEvent;
}
