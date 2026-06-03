package pl.example.cs2.ranking.service;

import pl.example.cs2.common.events.MatchFinishedEvent;
import pl.example.cs2.ranking.dto.LeaderboardEntry;

import java.util.List;

public interface RankingService {
    void processMatchFinished(MatchFinishedEvent event);
    void registerPlayer(Long playerId, String username);
    List<LeaderboardEntry> getLeaderboard();
}
