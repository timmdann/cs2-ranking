package pl.example.cs2.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.example.cs2.match.entity.MatchEntity;

public interface MatchRepository extends JpaRepository<MatchEntity, Long> {}
