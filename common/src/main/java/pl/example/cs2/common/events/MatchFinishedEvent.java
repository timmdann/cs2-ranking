package pl.example.cs2.common.events;

import java.time.Instant;
import java.util.List;

public record MatchFinishedEvent(
        String eventId,
        Instant occurredAt,
        Long matchId,
        List<Long> winnerTeamPlayerIds,
        List<Long> loserTeamPlayerIds
) {}
