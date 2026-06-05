package pl.example.cs2.player.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "player_map_stats")
public class MapStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mapName;

    @Column(nullable = false)
    private int wins;

    @Column(nullable = false)
    private int losses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    protected MapStatEntity() {}

    public MapStatEntity(String mapName, PlayerEntity player) {
        this.mapName = mapName;
        this.player = player;
        this.wins = 0;
        this.losses = 0;
    }

    public Long getId() { return id; }
    public String getMapName() { return mapName; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }

    public void addWin() { this.wins++; }
    public void addLoss() { this.losses++; }
}
