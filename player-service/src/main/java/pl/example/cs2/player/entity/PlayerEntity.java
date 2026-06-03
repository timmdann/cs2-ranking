package pl.example.cs2.player.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private int eloRating;

    @Column(nullable = false)
    private int matchesPlayed;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    protected PlayerEntity() {}

    public PlayerEntity(String username, int eloRating) {
        this.username = username;
        this.eloRating = eloRating;
        this.matchesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public int getEloRating() { return eloRating; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }

    public void setEloRating(int eloRating) { this.eloRating = eloRating; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
}
