package cv.portofolio.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class GameGrid {

   private static List<List<Integer>> gameGrid = new ArrayList<>(new ArrayList<>(Arrays.asList(
            new ArrayList<>(List.of(0, 0, 0)),
            new ArrayList<>(List.of(0, 0, 0)),
            new ArrayList<>(List.of(0, 0, 0))
    )));

    public GameGrid() {
    }

    public List<List<Integer>> getGameGrid() {
        return gameGrid;
    }

    public List<List<Integer>> updateGrid(int playerTurn, int location) {
        if (playerTurn < 1 || playerTurn > 2 || location < 0 || location >= 9) {
//            TODO : Throw exception
            System.out.println("No valid playerTurn/location");
            return new ArrayList<>();
        } else {
            int index = 0;
            for (int i = 0; i < gameGrid.size(); i++) {
                for (int j = 0; j < gameGrid.get(i).size(); j++) {
                    if (location == index) {
                        gameGrid.get(i).set(j, playerTurn);
                        return gameGrid;
                    }
                    index++;
                }
            }
//          TODO :  Throw exception
            return gameGrid;
        }
    }

    public void resetGrid() {
        for(int i = 0 ; i< getGameGrid().size(); i++) {
            for (int j = 0; j< getGameGrid().get(i).size(); j++) {
                getGameGrid().get(i).set(j, 0);
            }
        }
    }

    public ArrayList<Integer> flatGrid () {
        ArrayList<Integer> flatGrid = new ArrayList<>(9);
        Stream.of(getGameGrid())
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .forEachOrdered(flatGrid::add);

        return flatGrid;
    }


    @Override
    public String toString() {
        return gameGrid.get(0) + "\n" +
                gameGrid.get(1) + "\n" +
                gameGrid.get(2);
    }
}
