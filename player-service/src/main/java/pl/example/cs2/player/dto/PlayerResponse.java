package pl.example.cs2.player.dto;

public record PlayerResponse(
        Long id,
        String username,
        int eloRating,
        int matchesPlayed,
        int wins,
        int losses,
        double winRate
) {}
