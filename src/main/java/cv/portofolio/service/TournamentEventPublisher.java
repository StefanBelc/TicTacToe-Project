package cv.portofolio.service;

import com.company.promobridge.GameEvent;
import com.company.promobridge.TournamentEvent;
import com.company.promobridge.TournamentEventProducer;
import com.company.promobridge.TournamentStatus;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class TournamentEventPublisher {

    private final TournamentEventProducer tournamentEventProducer;


    public void sendTournamentStartedEvent(String tournamentId,
                                           TournamentStatus tournamentStatus,
                                           int totalPlayers,
                                           int totalMatches,
                                           Duration avgMatchDuration,
                                           Duration totalDuration,
                                           List<GameEvent> gameEvents) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(tournamentStatus)
                .totalPlayers(totalPlayers)
                .matches(totalMatches)
                .avgMatchDuration(avgMatchDuration)
                .totalDuration(totalDuration)
                .gameResults(gameEvents)
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }


    public void sendTournamentCreatedEvent(String tournamentId,
                                           TournamentStatus tournamentStatus,
                                           int totalPlayers,
                                           int totalMatches,
                                           Duration avgMatchDuration,
                                           Duration totalDuration,
                                           List<GameEvent> gameEvents) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(tournamentStatus)
                .totalPlayers(totalPlayers)
                .matches(totalMatches)
                .avgMatchDuration(avgMatchDuration)
                .totalDuration(totalDuration)
                .gameResults(gameEvents)
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }


    public void sendTournamentFinishedEvent(String tournamentId,
                                            TournamentStatus tournamentStatus,
                                            int totalPlayers,
                                            int totalMatches,
                                            Duration avgMatchDuration,
                                            Duration totalDuration,
                                            List<GameEvent> gameEvents) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(tournamentStatus)
                .totalPlayers(totalPlayers)
                .matches(totalMatches)
                .avgMatchDuration(avgMatchDuration)
                .totalDuration(totalDuration)
                .gameResults(gameEvents)
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }
}
