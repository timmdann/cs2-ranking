package pl.example.cs2.player.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.example.cs2.player.dto.AddEarningsRequest;
import pl.example.cs2.player.dto.CreatePlayerRequest;
import pl.example.cs2.player.dto.PlayerResponse;
import pl.example.cs2.player.dto.UpdateStatsRequest;
import pl.example.cs2.player.service.PlayerService;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(request));
    }

    @GetMapping("/{id}")
    public PlayerResponse getPlayer(@PathVariable Long id) {
        return playerService.getPlayer(id);
    }

    @GetMapping
    public List<PlayerResponse> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @PostMapping("/{id}/teams")
    public PlayerResponse joinTeam(@PathVariable Long id, @RequestParam String teamName) {
        return playerService.joinTeam(id, teamName);
    }

    @PostMapping("/{id}/stats")
    public PlayerResponse updateStats(@PathVariable Long id, @RequestBody UpdateStatsRequest request) {
        return playerService.updateStats(id, request.won(), request.newElo(), request.mapName());
    }

    @PostMapping("/{id}/earnings")
    public PlayerResponse addEarnings(@PathVariable Long id, @Valid @RequestBody AddEarningsRequest request) {
        return playerService.addTournamentEarnings(id, request.tournamentName(), request.amount(), request.currency());
    }
}
