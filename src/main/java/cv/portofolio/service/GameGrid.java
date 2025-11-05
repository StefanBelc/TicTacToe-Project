package cv.portofolio.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameGrid {

   static List<List<Integer>> gameGrid = new ArrayList<>(new ArrayList<>(Arrays.asList(
            new ArrayList<>(List.of(0, 0, 0)),
            new ArrayList<>(List.of(0, 0, 0)),
            new ArrayList<>(List.of(0, 0, 0))
    )));

    public GameGrid() {
    }

    List<List<Integer>> getGameGrid() {
        return gameGrid;
    }

    public List<List<Integer>> updateGrid(int input, int location) {
        int index = 0;
        if (input < 1 || input > 2 || location < 0 || location >= 9) {
            System.out.println("No valid input/location");
            return new ArrayList<>();
        } else {
            for (int i = 0; i < gameGrid.size(); i++) {
                for (int j = 0; j < gameGrid.get(i).size(); j++) {
                    if (location == index) {
                        gameGrid.get(i).set(j, input);
                        return gameGrid;
                    }
                    index++;
                }
            }
            return gameGrid;
        }
    }


    @Override
    public String toString() {
        return gameGrid.get(0) + "\n" +
                gameGrid.get(1) + "\n" +
                gameGrid.get(2);
    }
}
