package pl.example.cs2.match.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "match_winner_players", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "player_id")
    private List<Long> winnerTeamPlayerIds;

    @ElementCollection
    @CollectionTable(name = "match_loser_players", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "player_id")
    private List<Long> loserTeamPlayerIds;

    @Column(nullable = false)
    private Instant playedAt;

    protected MatchEntity() {}

    public MatchEntity(List<Long> winnerTeamPlayerIds, List<Long> loserTeamPlayerIds, Instant playedAt) {
        this.winnerTeamPlayerIds = winnerTeamPlayerIds;
        this.loserTeamPlayerIds = loserTeamPlayerIds;
        this.playedAt = playedAt;
    }

    public Long getId() { return id; }
    public List<Long> getWinnerTeamPlayerIds() { return winnerTeamPlayerIds; }
    public List<Long> getLoserTeamPlayerIds() { return loserTeamPlayerIds; }
    public Instant getPlayedAt() { return playedAt; }
}
