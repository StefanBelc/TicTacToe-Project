package cv.portofolio.service.persistence;

import cv.portofolio.service.persistence.entity.TournamentEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentPersistenceService {

    private final TournamentRepository tournamentRepository;

    @Transactional
    public void persistTournamentSnapshot(TournamentEntity currentTournamentEntity) {
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .tournamentId(currentTournamentEntity.getTournamentId())
                .tournamentStatus(currentTournamentEntity.getTournamentStatus())
                .totalMatches(currentTournamentEntity.getTotalMatches())
                .totalPlayers(currentTournamentEntity.getTotalPlayers())
                .totalDuration(currentTournamentEntity.getTotalDuration())
                .averageMatchDuration(currentTournamentEntity.getAverageMatchDuration())
                .updatedAt(currentTournamentEntity.getUpdatedAt())
                .build();

        tournamentRepository.save(tournamentEntity);
        log.info("Tournament saved successfully to database at {} with tournamentId {}", Instant.now(), currentTournamentEntity.getTournamentId());
    }
}
