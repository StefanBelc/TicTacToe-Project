package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class GameResult {

    private Player winner = new Player("Winner", 0);
    private Player loser = new Player("Loser",0);
    private Player player1;
    private Player player2;
    private Boolean isDraw;
    private List<List<Integer>> finalGrid;

    private final StringBuilder formattedGrid = new StringBuilder();

    public GameResult(Player winner, Player player1, Player player2, boolean isDraw, List<List<Integer>> finalGrid) {
        if (player1.equals(winner)) {
            this.winner = player1;
            this.loser = player2;
        } else if (player2.equals(winner)) {
            this.winner = player2;
            this.loser = player1;
        }
        this.player1 = player1;
        this.player2 = player2;
        this.isDraw = isDraw;
        this.finalGrid = finalGrid;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getLoser() {
        return loser;
    }

    public boolean isDraw() {
        return this.isDraw;
    }


    private void convertFinalGrid() {
        List<String> convertedList = new ArrayList<>();
        for (int i = 0; i < finalGrid.size(); i++) {
            convertedList.add(String.valueOf(finalGrid.get(i)));
        }
        formattedGrid.append("\n" + convertedList.get(0) + "\n" + convertedList.get(1) + "\n" + convertedList.get(2));
    }

    public GameResult getResult() {
        return this;

    }

    public void updateResult() {

        if (isDraw()) {
            player1.incrementDrawCount();
            player2.incrementDrawCount();
        } else {
            winner.incrementWinningCount();
            loser.incrementLoseCount();
        }
    }

    @Override
    public String toString() {
        convertFinalGrid();
        return formattedGrid.toString();
    }
}
