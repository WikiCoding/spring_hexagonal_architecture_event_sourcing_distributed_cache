package com.wikicoding.application.eventsourcinghandler;

import com.wikicoding.core.domain.abstractions.AggregateRoot;
import com.wikicoding.core.domain.match.Match;
import com.wikicoding.core.domain.match.MatchFactory;
import com.wikicoding.core.domain.team.Team;
import com.wikicoding.core.domain.team.TeamFactory;
import com.wikicoding.core.domain.events.BaseDomainEvent;
import com.wikicoding.core.ports.outbound.EventStoreRepository;

import java.util.*;

public class EventSourcingHandler {
    private final TeamFactory teamFactory;
    private final MatchFactory matchFactory;
    private final EventStoreRepository eventStoreRepository;

    public EventSourcingHandler(TeamFactory teamFactory, MatchFactory matchFactory,
                                EventStoreRepository eventStoreRepository) {
        this.teamFactory = teamFactory;
        this.matchFactory = matchFactory;
        this.eventStoreRepository = eventStoreRepository;
    }

    public AggregateRoot getByAggregateId(String aggregateId, String eventType) {
        List<BaseDomainEvent> events = eventStoreRepository.findByAggregateId(aggregateId, eventType);

        if (eventType.equals("TEAM")) {
            Team team = teamFactory.createNoArgsTeamAggregate();
            team.rebuildState(events);

            return team;
        } else {
            Match match = matchFactory.createNoArgsMatchAggregate();
            match.rebuildState(events);

            return match;
        }
    }

    public void save(AggregateRoot aggregateRoot) {
        if (aggregateRoot instanceof Match match) {
            eventStoreRepository.saveEvents(match.getMatchId(), match.getEvents(), match.getVersion());
            match.markEventsCommited();
            return;
        }

        if (aggregateRoot instanceof Team team) {
            eventStoreRepository.saveEvents(team.getTeamName(), team.getEvents(), team.getVersion());
            team.markEventsCommited();
        }
    }

    public List<AggregateRoot> findAllByEventType(String eventType) {
        // since this is a dummy project instead of creating a custom query I'll grab everything and then remove what we don't want
        List<BaseDomainEvent> events = eventStoreRepository.findAll(eventType);

        Map<String, ArrayList<BaseDomainEvent>> groupedEvents = new HashMap<>();

        events.forEach(event -> {
            if (groupedEvents.containsKey(event.getAggregateId())) {
                groupedEvents.get(event.getAggregateId()).add(event);
            } else {
                ArrayList<BaseDomainEvent> eventList = new ArrayList<>();
                eventList.add(event);
                groupedEvents.put(event.getAggregateId(), eventList);
            }
        });

        Set<String> ids = groupedEvents.keySet();
        List<AggregateRoot> aggregates = new ArrayList<>();

        if (eventType.equals("TEAM")) {
            ids.forEach(id -> {
                Team team = teamFactory.createNoArgsTeamAggregate();
                team.rebuildState(groupedEvents.get(id));
                aggregates.add(team);
            });

            return aggregates;
        }

        ids.forEach(id -> {
            Match match = matchFactory.createNoArgsMatchAggregate();
            match.rebuildState(groupedEvents.get(id));
            aggregates.add(match);
        });

        return aggregates;
    }
}
