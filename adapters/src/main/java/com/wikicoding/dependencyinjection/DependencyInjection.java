package com.wikicoding.dependencyinjection;

import com.wikicoding.application.eventsourcinghandler.EventSourcingHandler;
import com.wikicoding.application.usecases.match.CreateMatchEventHandler;
import com.wikicoding.application.usecases.match.FindMatchByIdEventHandler;
import com.wikicoding.application.usecases.team.CreateTeamEventHandler;
import com.wikicoding.application.usecases.team.FindTeamByIdEventHandler;
import com.wikicoding.application.usecases.match.ResultMatchEventHandler;
import com.wikicoding.core.domain.match.MatchFactory;
import com.wikicoding.core.domain.team.TeamFactory;
import com.wikicoding.core.ports.outbound.EventStoreRepository;
import com.wikicoding.outbound.persistence.cache.RedisCache;
import com.wikicoding.outbound.persistence.database.EventStore;
import com.wikicoding.outbound.persistence.database.StoreImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjection {
    @Bean
    public MatchFactory matchFactory() {
        return new MatchFactory();
    }

    @Bean
    public TeamFactory teamFactory() { return new TeamFactory(); }

    @Bean
    public EventStoreRepository eventStoreRepository(EventStore eventStore, RedisCache redisCache) {
        return new StoreImpl(redisCache, eventStore);
    }

    @Bean
    public EventSourcingHandler eventSourcingHandler(TeamFactory teamFactory, MatchFactory matchFactory,
                                                     EventStoreRepository eventStoreRepository) {
        return new EventSourcingHandler(teamFactory, matchFactory, eventStoreRepository);
    }

    @Bean
    public CreateMatchEventHandler createMatchEventHandler(MatchFactory matchFactory,
                                                           EventSourcingHandler eventSourcingHandler) {
        return new CreateMatchEventHandler(matchFactory, eventSourcingHandler);
    }

    @Bean
    public CreateTeamEventHandler createTeamEventHandler(TeamFactory teamFactory,
                                                         EventSourcingHandler eventSourcingHandler) {
        return new CreateTeamEventHandler(teamFactory, eventSourcingHandler);
    }

    @Bean
    public ResultMatchEventHandler resultMatchEventHandler(EventSourcingHandler eventSourcingHandler) {
        return new ResultMatchEventHandler(eventSourcingHandler);
    }

    @Bean
    public FindTeamByIdEventHandler findTeamByIdEventHandler(EventSourcingHandler eventSourcingHandler) {
        return new FindTeamByIdEventHandler(eventSourcingHandler);
    }

    @Bean
    public FindMatchByIdEventHandler findMatchByIdEventHandler(EventSourcingHandler eventSourcingHandler) {
        return new FindMatchByIdEventHandler(eventSourcingHandler);
    }
}