package cv.portofolio.service;

import com.company.promobridge.GameEvent;
import com.company.promobridge.GameEventProducer;
import com.company.promobridge.GameStatus;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public class GameEventPublisher {

    private GameEventProducer gameEventProducer;

    public GameEvent sendGameCreatedEvent(String gameId,
                                     String tournamentId,
                                     GameStatus status,
                                     String player1,
                                     String player2,
                                     String winner,
                                     String loser,
                                     boolean draw,
                                     Duration duration) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(status)
                .player1(player1)
                .player2(player2)
                .winner(winner)
                .loser(loser)
                .draw(draw)
                .duration(duration)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;

    }


    public GameEvent sendGameStartedEvent(String gameId,
                                     String tournamentId,
                                     GameStatus status,
                                     String player1,
                                     String player2,
                                     String winner,
                                     String loser,
                                     boolean draw,
                                     Duration duration) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(status)
                .player1(player1)
                .player2(player2)
                .winner(winner)
                .loser(loser)
                .draw(draw)
                .duration(duration)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;
    }


    public GameEvent sendGameFinishedEvent(String gameId,
                                      String tournamentId,
                                      GameStatus status,
                                      String player1,
                                      String player2,
                                      String winner,
                                      String loser,
                                      boolean draw,
                                      Duration duration) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(status)
                .player1(player1)
                .player2(player2)
                .winner(winner)
                .loser(loser)
                .draw(draw)
                .duration(duration)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;
    }
}
