package cv.portofolio.service;

import com.company.promobridge.GameEvent;
import com.company.promobridge.TournamentEvent;
import com.company.promobridge.TournamentEventProducer;
import com.company.promobridge.TournamentStatus;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TournamentEventPublisher {

    private final TournamentEventProducer tournamentEventProducer;


    public void sendTournamentCreatedEvent(String tournamentId) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(TournamentStatus.CREATED)
                .totalPlayers(0)
                .matches(0)
                .avgMatchDuration(null)
                .totalDuration(null)
                .gameResults(new ArrayList<>())
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }


    public void sendTournamentStartedEvent(String tournamentId,
                                           int totalPlayers,
                                           int totalMatches,
                                           List<GameEvent> gameEvents) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(TournamentStatus.STARTED)
                .totalPlayers(totalPlayers)
                .matches(totalMatches)
                .avgMatchDuration(null)
                .totalDuration(null)
                .gameResults(gameEvents)
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }


    public void sendTournamentFinishedEvent(String tournamentId,
                                            int totalPlayers,
                                            int totalMatches,
                                            List<GameEvent> gameEvents) {

        TournamentEvent tournamentEvent = TournamentEvent
                .builder()
                .tournamentId(tournamentId)
                .tournamentStatus(TournamentStatus.FINISHED)
                .totalPlayers(totalPlayers)
                .matches(totalMatches)
                .avgMatchDuration(null)
                .totalDuration(null)
                .gameResults(gameEvents)
                .build();

        tournamentEventProducer.produceTournamentEvent(tournamentEvent);
    }
}
