package cv.portofolio.service;

import com.company.promobridge.GameEvent;
import com.company.promobridge.TournamentEvent;
import com.company.promobridge.TournamentEventProducer;
import com.company.promobridge.TournamentStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TournamentEventPublisher {

    private final TournamentEventProducer tournamentEventProducer;
    private final Logger logger = LoggerFactory.getLogger(TournamentEventPublisher.class);


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
        logger.info("sent tournament created event");
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
        logger.info("sent tournament started event");
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
        logger.info("sent tournament finished event");
    }
}
