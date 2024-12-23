package com.wikicoding.outbound.persistence.cache;

import com.wikicoding.outbound.persistence.datamodels.TeamDataModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamCache extends CrudRepository<TeamDataModel, String> {
}
