package pl.example.cs2.player.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TournamentEarningsResponse(
        String tournamentName,
        BigDecimal amount,
        String currency,
        LocalDateTime date
) {}
