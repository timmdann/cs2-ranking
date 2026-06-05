package pl.example.cs2.match.dto;

import java.time.Instant;
import java.util.List;

public record MatchResponse(
        Long id,
        List<Long> winnerTeamPlayerIds,
        List<Long> loserTeamPlayerIds,
        Instant playedAt,
        String mapName
) {}
