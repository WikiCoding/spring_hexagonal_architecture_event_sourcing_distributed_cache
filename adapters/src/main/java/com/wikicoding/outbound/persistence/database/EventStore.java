package com.wikicoding.outbound.persistence.database;

import com.wikicoding.outbound.persistence.datamodels.EventDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStore extends MongoRepository<EventDataModel, String> {
    List<EventDataModel> findByAggregateId(String aggregateId);
}
