package pl.example.cs2.ranking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.cs2.common.contracts.PlayerDetails;
import pl.example.cs2.common.events.MatchFinishedEvent;
import pl.example.cs2.ranking.dto.LeaderboardEntry;
import pl.example.cs2.ranking.service.RankingService;

import java.util.List;

@RestController
public class LeaderboardController {

    private final RankingService rankingService;

    public LeaderboardController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        return rankingService.getLeaderboard();
    }

    @PostMapping("/leaderboard/players")
    public ResponseEntity<Void> registerPlayer(@RequestBody PlayerDetails player) {
        rankingService.registerPlayer(player.id(), player.username());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leaderboard/match")
    public ResponseEntity<Void> processMatch(@RequestBody MatchFinishedEvent event) {
        rankingService.processMatchFinished(event);
        return ResponseEntity.ok().build();
    }
}