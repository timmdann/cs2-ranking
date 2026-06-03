package pl.example.cs2.ranking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.cs2.ranking.entity.RankingEntity;

import java.util.List;

public interface RankingRepository extends JpaRepository<RankingEntity, Long> {
    List<RankingEntity> findAllByOrderByEloRatingDesc();
}
