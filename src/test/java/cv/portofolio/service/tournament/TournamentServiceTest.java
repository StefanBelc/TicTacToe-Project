package cv.portofolio.service.tournament;

import cv.portofolio.service.game.GameEngine;
import cv.portofolio.service.game.GameState;
import cv.portofolio.service.game.messaging.GameEventPublisher;
import cv.portofolio.service.infrastructure.DurationStopWatch;
import cv.portofolio.service.persistence.GamePersistenceService;
import cv.portofolio.service.persistence.TournamentPersistenceService;
import cv.portofolio.service.persistence.entity.GameEntity;
import cv.portofolio.service.persistence.entity.TournamentEntity;
import cv.portofolio.service.player.PlayerGenerator;
import cv.portofolio.service.tournament.messaging.TournamentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit test: the game engine is real, everything that talks to Kafka or the database is mocked.
 */
@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentEventPublisher tournamentEventPublisher;
    @Mock
    private GameEventPublisher gameEventPublisher;
    @Mock
    private GamePersistenceService gamePersistenceService;
    @Mock
    private TournamentPersistenceService tournamentPersistenceService;

    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        tournamentService = new TournamentService(
                new GameEngine(new GameState(), new DurationStopWatch()),
                new PlayerGenerator(),
                new ArrayList<>(),
                new ArrayList<>(),
                tournamentEventPublisher,
                gameEventPublisher,
                gamePersistenceService,
                tournamentPersistenceService,
                new DurationStopWatch());
    }

    @Test
    void oddNumberOfPlayersPlaysRoundRobin() {
        TournamentResult result = tournamentService.startTournament(3);

        assertThat(result.totalPlayers()).isEqualTo(3);
        assertThat(result.rounds()).as("3 players -> 3 pairings").isEqualTo(3);
        assertThat(Set.of(result.firstWinner().name(), result.secondWinner().name(), result.thirdWinner().name()))
                .as("podium has three different players").hasSize(3);

        verify(tournamentPersistenceService).persistTournamentStartedSnapshot(anyString(), eq(3));
        verify(tournamentEventPublisher).sendTournamentCreatedEvent(anyString());
        verify(tournamentEventPublisher).sendTournamentStartedEvent(anyString(), eq(3), anyInt(), anyList());
        verify(tournamentEventPublisher).sendTournamentFinishedEvent(anyString(), eq(3), anyInt(), anyList());
        verify(gamePersistenceService, atLeast(3)).persistGameSnapshot(org.mockito.ArgumentMatchers.any(GameEntity.class));
        verify(gameEventPublisher, atLeast(3)).sendGameFinishedEvent(
                anyString(), anyString(), anyString(), anyString(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void evenNumberOfPlayersPlaysSingleEliminationAndPersistsFinishedSnapshot() {
        TournamentResult result = tournamentService.startTournament(4);

        assertThat(result.totalPlayers()).isEqualTo(4);
        assertThat(Set.of(result.firstWinner().name(), result.secondWinner().name(), result.thirdWinner().name()))
                .hasSize(3);
        assertThat(result.firstWinner().wins()).as("the champion won at least the semi and the final").isGreaterThanOrEqualTo(2);

        ArgumentCaptor<TournamentEntity> finished = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentPersistenceService).persistTournamentFinishedSnapshot(finished.capture());
        assertThat(finished.getValue().getTournamentStatus()).isEqualTo("FINISHED");
        assertThat(finished.getValue().getTotalPlayers()).isEqualTo(4);
        assertThat(finished.getValue().getTotalMatches()).as("2 semis + final, plus replays of draws").isGreaterThanOrEqualTo(3);

        ArgumentCaptor<GameEntity> games = ArgumentCaptor.forClass(GameEntity.class);
        verify(gamePersistenceService, atLeast(3)).persistGameSnapshot(games.capture());
        assertThat(games.getAllValues())
                .allSatisfy(game -> {
                    assertThat(game.getTournamentId()).isEqualTo(finished.getValue().getTournamentId());
                    assertThat(game.getGameStatus()).isEqualTo("FINISHED");
                });
    }

    @Test
    void recoveryDoesNothingWhenNoTournamentIsActive() {
        when(tournamentPersistenceService.findActiveTournaments()).thenReturn(List.of());

        tournamentService.recoverActiveTournaments();

        verifyNoInteractions(gameEventPublisher, gamePersistenceService, tournamentEventPublisher);
    }

    @Test
    void activeTournamentStateIsEmptyWhenNothingWasPlayed() {
        when(tournamentPersistenceService.findLatestTournament()).thenReturn(Optional.empty());

        assertThat(tournamentService.getActiveTournamentState()).isEmpty();
    }

    @Test
    void activeTournamentStateSummarisesTheLatestTournament() {
        TournamentEntity latest = TournamentEntity.builder()
                .tournamentId("t-1")
                .tournamentStatus("FINISHED")
                .totalPlayers(5)
                .updatedAt(LocalDateTime.now())
                .build();
        List<GameEntity> games = List.of(
                GameEntity.builder().gameId("g-1").tournamentId("t-1").build(),
                GameEntity.builder().gameId("g-2").tournamentId("t-1").build());
        when(tournamentPersistenceService.findLatestTournament()).thenReturn(Optional.of(latest));
        when(gamePersistenceService.getGamesForTournament("t-1")).thenReturn(games);

        Map<String, Object> state = tournamentService.getActiveTournamentState();

        assertThat(state)
                .containsEntry("tournamentId", "t-1")
                .containsEntry("status", "FINISHED")
                .containsEntry("totalPlayers", 5)
                .containsEntry("totalMatches", 2);
        verify(gamePersistenceService, times(1)).getGamesForTournament("t-1");
    }
}
