package cv.portofolio.service.persistence;

import com.company.promobridge.TournamentStatus;
import cv.portofolio.service.persistence.entity.TournamentEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentPersistenceService {

    private final TournamentRepository tournamentRepository;

    @Transactional
    public void persistTournamentStartedSnapshot(String tournamentId, int totalPlayers) {
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .tournamentId(tournamentId)
                .tournamentStatus(String.valueOf(TournamentStatus.STARTED))
                .totalMatches(0)
                .totalPlayers(totalPlayers)
                .totalDuration(0)
                .averageMatchDuration(0)
                .updatedAt(LocalDateTime.now())
                .build();

        tournamentRepository.save(tournamentEntity);
        log.info("Tournament started snapshot saved successfully to database at {} with tournamentId {}", Instant.now(), tournamentId);
    }

    @Transactional
    public void persistTournamentFinishedSnapshot(TournamentEntity currentTournamentEntity) {
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
        log.info("Tournament finished snapshot saved successfully to database at {} with tournamentId {}", Instant.now(), currentTournamentEntity.getTournamentId());
    }

    public List<TournamentEntity> findActiveTournaments() {
        return tournamentRepository.findByTournamentStatus(String.valueOf(TournamentStatus.STARTED));
    }

    public Optional<TournamentEntity> findLatestTournament() {
        return tournamentRepository.findFirstByOrderByUpdatedAtDesc();
    }
}
