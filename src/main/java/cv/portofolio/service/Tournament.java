package cv.portofolio.service;

import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static cv.portofolio.service.TournamentFormat.ROUND_ROBIN;
import static cv.portofolio.service.TournamentFormat.SINGLE_ELIMINATION;

@Service
public class Tournament {
    private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
    private final GameEngine gameEngine;
    private final PlayerGenerator playerGenerator;

    private int totalMatches;
    private List<GameResult> resultsList = new ArrayList<>();

    public Tournament() {
        this.gameEngine = new GameEngine();
        this.playerGenerator = new PlayerGenerator();
    }

    public TournamentResult startTournament(@Min(3) int numberOfPlayers) {
        if (numberOfPlayers % 2 == 0) {
            return singleEliminationFormat(numberOfPlayers);
        } else {
            return roundRobinFormat(numberOfPlayers);
        }
    }

    private TournamentResult roundRobinFormat(int numberOfPlayers) {
        int tournamentRounds = 0;
        GameResult gameResult;
        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, ROUND_ROBIN);

            for(int i =0; i<pairedPlayers.size(); i++) {
                logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                        pairedPlayers.get(i).player2().getName());
                gameResult = gameEngine.startGame(pairedPlayers.get(i));
                totalMatches++;
                resultsList.add(gameResult);
                if(gameResult.isDraw()) {
                    gameResult = gameEngine.startGame(pairedPlayers.get(i));
                    totalMatches++;
                    resultsList.add(gameResult);
                }
                tournamentRounds++;
            }
        // TODO: PA-25 finish RoundRobin format (points-calculated results)
        //TODO: Check accuracy of top 3 winners
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, ROUND_ROBIN);
    }


    private TournamentResult singleEliminationFormat(int numberOfPlayers) {

        List<Player> generatedPlayers = playerGenerator.generatePlayers(numberOfPlayers);
        int tournamentRounds = 0;
        boolean isPlaying = true;
        GameResult gameResult;
        List<PlayersPair> pairedPlayers = pairPlayers(generatedPlayers, SINGLE_ELIMINATION);
        List<Player> winners = new ArrayList<>();

        while (isPlaying) {
            for (int i = 0; i < pairedPlayers.size(); i++) {
                if (pairedPlayers.get(i).player1() == null) {
                    winners.add(pairedPlayers.get(i).player2());
                    continue;
                } else if (pairedPlayers.get(i).player2() == null) {
                    winners.add(pairedPlayers.get(i).player1());
                    continue;
                } else {
                    logger.info("{} is playing against {}", pairedPlayers.get(i).player1().getName(),
                            pairedPlayers.get(i).player2().getName());

                    gameResult = gameEngine.startGame(pairedPlayers.get(i));
                    totalMatches++;
                    resultsList.add(gameResult);
                    while (gameResult.isDraw()) {
                        gameResult = gameEngine.startGame(pairedPlayers.get(i));
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
        return tournamentResult(resultsList, numberOfPlayers, tournamentRounds, SINGLE_ELIMINATION);
    }

    private List<PlayersPair> pairPlayers(List<Player> players, TournamentFormat tournamentFormat) {
        Collections.shuffle(players);

        List<PlayersPair> pairedPlayers = new ArrayList<>();
        if (tournamentFormat == SINGLE_ELIMINATION) {
            int index = 0;
            int loopCondition = (int) Math.ceil((double) players.size() / 2);
            for (int i = 0; i < loopCondition; i++) {
                if (i == loopCondition - 1 && (players.size() % 2 != 0)) {
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
        // TODO: PA-25 Correct method to return accurate results
        if (tournamentFormat == ROUND_ROBIN) {
            Comparator<Player> gameResultsComparator = Comparator.comparingInt(Player::getWinningCount);
            List<Player> topThreeWinners = gameResults.stream()
                    .filter(s-> !s.isDraw())
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

                if(resultListIterator.hasPrevious()) {
                    finalMatch = resultListIterator.previous();
                    semiFinal = resultListIterator.previous();
                }
                winners.add(finalMatch.getWinner());
                winners.add(finalMatch.getLoser());
                winners.add(semiFinal.getLoser());

            for(Player winner : winners) {
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
