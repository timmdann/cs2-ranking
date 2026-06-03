package pl.example.cs2.ranking.dto;

public record LeaderboardEntry(
        int position,
        Long playerId,
        String username,
        int eloRating,
        int matchesPlayed,
        int wins,
        int losses,
        double winRate
) {}
