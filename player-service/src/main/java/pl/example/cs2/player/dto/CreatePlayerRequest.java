package pl.example.cs2.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePlayerRequest {

    @NotBlank
    @Size(min = 3, max = 32)
    private String username;

    public CreatePlayerRequest() {}
    public CreatePlayerRequest(String username) { this.username = username; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
