package com.wikicoding.outbound.persistence.cache;

import com.wikicoding.outbound.persistence.datamodels.MatchDataModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchCache extends CrudRepository<MatchDataModel, String> {
}
