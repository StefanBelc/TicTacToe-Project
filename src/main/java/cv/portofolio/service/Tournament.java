package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Tournament {
    private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
    private final GameEngine gameEngine = new GameEngine();
    private PlayerGenerator playerGenerator = new PlayerGenerator();

    private int totalMatches;
    private List<Result> resultsList = new ArrayList<>();

    public void startTournament() {

        Result result;
        boolean isPlaying = true;
        while (isPlaying) {
            result = gameEngine.startGame(playerGenerator.generatePlayers(2));

            resultsList.add(result);
            isPlaying = tournamentResult(resultsList);
            totalMatches += result.getMatchesCount();
            logger.info("Total Matches played: {}", totalMatches);

        }
    }

    private boolean tournamentResult(List<Result> results) {
        int ticWinningCount = 0;
        int tacWinningCount = 0;
        for (Result element : results) {
            if (element.getWinnerId() == 1) {
                ticWinningCount++;
            } else if (element.getWinnerId() == 2) {
                tacWinningCount++;
            }
            if (ticWinningCount == 3) {
                logger.info("Tournament winner: {}", "Tic");
                return false;
            }
            if (tacWinningCount == 3) {
                logger.info("Tournament winner: {}", "Tac");
                return false;
            }
        }
        return true;
    }
}
