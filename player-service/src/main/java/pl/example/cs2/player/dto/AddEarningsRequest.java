package pl.example.cs2.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record AddEarningsRequest(
        @NotBlank String tournamentName,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currency
) {}
