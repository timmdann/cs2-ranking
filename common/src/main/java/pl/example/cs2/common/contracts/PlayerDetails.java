package pl.example.cs2.common.contracts;

public record PlayerDetails(
        Long id,
        String username,
        int eloRating
) {}
