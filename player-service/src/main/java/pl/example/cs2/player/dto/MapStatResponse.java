package pl.example.cs2.player.dto;

public record MapStatResponse(
        String mapName,
        int wins,
        int losses,
        double winRate
) {}
