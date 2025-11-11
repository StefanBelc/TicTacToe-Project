package cv.portofolio.service;

import java.util.List;

public class GameEngine {
    private List<Player> players;
    private int currentPlayerIndex;
    GameState gameState = new GameState();


    public void initPlayers() {
        Player tic = new Player("Tic");
        Player tac = new Player("Tac");
        players.add(tic);
        players.add(tac);
    }


    public Boolean isGameOver() {
        if (gameState.hasTicWon()) {
            return true;
        } else if (gameState.hasTacWon()) {
            return true;
        } else if (gameState.isDraw()) {
            return true;
        } else {
            return false;
        }

    }


    public String startGame() {
        String result = "";
        initPlayers();
        while (!isGameOver()) {
            int ticPosition = players.get(0).pickPosition(gameState.availablePositions());
            gameState.tic(ticPosition);
            int tacPosition = players.get(1).pickPosition(gameState.availablePositions());
            gameState.tac(tacPosition);
//            TODO : finish game logic
        }


        return result;
    }

}
