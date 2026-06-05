package pl.example.cs2.player.service;

import pl.example.cs2.player.dto.CreatePlayerRequest;
import pl.example.cs2.player.dto.PlayerResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PlayerService {
    PlayerResponse createPlayer(CreatePlayerRequest request);
    PlayerResponse getPlayer(Long id);
    List<PlayerResponse> getAllPlayers();
    PlayerResponse updateStats(Long playerId, boolean won, int newElo, String mapName);
    PlayerResponse joinTeam(Long playerId, String teamName);
    PlayerResponse addTournamentEarnings(Long playerId, String tournamentName, BigDecimal amount, String currency);
}
