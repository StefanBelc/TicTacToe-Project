package cv.portofolio.service.persistence;

import cv.portofolio.service.persistence.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, String> {
    List<GameEntity> findByTournamentId(String tournamentId);
}
