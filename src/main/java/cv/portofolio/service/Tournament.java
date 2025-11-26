package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tournament {
    private static final Logger logger = LoggerFactory.getLogger(Tournament.class);
    private final GameEngine gameEngine = new GameEngine();
    private PlayerGenerator playerGenerator = new PlayerGenerator();
    private int totalRounds;
    private int totalMatches;

    public void startTournament() {

        while (totalRounds <= 3) {
            totalRounds = gameEngine.startGame(playerGenerator.generatePlayers(2)).getResult().getWinningScore();
        }
        totalMatches = gameEngine.getMatches();
        logger.info("Total Matches: " + totalMatches);
    }
}
