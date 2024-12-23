package com.wikicoding.inbound.rest;

import com.wikicoding.application.usecases.match.FindMatchByIdEventHandler;
import com.wikicoding.common.commands.CreateMatchCommand;
import com.wikicoding.common.commands.ResultMatchCommand;
import com.wikicoding.common.dtos.MatchDto;
import com.wikicoding.application.usecases.match.CreateMatchEventHandler;
import com.wikicoding.application.usecases.match.ResultMatchEventHandler;
import com.wikicoding.common.queries.FindMatchByIdQuery;
import com.wikicoding.inbound.rest.dtos.CreateMatchRequest;
import com.wikicoding.inbound.rest.dtos.MatchResponse;
import com.wikicoding.inbound.rest.dtos.ResultMatchRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/matches")
@AllArgsConstructor
@Slf4j
public class MatchesController {
    private final CreateMatchEventHandler createMatchEventHandler;
    private final ResultMatchEventHandler resultMatchEventHandler;
    private final FindMatchByIdEventHandler findMatchByIdEventHandler;
    private final Logger logger = LoggerFactory.getLogger(MatchesController.class);

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@RequestBody CreateMatchRequest request) {
        validateCreateMatchRequestBody(request);

        CreateMatchCommand command = new CreateMatchCommand(request.getMatchName(), request.getTeamA(), request.getTeamB());

        MatchDto matchDto = createMatchEventHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MatchResponse(matchDto.getMatchId(), matchDto.getMatchName(),
                matchDto.getTeamA(), matchDto.getTeamB(), matchDto.getMatchScore(), matchDto.getCreatedAt(), matchDto.getVersion()));
    }

    @PutMapping("/{match-id}")
    public ResponseEntity<MatchResponse> resultMatch(@PathVariable(name = "match-id") String matchId,
                                                     @RequestBody ResultMatchRequest request) {
        validateResultMatchRequest(matchId, request.getMatchScore());

        ResultMatchCommand command = new ResultMatchCommand(matchId, request.getMatchScore());

        MatchDto matchDto = resultMatchEventHandler.handle(command);

        return ResponseEntity.status(HttpStatus.OK).body(new MatchResponse(matchDto.getMatchId(), matchDto.getMatchName(),
                matchDto.getTeamA(), matchDto.getTeamB(), matchDto.getMatchScore(), matchDto.getCreatedAt(), matchDto.getVersion()));
    }

    @GetMapping("/{match-id}")
    public ResponseEntity<MatchResponse> findMatchById(@PathVariable(name = "match-id") String matchId) {
        FindMatchByIdQuery findMatchByIdQuery = new FindMatchByIdQuery(matchId);

        MatchDto matchDto = findMatchByIdEventHandler.handle(findMatchByIdQuery);

        MatchResponse matchResponse = new MatchResponse(matchDto.getMatchId(), matchDto.getMatchName(), matchDto.getTeamA(),
                matchDto.getTeamB(), matchDto.getMatchScore(), matchDto.getCreatedAt(), matchDto.getVersion());

        return ResponseEntity.ok(matchResponse);
    }

    private void validateResultMatchRequest(String matchId, String matchScore) {
        if (matchId.trim().isEmpty()) {
            logger.error("matchesController: Validation error - Match id can't be empty");
            throw new IllegalArgumentException("Match id can't be empty");
        }
        if (matchScore.trim().isEmpty()) {
            logger.error("matchesController: Validation error - Match score can't be empty");
            throw new IllegalArgumentException("Match score can't be empty");
        }
    }

    private void validateCreateMatchRequestBody(CreateMatchRequest request) {
        if (request.getMatchName().trim().isEmpty()) {
            logger.error("matchesController: Validation error - Match name can't be empty");
            throw new IllegalArgumentException("Match name can't be empty");
        }
        if (request.getTeamA().trim().isEmpty()) {
            logger.error("matchesController: Validation error - Team A can't be empty");
            throw new IllegalArgumentException("Team A can't be empty");
        }
        if (request.getTeamB().trim().isEmpty()) {
            logger.error("matchesController: Validation error - Team B can't be empty");
            throw new IllegalArgumentException("Team B can't be empty");
        }
    }
}
