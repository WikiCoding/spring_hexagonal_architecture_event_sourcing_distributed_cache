package com.wikicoding.outbound.persistence.cache;

import com.wikicoding.outbound.persistence.datamodels.CacheEventDataModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedisCache extends CrudRepository<CacheEventDataModel, String> {
    List<CacheEventDataModel> findByAggregateId(String aggregateId);
}
