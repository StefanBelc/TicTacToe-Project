package cv.portofolio.service;

import com.company.promobridge.GameEvent;
import com.company.promobridge.GameEventProducer;
import com.company.promobridge.GameStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameEventPublisher {

    private GameEventProducer gameEventProducer;

    public GameEvent sendGameCreatedEvent(String gameId,
                                     String tournamentId,
                                     String player1,
                                     String player2) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(GameStatus.CREATED)
                .player1(player1)
                .player2(player2)
                .winner(null)
                .loser(null)
                .draw(false)
                .duration(null)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;

    }


    public GameEvent sendGameStartedEvent(String gameId,
                                     String tournamentId,
                                     String player1,
                                     String player2) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(GameStatus.STARTED)
                .player1(player1)
                .player2(player2)
                .winner(null)
                .loser(null)
                .draw(false)
                .duration(null)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;
    }


    public GameEvent sendGameFinishedEvent(String gameId,
                                      String tournamentId,
                                      String player1,
                                      String player2,
                                      String winner,
                                      String loser,
                                      boolean draw) {

        GameEvent gameEvent = GameEvent
                .builder()
                .gameId(gameId)
                .tournamentId(tournamentId)
                .status(GameStatus.FINISHED)
                .player1(player1)
                .player2(player2)
                .winner(winner)
                .loser(loser)
                .draw(draw)
                .duration(null)
                .build();

        gameEventProducer.produceTournamentEvent(gameEvent);
        return gameEvent;
    }
}
