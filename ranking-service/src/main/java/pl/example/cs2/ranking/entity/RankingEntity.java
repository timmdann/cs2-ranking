package pl.example.cs2.ranking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rankings")
public class RankingEntity {

    @Id
    private Long playerId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private int eloRating;

    @Column(nullable = false)
    private int matchesPlayed;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    protected RankingEntity() {}

    public RankingEntity(Long playerId, String username, int eloRating) {
        this.playerId = playerId;
        this.username = username;
        this.eloRating = eloRating;
        this.matchesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
    }

    public Long getPlayerId() { return playerId; }
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
