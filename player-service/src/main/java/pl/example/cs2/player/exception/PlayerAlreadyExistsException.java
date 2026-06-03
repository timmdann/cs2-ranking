package pl.example.cs2.player.exception;

public class PlayerAlreadyExistsException extends RuntimeException {
    public PlayerAlreadyExistsException(String message) { super(message); }
}
