package cv.portofolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameEngine {
    private static final Logger logger = LoggerFactory.getLogger(GameEngine.class);
    private final List<Player> players = new ArrayList<>();  //TODO make it local variable, not needed here
    private int currentPlayerIndex; //TODO make it local variable, not needed here
    private final GameState gameState;
    private GameResult finalGameResult; //TODO make it local variable, not needed here

    //TODO use injection, easier to test
    public GameEngine(GameState gameState) {
        this.currentPlayerIndex = 0;
        this.gameState = gameState;
    }

    private void initPlayers(PlayersPair playersPair) { //TODO this method not needed
        this.players.add(playersPair.player1());
        this.players.add(playersPair.player2());
        logger.info("Players initialized");
    }


    private Boolean isGameOver(Player currentPlayer, int symbol) {
        if (gameState.hasWon(symbol)) {
            logger.info(currentPlayer.getName() + " is the winner");
            return true;
        } else if (gameState.isDraw()) {
            logger.info("DRAW");
            return true;
        } else {
            return false;
        }
    }

    //TODO refactor this method, really hard to read
    public GameResult startGame(PlayersPair playersPair) {
        Player player1 = playersPair.player1();
        Player player2 = playersPair.player2();
        boolean gameOver = false;
        while (!gameOver) {
            movePlayer1(player1);
            movePlayer2(player2);
            gameOver = isGameOver(player1, 1) || isGameOver(player2, 2);
        }
        boolean draw = gameState.isDraw();
        boolean player1Won = gameState.hasWon(1);
        boolean player2Won = gameState.hasWon(2);
        GameResult gameResult = GameResult.builder()
                                          .player1(player1)
                                          .player2(player2)
                                          .isDraw(draw)
                                          .player1Won(player1Won)
                                          .player2Won(player2Won)
                                          .build();
        updatePlayerMetrics(player1, player2, player1Won, player2Won);
        return gameResult;
    }

    private void updatePlayerMetrics(Player player1,
                                     Player player2,
                                     boolean player1Won,
                                     boolean player2Won) {
        if(player1Won) {
            player1.incrementWinningCount();
            player2.incrementLoseCount();
        } else if (player2Won) {
            player1.incrementLoseCount();
            player2.incrementWinningCount();
        } else {
            player1.incrementDrawCount();
            player2.incrementDrawCount();
        }
    }

//    public GameResult startGame2(PlayersPair playersPair) {
//        players.clear();
//        logger.info("player list cleared!");
//        gameState.init();
//        logger.info("grid has been reset!");
//        initPlayers(playersPair);
//        currentPlayerIndex = 0;
//        Player currentPlayer = players.get(currentPlayerIndex); //you already receive 2 players as a pair, why you use list? it's harder to work with
//        currentPlayer.assignPlayerSymbol(currentPlayerIndex); //baaad
//
//        while (!isGameOver(currentPlayer)) {
//
//            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
//            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
//            if (isGameOver(currentPlayer)) {
//                currentPlayer.assignPlayerSymbol(currentPlayerIndex);
//                break;
//            }
//            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
//            currentPlayer = players.get(currentPlayerIndex);
//            currentPlayer.assignPlayerSymbol(currentPlayerIndex);
//
//            gameState.move(currentPlayer.pickPosition(gameState.availablePositions()), currentPlayerIndex + 1);
//            logger.info(currentPlayer.getName() + " moved to position " + currentPlayer.getLastPosition());
//            if (isGameOver(currentPlayer)) {
//                currentPlayer.assignPlayerSymbol(currentPlayerIndex);
//                break;
//            }
//            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
//            currentPlayer = players.get(currentPlayerIndex);
//            currentPlayer.assignPlayerSymbol(currentPlayerIndex);
//
//        }
//
//        finalGameResult = result(players.get(0),players.get(1));
//        logger.info(String.valueOf(finalGameResult));
//        return finalGameResult;
//    }

    private void movePlayer1(Player player1) {
        int position = player1.pickPosition(gameState.availablePositions());
        movePlayer(1, position);
    }

    private void movePlayer2(Player player2) {
        int position = player2.pickPosition(gameState.availablePositions());
        movePlayer(2, position);
    }

    private void movePlayer(int symbol, int position) {
        boolean noAvailablePositions = gameState.availablePositions().isEmpty();
        if(noAvailablePositions) {
            logger.info("No available positions, skipping move for player number:  " + symbol);
        } else {
            gameState.move(position, symbol);
            logger.info("Player {} moved to position {}", symbol, position);
        }
    }

    record MoveResult(boolean success,
                      boolean draw,
                      boolean won) {
        public boolean gameOver() {
            return !success || draw || won;
        }
    }

    private GameResult result(Player player1, Player player2) {
        if (gameState.hasWon(player1.getPlayerSymbol())) {
           return GameResult.player1Winner(player1,player2);
        } else if (gameState.hasWon(player2.getPlayerSymbol())) {
            return GameResult.player2Winner(player1,player2);
        } else {
            return GameResult.drawResult(player1,player2);
        }
    }

}
