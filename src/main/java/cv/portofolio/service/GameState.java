package cv.portofolio.service;

import java.util.List;
import java.util.stream.IntStream;

public class GameState {


    private GameGrid currentState = new GameGrid();


    public GameGrid getCurrentState() {
        return currentState;
    }

    public List<Integer> availablePositions() {

        return IntStream.range(0, 9)
                .boxed()
                .filter(position -> currentState.flatGrid().get(position) == 0)
                .toList();
    }

    public void init() {
        getCurrentState().resetGrid();
        System.out.println("\nGrid has been reset!");
    }

    //    tic = X
    public void tic(int position) {
        currentState.updateGrid(1, position);
    }

    //    tac = 0
    public void tac(int position) {
        currentState.updateGrid(2, position);
    }


    public Boolean hasWon(int symbol) {
        return anyRow(symbol) ||
                anyColumn(symbol) ||
                firstDiagonal(symbol) ||
                secondDiagonal(symbol);
    }

    private boolean anyRow(int playerSymbol) {

        int playerSymbolCount = 0;
        for (int i = 0; i < currentState.getGameGrid().size(); i++) {
            for (int j = 0; j < currentState.getGameGrid().size(); j++) {
                if (currentState.getGameGrid().get(i).get(j) == playerSymbol) {
                    playerSymbolCount++;
                }
                if (playerSymbolCount == 3) {
                    return true;
                }
            }
            playerSymbolCount = 0;
        }
        return false;
    }

    private boolean anyColumn(int playerSymbol) {
        int playerSymbolCount = 0;
        for (int i = 0; i < currentState.getGameGrid().size(); i++) {
            for (int j = 0; j < currentState.getGameGrid().size(); j++) {
                if (currentState.getGameGrid().get(j).get(i) == playerSymbol) {
                    playerSymbolCount++;
                }
                if (playerSymbolCount == 3) {
                    return true;
                }
            }
            playerSymbolCount = 0;
        }
        return false;
    }

    private boolean firstDiagonal(int playerSymbol) {
        for (int i = 0; i < currentState.getGameGrid().size(); i++) {
            if (currentState.getGameGrid().get(i).get(i) != playerSymbol) {
                return false;
            }
        }
        return true;
    }

    private boolean secondDiagonal(int playerSymbol) {
        int index = 0;
        for (int i = 2; i< currentState.getGameGrid().size(); i--) {
            if (currentState.getGameGrid().get(index).get(i) != playerSymbol) {
                return false;
            }
            index++;
            if(index == 3) {
                break;
            }
        }
        return true;
    }


    public Boolean isDraw() {
        if (availablePositions().size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return currentState.getGameGrid().get(0) + "\n" +
                currentState.getGameGrid().get(1) + "\n" +
                currentState.getGameGrid().get(2);
    }
}
