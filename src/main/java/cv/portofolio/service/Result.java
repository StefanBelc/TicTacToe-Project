package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private Player player;
    private Boolean isDraw;
    private List<List<Integer>> finalGrid;

   private final StringBuilder formattedGrid = new StringBuilder();
    public Result(Player winner, boolean isDraw, List<List<Integer>> finalGrid) {
        this.player = winner;
        this.isDraw = isDraw;
        this.finalGrid = finalGrid;
    }

    private void convertFinalGrid() {
        List<String> convertedList = new ArrayList<>();
        for (int i = 0; i < finalGrid.size(); i++) {
            convertedList.add(String.valueOf(finalGrid.get(i)));
        }
        formattedGrid.append("\n" + convertedList.get(0) +"\n" + convertedList.get(1) + "\n" + convertedList.get(2));
    }


    @Override
    public String toString() {
        convertFinalGrid();
        if (player == null && isDraw) {
            return "The game is a draw! No Player wins!";
        } else {
            return "Winner: " + player.getName() +
                    "\nFinal Grid:" + formattedGrid;
        }
    }
}
