package pl.example.cs2.player.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_team_history")
public class TeamHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column
    private LocalDateTime leftAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    protected TeamHistoryEntity() {}

    public TeamHistoryEntity(String teamName, LocalDateTime joinedAt, PlayerEntity player) {
        this.teamName = teamName;
        this.joinedAt = joinedAt;
        this.player = player;
    }

    public Long getId() { return id; }
    public String getTeamName() { return teamName; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public LocalDateTime getLeftAt() { return leftAt; }
    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }
}
