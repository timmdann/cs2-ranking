package pl.example.cs2.player.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.cs2.player.entity.PlayerEntity;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    Optional<PlayerEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    List<PlayerEntity> findAllByOrderByEloRatingDesc();
}
