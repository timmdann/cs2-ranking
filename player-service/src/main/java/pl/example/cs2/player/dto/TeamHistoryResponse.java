package pl.example.cs2.player.dto;

import java.time.LocalDateTime;

public record TeamHistoryResponse(
        String teamName,
        LocalDateTime joinedAt,
        LocalDateTime leftAt
) {}
