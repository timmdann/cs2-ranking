package pl.example.cs2.player.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamHistoryEntity> teamHistory = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MapStatEntity> mapStats = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentEarningsEntity> tournamentEarnings = new ArrayList<>();

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
    public List<TeamHistoryEntity> getTeamHistory() { return teamHistory; }
    public List<MapStatEntity> getMapStats() { return mapStats; }
    public List<TournamentEarningsEntity> getTournamentEarnings() { return tournamentEarnings; }

    public void setEloRating(int eloRating) { this.eloRating = eloRating; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }

    public void addTeam(String teamName) {
        // Close the previous team history if exists
        if (!teamHistory.isEmpty()) {
            TeamHistoryEntity last = teamHistory.get(teamHistory.size() - 1);
            if (last.getLeftAt() == null) {
                last.setLeftAt(LocalDateTime.now());
            }
        }
        teamHistory.add(new TeamHistoryEntity(teamName, LocalDateTime.now(), this));
    }

    public void updateMapStat(String mapName, boolean won) {
        MapStatEntity stat = mapStats.stream()
                .filter(s -> s.getMapName().equalsIgnoreCase(mapName))
                .findFirst()
                .orElseGet(() -> {
                    MapStatEntity newStat = new MapStatEntity(mapName, this);
                    mapStats.add(newStat);
                    return newStat;
                });

        if (won) {
            stat.addWin();
        } else {
            stat.addLoss();
        }
    }

    public void addTournamentEarnings(String tournamentName, BigDecimal amount, String currency, LocalDateTime date) {
        tournamentEarnings.add(new TournamentEarningsEntity(tournamentName, amount, currency, date, this));
    }
}
