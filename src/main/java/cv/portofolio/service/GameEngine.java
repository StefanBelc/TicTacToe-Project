package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
    private final GameState gameState;
    private GameResult finalGameResult;

    public GameEngine() {
        this.currentPlayerIndex = 0;
        this.gameState = new GameState();
    }

    private void initPlayers(PlayersPair playersPair) {
        this.players.add(playersPair.player1());
        this.players.add(playersPair.player2());
        logger.info("Players initialized");
    }


    private Boolean isGameOver(Player currentPlayer) {
        if (gameState.hasWon(currentPlayer.getPlayerSymbol())) {
            logger.info(currentPlayer.getName() + " is the winner");
            return true;
        } else if (gameState.isDraw(currentPlayer.getPlayerSymbol())) {
            logger.info("DRAW");
            return true;
        } else {
            return false;
        }
    }

    public GameResult startGame(PlayersPair playersPair) {
        players.clear();
        logger.info("player list cleared!");
        gameState.init();
        logger.info("grid has been reset!");
        initPlayers(playersPair);
        currentPlayerIndex = 0;
        Player currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.assignPlayerSymbol(currentPlayerIndex);

        while (!isGameOver(currentPlayer)) {

            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
            if (isGameOver(currentPlayer)) {
                currentPlayer.assignPlayerSymbol(currentPlayerIndex);
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.assignPlayerSymbol(currentPlayerIndex);

            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
            if (isGameOver(currentPlayer)) {
                currentPlayer.assignPlayerSymbol(currentPlayerIndex);
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
            currentPlayer.assignPlayerSymbol(currentPlayerIndex);

        }

        finalGameResult = result(players, gameState.getCurrentState().getGameGrid());
        logger.info(String.valueOf(finalGameResult));
         finalGameResult.updateResult();
         return finalGameResult.getResult();

    }

    private GameResult result(List<Player> players, List<List<Integer>> finalGrid) {
        if (gameState.hasWon(players.get(0).getPlayerSymbol())) {
            return new GameResult(players.get(0),players.get(0), players.get(1), false, finalGrid);
        } else if (gameState.hasWon(players.get(1).getPlayerSymbol())) {
            return new GameResult(players.get(1),players.get(1), players.get(0), false, finalGrid);
        } else {
            return new GameResult(null,players.get(0), players.get(1), true, finalGrid);
        }
    }


//    @Override
//    public String toString() {
//        return "Board: " + "\n" + gameState.getCurrentState().getGameGrid().get(0) +
//                "\n" + gameState.getCurrentState().getGameGrid().get(1) +
//                "\n" + gameState.getCurrentState().getGameGrid().get(2);
//    }
}
