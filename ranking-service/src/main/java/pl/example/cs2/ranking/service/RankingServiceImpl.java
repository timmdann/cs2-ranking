package pl.example.cs2.ranking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.example.cs2.common.events.MatchFinishedEvent;
import pl.example.cs2.ranking.dto.LeaderboardEntry;
import pl.example.cs2.ranking.entity.RankingEntity;
import pl.example.cs2.ranking.repository.RankingRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;
    private final EloCalculator eloCalculator;
    private final RestTemplate restTemplate;
    private final String playerServiceUrl;

    public RankingServiceImpl(RankingRepository rankingRepository,
                             EloCalculator eloCalculator,
                             RestTemplate restTemplate,
                             @Value("${player.service.url}") String playerServiceUrl) {
        this.rankingRepository = rankingRepository;
        this.eloCalculator = eloCalculator;
        this.restTemplate = restTemplate;
        this.playerServiceUrl = playerServiceUrl;
    }

    @Override
    @Transactional
    public void processMatchFinished(MatchFinishedEvent event) {
        List<RankingEntity> winners = event.winnerTeamPlayerIds().stream()
                .map(id -> rankingRepository.findById(id).orElse(null))
                .filter(r -> r != null)
                .toList();

        List<RankingEntity> losers = event.loserTeamPlayerIds().stream()
                .map(id -> rankingRepository.findById(id).orElse(null))
                .filter(r -> r != null)
                .toList();

        if (winners.isEmpty() || losers.isEmpty()) return;

        double avgLoserRating = eloCalculator.averageRating(
                losers.stream().map(RankingEntity::getEloRating).toList());
        double avgWinnerRating = eloCalculator.averageRating(
                winners.stream().map(RankingEntity::getEloRating).toList());

        for (RankingEntity winner : winners) {
            int newElo = eloCalculator.calculateNewRating(winner.getEloRating(), avgLoserRating, true);
            winner.setEloRating(newElo);
            winner.setMatchesPlayed(winner.getMatchesPlayed() + 1);
            winner.setWins(winner.getWins() + 1);
            rankingRepository.save(winner);
            notifyPlayerService(winner.getPlayerId(), true, newElo, event.mapName());
        }

        for (RankingEntity loser : losers) {
            int newElo = eloCalculator.calculateNewRating(loser.getEloRating(), avgWinnerRating, false);
            loser.setEloRating(newElo);
            loser.setMatchesPlayed(loser.getMatchesPlayed() + 1);
            loser.setLosses(loser.getLosses() + 1);
            rankingRepository.save(loser);
            notifyPlayerService(loser.getPlayerId(), false, newElo, event.mapName());
        }
    }

    private void notifyPlayerService(Long playerId, boolean won, int newElo, String mapName) {
        try {
            Map<String, Object> request = Map.of(
                    "won", won,
                    "newElo", newElo,
                    "mapName", mapName
            );
            restTemplate.postForEntity(playerServiceUrl + "/players/" + playerId + "/stats", request, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to notify player-service for player " + playerId + ": " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void registerPlayer(Long playerId, String username) {
        if (!rankingRepository.existsById(playerId)) {
            rankingRepository.save(new RankingEntity(playerId, username, 1000));
        }
    }

    @Override
    public List<LeaderboardEntry> getLeaderboard() {
        List<RankingEntity> ranked = rankingRepository.findAllByOrderByEloRatingDesc();
        AtomicInteger position = new AtomicInteger(1);
        return ranked.stream()
                .map(r -> {
                    double winRate = r.getMatchesPlayed() == 0 ? 0.0
                            : (double) r.getWins() / r.getMatchesPlayed() * 100;
                    return new LeaderboardEntry(
                            position.getAndIncrement(),
                            r.getPlayerId(),
                            r.getUsername(),
                            r.getEloRating(),
                            r.getMatchesPlayed(),
                            r.getWins(),
                            r.getLosses(),
                            Math.round(winRate * 10.0) / 10.0
                    );
                })
                .toList();
    }
}
