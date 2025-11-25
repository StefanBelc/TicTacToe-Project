package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private GameState gameState = new GameState();


    private void initPlayers(List<Player> players) {
        try {
            this.players.addAll(players);
            logger.info("Players initialized");
        } catch (PlayerNotFoundException e) {
            logger.error("Failed to generate players: {} ", e.getMessage());
            players.add(new Player("Tic", 1));
            players.add(new Player("Tac", 2));
        }
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
        gameState.init();
        logger.info("grid has been reset!");
        initPlayers(currentPlayers);
        Player currentPlayer = players.get(currentPlayerIndex);
        boolean hasPlayerMoved = false;
        while (!isGameOver(currentPlayer)) {

            while (!hasPlayerMoved) {
                try {
                    gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
                    logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
                    hasPlayerMoved = true;
                } catch (NoAvailablePositionFoundException e) {
                    logger.error("No available move {}", e.getMessage());
                    return finalResult.getResult();
                } catch (InvalidMoveException e) {
                    logger.error("Invalid move ! {}", e.getMessage());
                }
            }
            if (isGameOver(currentPlayer)) {
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
            hasPlayerMoved = false;

            while (!hasPlayerMoved) {
                try {
                    gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
                    logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
                    hasPlayerMoved = true;
                } catch (NoAvailablePositionFoundException e) {
                    logger.error("No available move {}", e.getMessage());
                    return finalResult.getResult();
                } catch (InvalidMoveException e) {
                    logger.error("Invalid move ! {}", e.getMessage());
                }
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                currentPlayer = players.get(currentPlayerIndex);
            }
        }
        finalResult = result(players, gameState.isDraw(), gameState.getCurrentState().getGameGrid());
        logger.info(String.valueOf(finalResult));
        return finalResult.getResult();
        // TODO: PA-25 Continue tournament logic + player winning score (break when 3 reached)
    }

    private Result result(List<Player> players, boolean isDraw, List<List<Integer>> finalGrid) {
        for (Player player : players) {
            if (gameState.hasWon(player)) {
                return new Result(player, isDraw, finalGrid);
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
