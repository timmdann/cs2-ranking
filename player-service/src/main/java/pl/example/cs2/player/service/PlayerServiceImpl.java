package pl.example.cs2.player.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.example.cs2.common.contracts.PlayerDetails;
import pl.example.cs2.player.dto.CreatePlayerRequest;
import pl.example.cs2.player.dto.PlayerResponse;
import pl.example.cs2.player.entity.PlayerEntity;
import pl.example.cs2.player.exception.PlayerAlreadyExistsException;
import pl.example.cs2.player.exception.PlayerNotFoundException;
import pl.example.cs2.player.repository.PlayerRepository;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final int INITIAL_ELO = 1000;

    private final PlayerRepository playerRepository;
    private final RestTemplate restTemplate;
    private final String rankingServiceUrl;

    public PlayerServiceImpl(PlayerRepository playerRepository,
                             RestTemplate restTemplate,
                             @Value("${ranking.service.url}") String rankingServiceUrl) {
        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
        this.rankingServiceUrl = rankingServiceUrl;
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
    public PlayerResponse updateStats(Long playerId, boolean won, int newElo) {
        PlayerEntity player = findById(playerId);
        player.setEloRating(newElo);
        player.setMatchesPlayed(player.getMatchesPlayed() + 1);
        if (won) {
            player.setWins(player.getWins() + 1);
        } else {
            player.setLosses(player.getLosses() + 1);
        }
        return toResponse(playerRepository.save(player));
    }

    private PlayerEntity findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found: " + id));
    }

    private PlayerResponse toResponse(PlayerEntity entity) {
        double winRate = entity.getMatchesPlayed() == 0 ? 0.0
                : (double) entity.getWins() / entity.getMatchesPlayed() * 100;
        return new PlayerResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEloRating(),
                entity.getMatchesPlayed(),
                entity.getWins(),
                entity.getLosses(),
                Math.round(winRate * 10.0) / 10.0
        );
    }
}