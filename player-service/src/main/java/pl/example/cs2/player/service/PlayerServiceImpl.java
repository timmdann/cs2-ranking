package pl.example.cs2.player.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.example.cs2.common.contracts.PlayerDetails;
import pl.example.cs2.common.events.PlayerJoinedTeamEvent;
import pl.example.cs2.player.dto.*;
import pl.example.cs2.player.entity.PlayerEntity;
import pl.example.cs2.player.exception.PlayerAlreadyExistsException;
import pl.example.cs2.player.exception.PlayerNotFoundException;
import pl.example.cs2.player.repository.PlayerRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final int INITIAL_ELO = 1000;

    private final PlayerRepository playerRepository;
    private final RestTemplate restTemplate;
    private final String rankingServiceUrl;
    private final String activityServiceUrl;

    public PlayerServiceImpl(PlayerRepository playerRepository,
                             RestTemplate restTemplate,
                             @Value("${ranking.service.url}") String rankingServiceUrl,
                             @Value("${activity.service.url}") String activityServiceUrl) {
        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
        this.rankingServiceUrl = rankingServiceUrl;
        this.activityServiceUrl = activityServiceUrl;
    }

    @Override
    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        if (playerRepository.existsByUsername(request.getUsername())) {
            throw new PlayerAlreadyExistsException("Player already exists: " + request.getUsername());
        }
        PlayerEntity entity = new PlayerEntity(request.getUsername(), INITIAL_ELO);
        PlayerEntity saved = playerRepository.save(entity);

        try {
            PlayerDetails details = new PlayerDetails(saved.getId(), saved.getUsername(), saved.getEloRating());
            restTemplate.postForEntity(rankingServiceUrl + "/leaderboard/players", details, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to notify ranking-service: " + e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    public PlayerResponse getPlayer(Long id) {
        return toResponse(findById(id));
    }

    @Override
    public List<PlayerResponse> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PlayerResponse updateStats(Long playerId, boolean won, int newElo, String mapName) {
        PlayerEntity player = findById(playerId);
        player.setEloRating(newElo);
        player.setMatchesPlayed(player.getMatchesPlayed() + 1);
        if (won) {
            player.setWins(player.getWins() + 1);
        } else {
            player.setLosses(player.getLosses() + 1);
        }

        if (mapName != null && !mapName.isBlank()) {
            player.updateMapStat(mapName, won);
        }

        return toResponse(playerRepository.save(player));
    }

    @Override
    @Transactional
    public PlayerResponse joinTeam(Long playerId, String teamName) {
        PlayerEntity player = findById(playerId);
        player.addTeam(teamName);
        PlayerEntity saved = playerRepository.save(player);

        try {
            PlayerJoinedTeamEvent event = new PlayerJoinedTeamEvent(
                    UUID.randomUUID().toString(),
                    Instant.now(),
                    saved.getId(),
                    saved.getUsername(),
                    teamName
            );
            restTemplate.postForEntity(activityServiceUrl + "/activities/team-move", event, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to notify activity-service: " + e.getMessage());
        }

        return toResponse(saved);
    }

    @Override
    @Transactional
    public PlayerResponse addTournamentEarnings(Long playerId, String tournamentName, BigDecimal amount, String currency) {
        PlayerEntity player = findById(playerId);
        player.addTournamentEarnings(tournamentName, amount, currency, LocalDateTime.now());
        return toResponse(playerRepository.save(player));
    }

    private PlayerEntity findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + id));
    }

    private PlayerResponse toResponse(PlayerEntity entity) {
        double totalWinRate = entity.getMatchesPlayed() == 0 ? 0.0
                : (double) entity.getWins() / entity.getMatchesPlayed() * 100;

        List<TeamHistoryResponse> history = entity.getTeamHistory().stream()
                .map(h -> new TeamHistoryResponse(h.getTeamName(), h.getJoinedAt(), h.getLeftAt()))
                .toList();

        List<MapStatResponse> mapStats = entity.getMapStats().stream()
                .map(m -> {
                    int total = m.getWins() + m.getLosses();
                    double winRate = total == 0 ? 0.0 : (double) m.getWins() / total * 100;
                    return new MapStatResponse(
                            m.getMapName(),
                            m.getWins(),
                            m.getLosses(),
                            Math.round(winRate * 10.0) / 10.0
                    );
                })
                .toList();

        List<TournamentEarningsResponse> earnings = entity.getTournamentEarnings().stream()
                .map(e -> new TournamentEarningsResponse(e.getTournamentName(), e.getAmount(), e.getCurrency(), e.getDate()))
                .toList();

        BigDecimal totalEarnings = earnings.stream()
                .map(TournamentEarningsResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PlayerResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEloRating(),
                entity.getMatchesPlayed(),
                entity.getWins(),
                entity.getLosses(),
                Math.round(totalWinRate * 10.0) / 10.0,
                history,
                mapStats,
                earnings,
                totalEarnings
        );
    }
}