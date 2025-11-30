package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private final GameState gameState = new GameState();


    private void initPlayers(List<Player> players) {
            this.players.addAll(players);
            logger.info("Players initialized");
    }


    private Boolean isGameOver(Player currentPlayer) {
        if (gameState.hasWon(currentPlayer)) {
            logger.info("Player " + currentPlayer.getName() + " is the winner");
            return true;
        } else if (gameState.isDraw()) {
            logger.info("DRAW");
            return true;
        } else {
            return false;
        }
    }

    public Result startGame(List<Player> currentPlayers) {
        players.clear();
        logger.info("player list cleared!");
        gameState.init();
        logger.info("grid has been reset!");
        initPlayers(currentPlayers);
        Player currentPlayer = players.get(currentPlayerIndex);

        while (!isGameOver(currentPlayer)) {

            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
            if (isGameOver(currentPlayer)) {
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);

            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
            if (isGameOver(currentPlayer)) {
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);


        }

        Result finalResult = result(players, gameState.isDraw(), gameState.getCurrentState().getGameGrid());
        logger.info(String.valueOf(finalResult));
        return finalResult.getResult();

    }

    private Result result(List<Player> players, boolean isDraw, List<List<Integer>> finalGrid) {
        for (Player player : players) {
            if (gameState.hasWon(player)) {
                return new Result(player, finalGrid);
            }
        }
        return new Result(null, isDraw, finalGrid);
    }


    @Override
    public String toString() {
        return "Board: " + "\n" + gameState.getCurrentState().getGameGrid().get(0) +
                "\n" + gameState.getCurrentState().getGameGrid().get(1) +
                "\n" + gameState.getCurrentState().getGameGrid().get(2);
    }
}
