package cv.portofolio.service.persistence;

import com.company.promobridge.GameStatus;
import cv.portofolio.service.persistence.entity.GameEntity;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Builder
public class GamePersistenceService {

    private final GameRepository gameRepository;


    @Transactional
    public void persistGameSnapshot(GameEntity currentGameEntity) {
        GameEntity gameEntity = GameEntity.builder()
                .gameId(currentGameEntity.getGameId())
                .tournamentId(currentGameEntity.getTournamentId())
                .gameStatus(String.valueOf(GameStatus.FINISHED))
                .player1(currentGameEntity.getPlayer1())
                .player2(currentGameEntity.getPlayer2())
                .winner(currentGameEntity.getWinner())
                .loser(currentGameEntity.getLoser())
                .isDraw(currentGameEntity.getIsDraw())
                .duration(currentGameEntity.getDuration())
                .build();

        gameRepository.save(gameEntity);
        log.info("Game snapshot saved successfully to database at {} with tournamentId {}", Instant.now(), currentGameEntity.getTournamentId());
    }
}
