package pl.example.cs2.match.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.example.cs2.common.events.MatchFinishedEvent;
import pl.example.cs2.match.dto.CreateMatchRequest;
import pl.example.cs2.match.dto.MatchResponse;
import pl.example.cs2.match.entity.MatchEntity;
import pl.example.cs2.match.event.InMemoryEventBus;
import pl.example.cs2.match.exception.MatchNotFoundException;
import pl.example.cs2.match.repository.MatchRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final InMemoryEventBus eventBus;
    private final Clock clock;
    private final RestTemplate restTemplate;
    private final String rankingServiceUrl;

    public MatchServiceImpl(MatchRepository matchRepository,
                            InMemoryEventBus eventBus,
                            Clock clock,
                            RestTemplate restTemplate,
                            @Value("${ranking.service.url}") String rankingServiceUrl) {
        this.matchRepository = matchRepository;
        this.eventBus = eventBus;
        this.clock = clock;
        this.restTemplate = restTemplate;
        this.rankingServiceUrl = rankingServiceUrl;
    }

    @Override
    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        MatchEntity entity = new MatchEntity(
                request.getWinnerTeamPlayerIds(),
                request.getLoserTeamPlayerIds(),
                Instant.now(clock)
        );
        MatchEntity saved = matchRepository.save(entity);

        MatchFinishedEvent event = new MatchFinishedEvent(
                UUID.randomUUID().toString(),
                Instant.now(clock),
                saved.getId(),
                saved.getWinnerTeamPlayerIds(),
                saved.getLoserTeamPlayerIds()
        );

        eventBus.publish(event);

        try {
            restTemplate.postForEntity(rankingServiceUrl + "/leaderboard/match", event, Void.class);
        } catch (Exception e) {
            // log but don't fail the match creation
            System.err.println("Failed to notify ranking-service: " + e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    public MatchResponse getMatch(Long id) {
        return toResponse(matchRepository.findById(id)
                .orElseThrow(() -> new MatchNotFoundException("Match not found: " + id)));
    }

    @Override
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private MatchResponse toResponse(MatchEntity entity) {
        return new MatchResponse(
                entity.getId(),
                entity.getWinnerTeamPlayerIds(),
                entity.getLoserTeamPlayerIds(),
                entity.getPlayedAt()
        );
    }
}