package pl.example.cs2.player.dto;

import java.math.BigDecimal;
import java.util.List;

public record PlayerResponse(
        Long id,
        String username,
        int eloRating,
        int matchesPlayed,
        int wins,
        int losses,
        double winRate,
        List<TeamHistoryResponse> teamHistory,
        List<MapStatResponse> mapStats,
        List<TournamentEarningsResponse> tournamentEarnings,
        BigDecimal totalEarnings
) {}
