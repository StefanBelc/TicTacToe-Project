package cv.portofolio.service.persistence;

import cv.portofolio.service.persistence.entity.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
    List<TournamentEntity> findByTournamentStatus(String tournamentStatus);
    Optional<TournamentEntity> findFirstByOrderByUpdatedAtDesc();
}
