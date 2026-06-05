package pl.example.cs2.player.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_tournament_earnings")
public class TournamentEarningsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tournamentName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    protected TournamentEarningsEntity() {}

    public TournamentEarningsEntity(String tournamentName, BigDecimal amount, String currency, LocalDateTime date, PlayerEntity player) {
        this.tournamentName = tournamentName;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.player = player;
    }

    public Long getId() { return id; }
    public String getTournamentName() { return tournamentName; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDateTime getDate() { return date; }
}
