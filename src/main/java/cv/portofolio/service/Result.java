package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private int winnerId;
    private Player player;
    private Boolean isDraw;
    private List<List<Integer>> finalGrid;
    private int winningScore = 0;
    private int matchesCount = 0;


    private final StringBuilder formattedGrid = new StringBuilder();

    public Result(Player winner, List<List<Integer>> finalGrid) {
        this.player = winner;
        this.finalGrid = finalGrid;

    }
    public Result(Player winner, boolean isDraw, List<List<Integer>> finalGrid) {
        this.player = winner;
        this.isDraw = isDraw;
        this.finalGrid = finalGrid;
    }

    public boolean isDraw() {
        return this.isDraw;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public int getMatchesCount() {
        return matchesCount;
    }

    public int getWinningScore() {
        return winningScore;

    }

    private void convertFinalGrid() {
        List<String> convertedList = new ArrayList<>();
        for (int i = 0; i < finalGrid.size(); i++) {
            convertedList.add(String.valueOf(finalGrid.get(i)));
        }
        formattedGrid.append("\n" + convertedList.get(0) + "\n" + convertedList.get(1) + "\n" + convertedList.get(2));
    }

    public Result getResult() {
        matchesCount++;
        if (player != null) {
            this.winnerId = player.getId();
            player.incrementWinningCount();
            winningScore = player.getWinningCount();
        }
        return this;
    }

    @Override
    public String toString() {
        convertFinalGrid();
        if (player == null && isDraw) {
            return "The game is a draw! No Player wins!" +
                    "\nFinal Grid:" + formattedGrid;
        } else {
            return "Winner: " + player.getName() +
                    "\nFinal Grid:" + formattedGrid;
        }
    }
}
