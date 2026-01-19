package cv.portofolio.service;

import com.company.promobridge.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static cv.portofolio.service.TournamentFormat.ROUND_ROBIN;
import static cv.portofolio.service.TournamentFormat.SINGLE_ELIMINATION;

@RequiredArgsConstructor
@Service
public class TournamentService {
    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final GameEngine gameEngine;
    private final PlayerGenerator playerGenerator;

    private int totalMatches;
    private final List<GameResult> resultsList;
    private final List<GameEvent> gameEvents;
    private final TournamentEventPublisher tournamentEventPublisher;
    private final GameEventPublisher gameEventPublisher;


    public TournamentResult startTournament(int numberOfPlayers) {
        String tournamentId = UUID.randomUUID().toString();
        if (numberOfPlayers % 2 == 0) {
            return singleEliminationFormat(numberOfPlayers, tournamentId);
        } else {
            return roundRobinFormat(numberOfPlayers, tournamentId);
        }
    }

    private TournamentResult roundRobinFormat(int numberOfPlayers, String tournamentId) {
        TournamentStatus tournamentStatus = TournamentStatus.CREATED;
        tournamentEventPublisher.sendTournamentCreatedEvent(tournamentId, tournamentStatus, 0, 0, null, null, new ArrayList<>());
        int tournamentRounds = 0;
        GameResult gameResult;
        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, ROUND_ROBIN);
        tournamentStatus = TournamentStatus.STARTED;
        tournamentEventPublisher.sendTournamentStartedEvent(tournamentId, tournamentStatus, numberOfPlayers, totalMatches, null, null, gameEvents);

        for (int i = 0; i < pairedPlayers.size(); i++) {
            String gameId = UUID.randomUUID().toString();
            GameStatus gameStatus = GameStatus.CREATED;
            gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));

            logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                    pairedPlayers.get(i).player2().getName());
            gameStatus = GameStatus.STARTED;
            gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));
            gameResult = gameEngine.startGame(pairedPlayers.get(i));
            gameStatus = GameStatus.FINISHED;
            gameEvents.add(gameEventPublisher.sendGameFinishedEvent(gameId, tournamentId, gameStatus, gameResult.player1().getName(), gameResult.player2().getName(), gameResult.getWinner().getName(), gameResult.getLoser().getName(), gameResult.isDraw(), null));
            totalMatches++;
            resultsList.add(gameResult);

            if (gameResult.isDraw()) {
                gameId = UUID.randomUUID().toString();
                gameStatus = GameStatus.CREATED;
                gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));
                gameStatus = GameStatus.STARTED;
                gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));
                gameResult = gameEngine.startGame(pairedPlayers.get(i));
                gameStatus = GameStatus.FINISHED;
                gameEvents.add(gameEventPublisher.sendGameFinishedEvent(gameId, tournamentId, gameStatus, gameResult.player1().getName(), gameResult.player2().getName(), gameResult.getWinner().getName(), gameResult.getLoser().getName(), gameResult.isDraw(), null));
                totalMatches++;
                resultsList.add(gameResult);
            }
            tournamentRounds++;
        }
        tournamentStatus = TournamentStatus.FINISHED;
        tournamentEventPublisher.sendTournamentFinishedEvent(tournamentId, tournamentStatus, numberOfPlayers, totalMatches, null, null, gameEvents);
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, ROUND_ROBIN);
    }


    private TournamentResult singleEliminationFormat(int numberOfPlayers, String tournamentId) {
        TournamentStatus tournamentStatus = TournamentStatus.CREATED;
        tournamentEventPublisher.sendTournamentCreatedEvent(tournamentId, tournamentStatus, 0, 0, null, null, new ArrayList<>());

        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        int tournamentRounds = 0;
        boolean isPlaying = true;
        GameResult gameResult;
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, SINGLE_ELIMINATION);
        List<Player> winners = new ArrayList<>();
        tournamentStatus = TournamentStatus.STARTED;
        tournamentEventPublisher.sendTournamentStartedEvent(tournamentId, tournamentStatus, numberOfPlayers, totalMatches, null, null, gameEvents);

        while (isPlaying) {
            for (int i = 0; i < pairedPlayers.size(); i++) {
                String gameId = UUID.randomUUID().toString();
                GameStatus gameStatus = GameStatus.CREATED;
                gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));

                if (pairedPlayers.get(i).player1() == null) {
                    winners.add(pairedPlayers.get(i).player2());
                    continue;
                } else if (pairedPlayers.get(i).player2() == null) {
                    winners.add(pairedPlayers.get(i).player1());
                    continue;
                } else {
                    logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                            pairedPlayers.get(i).player2().getName());
                    gameStatus = GameStatus.STARTED;
                    gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));
                    gameResult = gameEngine.startGame(pairedPlayers.get(i));
                    gameStatus = GameStatus.FINISHED;
                    gameEvents.add(gameEventPublisher.sendGameFinishedEvent(gameId, tournamentId, gameStatus, gameResult.player1().getName(), gameResult.player2().getName(), gameResult.getWinner().getName(), gameResult.getLoser().getName(), gameResult.isDraw(), null));
                    totalMatches++;
                    resultsList.add(gameResult);
                    while (gameResult.isDraw()) {
                        gameId = UUID.randomUUID().toString();
                        gameStatus = GameStatus.CREATED;
                        gameEvents.add(gameEventPublisher.sendGameCreatedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));
                        gameStatus = GameStatus.STARTED;
                        gameEvents.add(gameEventPublisher.sendGameStartedEvent(gameId, tournamentId, gameStatus, pairedPlayers.get(i).player1().getName(), pairedPlayers.get(i).player2().getName(), null, null, false, null));

                        gameResult = gameEngine.startGame(pairedPlayers.get(i));
                        gameStatus = GameStatus.FINISHED;
                        gameEvents.add(gameEventPublisher.sendGameFinishedEvent(gameId, tournamentId, gameStatus, gameResult.player1().getName(), gameResult.player2().getName(), gameResult.getWinner().getName(), gameResult.getLoser().getName(), gameResult.isDraw(), null));

                        resultsList.add(gameResult);
                        totalMatches++;
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
        logger.info("Total Matches played: {}", totalMatches);
        tournamentStatus = TournamentStatus.FINISHED;
        tournamentEventPublisher.sendTournamentFinishedEvent(tournamentId, tournamentStatus, numberOfPlayers, totalMatches, null, null, gameEvents);
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, SINGLE_ELIMINATION);
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

    private TournamentResult tournamentResult(List<GameResult> gameResults, int totalPlayers,
                                              int totalRounds, TournamentFormat tournamentFormat) {

        List<Winner> topThreeWinnersList = new ArrayList<>();

        if (tournamentFormat == ROUND_ROBIN) {
            Comparator<Player> gameResultsComparator = Comparator.comparingInt(Player::getWinningCount);
            List<Player> topThreeWinners = gameResults.stream()
                    .filter(s -> !s.isDraw())
                    .map(GameResult::getWinner)
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
                    totalPlayers,
                    totalRounds
            );
        }
        if (tournamentFormat == SINGLE_ELIMINATION) {
            ListIterator<GameResult> resultListIterator = gameResults.listIterator(gameResults.size());
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
                    totalPlayers,
                    totalRounds);
        } else {
            throw new IllegalArgumentException("Invalid Tournament Format");
        }
    }
}
