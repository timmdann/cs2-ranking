package pl.example.cs2.common.events;

import java.time.Instant;

public record PlayerJoinedTeamEvent(
        String eventId,
        Instant occurredAt,
        Long playerId,
        String username,
        String teamName
) {}
