package com.wikicoding.inbound.rest;

import com.wikicoding.application.usecases.team.FindAllTeamsEventHandler;
import com.wikicoding.application.usecases.team.FindTeamByIdEventHandler;
import com.wikicoding.common.commands.CreateTeamCommand;
import com.wikicoding.common.dtos.TeamDto;
import com.wikicoding.application.usecases.team.CreateTeamEventHandler;
import com.wikicoding.common.queries.FindAllTeamsQuery;
import com.wikicoding.common.queries.FindTeamByIdQuery;
import com.wikicoding.inbound.rest.dtos.CreateTeamRequest;
import com.wikicoding.inbound.rest.dtos.TeamResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/teams")
@AllArgsConstructor
public class TeamsController {
    private final CreateTeamEventHandler createTeamEventHandler;
    private final FindTeamByIdEventHandler findTeamByIdEventHandler;
    private final FindAllTeamsEventHandler findAllTeamsEventHandler;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@RequestBody CreateTeamRequest request) {
        if (request.getTeamName().trim().isEmpty()) throw new IllegalArgumentException("Name can't be empty");

        CreateTeamCommand createTeamCommand = new CreateTeamCommand(request.getTeamName());

        TeamDto teamDto = createTeamEventHandler.handle(createTeamCommand);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TeamResponse(teamDto.getTeamName(),
                teamDto.getVersion(), teamDto.getCreatedAt()));
    }

    @GetMapping("/{team-name}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable(name = "team-name") String teamName) {
        if (teamName.trim().isEmpty()) throw new IllegalArgumentException("Name can't be empty");

        FindTeamByIdQuery query = new FindTeamByIdQuery(teamName);

        TeamDto teamDto = findTeamByIdEventHandler.handle(query);

        return ResponseEntity.ok(new TeamResponse(teamDto.getTeamName(), teamDto.getVersion(), teamDto.getCreatedAt()));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getTeams() {
        FindAllTeamsQuery query = new FindAllTeamsQuery();

        List<TeamDto> teamDtos = findAllTeamsEventHandler.handle(query);
        List<TeamResponse> teamResponses = new ArrayList<>();
        teamDtos.forEach(teamDto ->
                teamResponses.add(new TeamResponse(teamDto.getTeamName(), teamDto.getVersion(), teamDto.getCreatedAt())));

        return ResponseEntity.ok(teamResponses);
    }
}
