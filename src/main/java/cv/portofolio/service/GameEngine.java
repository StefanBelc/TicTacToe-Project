package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex;
    GameState gameState = new GameState();


    private void initPlayers() {
        Player tic = new Player("Tic");
        Player tac = new Player("Tac");
        players.add(tic);
        players.add(tac);
    }


    private Boolean isGameOver() {
        if (gameState.hasWon(1) || gameState.hasWon(2)) {
            return true;
        } else if (gameState.isDraw()) {
            return true;
        } else {
            return false;
        }
    }


    public String startGame() {
        initPlayers();

        while (!isGameOver()) {
            int ticPosition = players.get(0).pickPosition(gameState.availablePositions());
            gameState.tic(ticPosition);
            isGameOver();
            int tacPosition = players.get(1).pickPosition(gameState.availablePositions());
            gameState.tac(tacPosition);

        }
        return result();
    }

    private String result() {
        if (gameState.hasWon(1)) {
            return "X has won !" + "\n" + gameState.getCurrentState().getGameGrid().toString();
        } else if (gameState.hasWon(2)) {
            return "0 has won !" + "\n" + gameState.getCurrentState().getGameGrid().toString();
        } else {
            return "DRAW!" + "\n" + gameState.getCurrentState().getGameGrid().toString();
        }
    }

    @Override
    public String toString() {
        return "\n" + gameState.getCurrentState().getGameGrid().get(0) +
                "\n" + gameState.getCurrentState().getGameGrid().get(1) +
                "\n" + gameState.getCurrentState().getGameGrid().get(2);
    }
}
