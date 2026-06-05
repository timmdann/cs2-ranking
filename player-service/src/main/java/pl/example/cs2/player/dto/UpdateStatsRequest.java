package pl.example.cs2.player.dto;

public record UpdateStatsRequest(
        boolean won,
        int newElo,
        String mapName
) {}
