package cv.portofolio.service.tournament;

import com.company.promobridge.GameEvent;
import com.company.promobridge.GameStatus;
import com.company.promobridge.TournamentStatus;
import cv.portofolio.service.game.GameEngine;
import cv.portofolio.service.game.GameResult;
import cv.portofolio.service.game.Winner;
import cv.portofolio.service.game.messaging.GameEventPublisher;
import cv.portofolio.service.infrastructure.DurationStopWatch;
import cv.portofolio.service.persistence.GamePersistenceService;
import cv.portofolio.service.persistence.TournamentPersistenceService;
import cv.portofolio.service.persistence.entity.GameEntity;
import cv.portofolio.service.persistence.entity.TournamentEntity;
import cv.portofolio.service.player.Player;
import cv.portofolio.service.player.PlayerGenerator;
import cv.portofolio.service.player.PlayersPair;
import cv.portofolio.service.tournament.messaging.TournamentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cv.portofolio.service.tournament.TournamentFormat.ROUND_ROBIN;
import static cv.portofolio.service.tournament.TournamentFormat.SINGLE_ELIMINATION;

@RequiredArgsConstructor
@Service
public class TournamentService {
    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final GameEngine gameEngine;
    private final PlayerGenerator playerGenerator;

    private final AtomicInteger totalMatches = new AtomicInteger(0);
    private final List<GameResult> resultsList;
    private final List<GameEvent> gameEvents;
    private final TournamentEventPublisher tournamentEventPublisher;
    private final GameEventPublisher gameEventPublisher;
    private final GamePersistenceService gamePersistenceService;
    private final TournamentPersistenceService tournamentPersistenceService;
    private final DurationStopWatch tournamentStopWatch;
    private final AtomicInteger averageMatchDuration = new AtomicInteger(0);


    public TournamentResult startTournament(int numberOfPlayers) {
        tournamentStopWatch.reset();
        gameEvents.clear();
        resultsList.clear();
        totalMatches.set(0);
        String tournamentId = UUID.randomUUID().toString();
        if (numberOfPlayers % 2 == 0) {
            tournamentStopWatch.start();
            return singleEliminationFormat(numberOfPlayers, tournamentId);
        } else {
            tournamentStopWatch.start();
            return roundRobinFormat(numberOfPlayers, tournamentId);
        }
    }

    private TournamentResult roundRobinFormat(int numberOfPlayers, String tournamentId) {
        totalMatches.incrementAndGet();
        tournamentPersistenceService.persistTournamentStartedSnapshot(tournamentId, numberOfPlayers);
        tournamentEventPublisher.sendTournamentCreatedEvent(tournamentId);
        int tournamentRounds = 0;
        GameResult gameResult;
        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, ROUND_ROBIN);
        tournamentEventPublisher.sendTournamentStartedEvent(tournamentId, numberOfPlayers, totalMatches.get(), gameEvents);


        for (int i = 0; i < pairedPlayers.size(); i++) {
            String gameId = UUID.randomUUID().toString();
            gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
            logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                    pairedPlayers.get(i).player2().getName());
            gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
            gameResult = gameEngine.startGame(pairedPlayers.get(i));
            gamePersistenceService.persistGameSnapshot(buildGameSnapshot(tournamentId, gameId, pairedPlayers, i, gameResult));
            gameEvents.add(sendGameFinishedEvent(gameId, tournamentId, gameResult));
            averageMatchDuration.addAndGet((int) gameResult.matchDuration());
            totalMatches.incrementAndGet();
            resultsList.add(gameResult);

            if (gameResult.isDraw()) {
                gameId = UUID.randomUUID().toString();
                gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
                gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
                gameResult = gameEngine.startGame(pairedPlayers.get(i));
                gamePersistenceService.persistGameSnapshot(buildGameSnapshot(tournamentId, gameId, pairedPlayers, i, gameResult));
                gameEvents.add(sendGameFinishedEvent(gameId, tournamentId, gameResult));
                averageMatchDuration.addAndGet((int) gameResult.matchDuration());
                totalMatches.incrementAndGet();
                resultsList.add(gameResult);
            }
            tournamentRounds++;
        }
        tournamentStopWatch.stop();
        tournamentPersistenceService.persistTournamentFinishedSnapshot(buildTournamentSnapshot(numberOfPlayers, tournamentId));
        tournamentEventPublisher.sendTournamentFinishedEvent(tournamentId, numberOfPlayers, totalMatches.get(), gameEvents);
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, ROUND_ROBIN, tournamentId);
    }

    private TournamentEntity buildTournamentSnapshot(int numberOfPlayers, String tournamentId) {
        return TournamentEntity.builder()
                .tournamentId(tournamentId)
                .tournamentStatus(String.valueOf(TournamentStatus.FINISHED))
                .totalMatches(totalMatches.get())
                .totalPlayers(numberOfPlayers)
                .totalDuration(tournamentStopWatch.getDuration())
                .averageMatchDuration(getAvgMatchDuration())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private long getAvgMatchDuration() {
        long matches = totalMatches.get();
        if (matches == 0) {
            return 0;
        }

        return tournamentStopWatch.getDuration() / matches;
    }

    private GameEntity buildGameSnapshot(String tournamentId, String gameId, List<PlayersPair> pairedPlayers, int currentPairIndex, GameResult gameResult) {
        if (!gameResult.isDraw()) {
            return GameEntity.builder()
                    .gameId(gameId)
                    .tournamentId(tournamentId)
                    .gameStatus(String.valueOf(GameStatus.FINISHED))
                    .player1(pairedPlayers.get(currentPairIndex).player1().getName())
                    .player2(pairedPlayers.get(currentPairIndex).player2().getName())
                    .winner(gameResult.getWinner().getName())
                    .loser(gameResult.getLoser().getName())
                    .isDraw(false)
                    .duration(gameResult.matchDuration())
                    .build();
        } else {
            return GameEntity.builder()
                    .gameId(gameId)
                    .tournamentId(tournamentId)
                    .gameStatus(String.valueOf(GameStatus.FINISHED))
                    .player1(pairedPlayers.get(currentPairIndex).player1().getName())
                    .player2(pairedPlayers.get(currentPairIndex).player2().getName())
                    .winner(null)
                    .loser(null)
                    .isDraw(true)
                    .duration(gameResult.matchDuration())
                    .build();

        }
    }


    private TournamentResult singleEliminationFormat(int numberOfPlayers, String tournamentId) {
        totalMatches.set(0);
        tournamentPersistenceService.persistTournamentStartedSnapshot(tournamentId, numberOfPlayers);
        tournamentEventPublisher.sendTournamentCreatedEvent(tournamentId);

        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        int tournamentRounds = 0;
        boolean isPlaying = true;
        GameResult gameResult;
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, SINGLE_ELIMINATION);
        List<Player> winners = new ArrayList<>();
        tournamentEventPublisher.sendTournamentStartedEvent(tournamentId, numberOfPlayers, totalMatches.get(), gameEvents);

        while (isPlaying) {
            for (int i = 0; i < pairedPlayers.size(); i++) {
                String gameId = UUID.randomUUID().toString();

                if (pairedPlayers.get(i).player1() == null) {
                    winners.add(pairedPlayers.get(i).player2());
                    continue;
                } else if (pairedPlayers.get(i).player2() == null) {
                    winners.add(pairedPlayers.get(i).player1());
                    continue;
                } else {
                    gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
                    logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                            pairedPlayers.get(i).player2().getName());
                    gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
                    gameResult = gameEngine.startGame(pairedPlayers.get(i));
                    gamePersistenceService.persistGameSnapshot(buildGameSnapshot(tournamentId, gameId, pairedPlayers, i, gameResult));
                    gameEvents.add(sendGameFinishedEvent(gameId, tournamentId, gameResult));

                    averageMatchDuration.addAndGet((int) gameResult.matchDuration());
                    totalMatches.incrementAndGet();
                    resultsList.add(gameResult);

                    while (gameResult.isDraw()) {
                        gameId = UUID.randomUUID().toString();
                        gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));
                        gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName()));

                        gameResult = gameEngine.startGame(pairedPlayers.get(i));
                        gamePersistenceService.persistGameSnapshot(buildGameSnapshot(tournamentId, gameId, pairedPlayers, i, gameResult));
                        gameEvents.add(sendGameFinishedEvent(gameId, tournamentId, gameResult));

                        averageMatchDuration.addAndGet((int) gameResult.matchDuration());
                        resultsList.add(gameResult);
                        totalMatches.incrementAndGet();
                    }
                    winners.add(gameResult.getWinner());
                }
                tournamentRounds++;
                if (pairedPlayers.size() < 2) {
                    isPlaying = false;
                    break;
                }
            }
            if (winners.size() > 1) {
                pairedPlayers = pairPlayers(winners, SINGLE_ELIMINATION);
            }
            winners.clear();

        }
        tournamentStopWatch.stop();
        tournamentPersistenceService.persistTournamentFinishedSnapshot(buildTournamentSnapshot(numberOfPlayers, tournamentId));
        logger.info("Total Matches played: {}", totalMatches.get());
        tournamentEventPublisher.sendTournamentFinishedEvent(tournamentId, numberOfPlayers, totalMatches.get(), gameEvents);
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, SINGLE_ELIMINATION, tournamentId);
    }

    private List<PlayersPair> pairPlayers(List<Player> players, TournamentFormat tournamentFormat) {
        Collections.shuffle(players);

        List<PlayersPair> pairedPlayers = new ArrayList<>();
        if (tournamentFormat == SINGLE_ELIMINATION) {
            int index = 0;
            int pairsCount = (int) Math.ceil((double) players.size() / 2);
            for (int i = 0; i < pairsCount; i++) {
                if (i == pairsCount - 1 && (players.size() % 2 != 0)) {
                    pairedPlayers.add(new PlayersPair(players.get(index), null));
                    break;
                } else {
                    pairedPlayers.add(new PlayersPair(players.get(index), players.get(index + 1)));
                    index += 2;
                }
            }
            return pairedPlayers;
        }

        if (tournamentFormat == ROUND_ROBIN) {
            for (int i = 0; i < players.size() - 1; i++) {
                for (int j = i + 1; j < players.size(); j++) {
                    pairedPlayers.add(new PlayersPair(players.get(i), players.get(j)));
                }
            }
            return pairedPlayers;
        } else {
            throw new IllegalArgumentException("Invalid Tournament Format");
        }
    }

    private GameEvent sendGameFinishedEvent(String gameId, String tournamentId, GameResult gameResult) {
        return gameEventPublisher.sendGameFinishedEvent(
                gameId,
                tournamentId,
                gameResult.player1().getName(),
                gameResult.player2().getName(),
                extractPlayerName(gameResult.getWinner()),
                extractPlayerName(gameResult.getLoser()),
                gameResult.isDraw());
    }

    private String extractPlayerName(Player player) {
        return player == null ? null : player.getName();
    }

    private TournamentResult tournamentResult(List<GameResult> gameResults, int totalPlayers,
                                              int totalRounds, TournamentFormat tournamentFormat, String tournamentId) {

        List<Winner> topThreeWinnersList = new ArrayList<>();

        if (tournamentFormat == ROUND_ROBIN) {
            Comparator<Player> gameResultsComparator = Comparator.comparingInt(Player::getWinningCount);
            List<Player> topThreeWinners = gameResults.stream()
                    .flatMap(result -> java.util.stream.Stream.of(result.player1(), result.player2()))
                    .sorted(gameResultsComparator.reversed().
                            thenComparing(Player::getLoseCount)
                            .thenComparing(Player::getDrawCount))
                    .distinct()
                    .limit(3)
                    .toList();

            for (Player topThreeWinner : topThreeWinners) {
                topThreeWinnersList.add(new Winner(topThreeWinner.getName(),
                        topThreeWinner.getWinningCount(),
                        topThreeWinner.getLoseCount(),
                        topThreeWinner.getDrawCount()));
            }

            return new TournamentResult(topThreeWinnersList.get(0),
                    topThreeWinnersList.get(1),
                    topThreeWinnersList.get(2),
                    tournamentId,
                    totalPlayers,
                    totalRounds
            );
        }
        if (tournamentFormat == SINGLE_ELIMINATION) {
            List<GameResult> finishedMatches = gameResults.stream()
                    .filter(result -> !result.isDraw())
                    .toList();
            ListIterator<GameResult> resultListIterator = finishedMatches.listIterator(finishedMatches.size());
            List<Player> winners = new ArrayList<>();
            GameResult finalMatch = null;
            GameResult semiFinal = null;

            // TODO: PA-25 UnitTest

            if (resultListIterator.hasPrevious()) {
                finalMatch = resultListIterator.previous();
                semiFinal = resultListIterator.previous();
            }
            winners.add(finalMatch.getWinner());
            winners.add(finalMatch.getLoser());
            winners.add(semiFinal.getLoser());

            for (Player winner : winners) {
                topThreeWinnersList.add(new Winner(winner.getName(),
                        winner.getWinningCount(),
                        winner.getLoseCount(),
                        winner.getDrawCount()));
            }
            return new TournamentResult(topThreeWinnersList.get(0),
                    topThreeWinnersList.get(1),
                    topThreeWinnersList.get(2),
                    tournamentId,
                    totalPlayers,
                    totalRounds);
        } else {
            throw new IllegalArgumentException("Invalid Tournament Format");
        }
    }

    public void recoverActiveTournaments() {
        List<TournamentEntity> activeTournaments = tournamentPersistenceService.findActiveTournaments();
        if (activeTournaments.isEmpty()) {
            logger.info("No active tournaments found for recovery.");
            return;
        }

        for (TournamentEntity active : activeTournaments) {
            logger.info("Recovering active tournament {} with {} total players", active.getTournamentId(), active.getTotalPlayers());
            gameEvents.clear();
            resultsList.clear();
            List<GameEntity> savedGames = gamePersistenceService.getGamesForTournament(active.getTournamentId());
            logger.info("Found {} completed games in database for active tournament {}", savedGames.size(), active.getTournamentId());

            for (GameEntity savedGame : savedGames) {
                gameEventPublisher.sendGameFinishedEvent(
                        savedGame.getGameId(),
                        savedGame.getTournamentId(),
                        savedGame.getPlayer1(),
                        savedGame.getPlayer2(),
                        savedGame.getWinner(),
                        savedGame.getLoser(),
                        Boolean.TRUE.equals(savedGame.getIsDraw())
                );
            }

            int numberOfPlayers = active.getTotalPlayers();
            if (numberOfPlayers % 2 == 0) {
                singleEliminationFormat(numberOfPlayers, active.getTournamentId());
            } else {
                roundRobinFormat(numberOfPlayers, active.getTournamentId());
            }
        }
    }

    public Map<String, Object> getActiveTournamentState() {
        Optional<TournamentEntity> latestOpt = tournamentPersistenceService.findLatestTournament();
        if (latestOpt.isEmpty()) {
            return Collections.emptyMap();
        }

        TournamentEntity tournament = latestOpt.get();
        List<GameEntity> games = gamePersistenceService.getGamesForTournament(tournament.getTournamentId());

        Map<String, Object> state = new HashMap<>();
        state.put("tournamentId", tournament.getTournamentId());
        state.put("status", tournament.getTournamentStatus());
        state.put("totalPlayers", tournament.getTotalPlayers());
        state.put("totalMatches", games.size());
        state.put("games", games);

        return state;
    }
}
