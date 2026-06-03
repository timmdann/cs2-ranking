package pl.example.cs2.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import pl.example.cs2.player.dto.CreatePlayerRequest;
import pl.example.cs2.player.dto.PlayerResponse;
import pl.example.cs2.player.entity.PlayerEntity;
import pl.example.cs2.player.exception.PlayerAlreadyExistsException;
import pl.example.cs2.player.repository.PlayerRepository;
import pl.example.cs2.player.service.PlayerServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceTest {

    private PlayerRepository playerRepository;
    private PlayerServiceImpl playerService;

    @BeforeEach
    void setUp() {
        playerRepository = Mockito.mock(PlayerRepository.class);
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        playerService = new PlayerServiceImpl(playerRepository, restTemplate, "http://localhost:8083");
    }

    @Test
    void createPlayerShouldReturnPlayerWithInitialElo() {
        CreatePlayerRequest request = new CreatePlayerRequest("s1mple");
        PlayerEntity entity = new PlayerEntity("s1mple", 1000);
        when(playerRepository.existsByUsername("s1mple")).thenReturn(false);
        when(playerRepository.save(any())).thenReturn(entity);

        PlayerResponse response = playerService.createPlayer(request);

        assertEquals("s1mple", response.username());
        assertEquals(1000, response.eloRating());
    }

    @Test
    void createPlayerShouldThrowWhenUsernameAlreadyExists() {
        CreatePlayerRequest request = new CreatePlayerRequest("s1mple");
        when(playerRepository.existsByUsername("s1mple")).thenReturn(true);

        assertThrows(PlayerAlreadyExistsException.class, () -> playerService.createPlayer(request));
        verify(playerRepository, never()).save(any());
    }

    @Test
    void winRateShouldBeZeroForNewPlayer() {
        PlayerEntity entity = new PlayerEntity("niko", 1000);
        when(playerRepository.findById(1L)).thenReturn(Optional.of(entity));

        PlayerResponse response = playerService.getPlayer(1L);

        assertEquals(0.0, response.winRate());
    }
}