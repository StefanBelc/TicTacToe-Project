package cv.portofolio.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;
@Component
public class GameState {


    private final GameGrid currentState;

    public GameState() {
        this.currentState = new GameGrid();
    }

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
    }

    public void move(int position, int playerTurn) {
        currentState.updateGrid(playerTurn, position);
    }


    public Boolean hasWon(int currentPlayerSymbol) {
        return anyRow(currentPlayerSymbol) ||
                anyColumn(currentPlayerSymbol) ||
                firstDiagonal(currentPlayerSymbol) ||
                secondDiagonal(currentPlayerSymbol);
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
        for (int i = 2; i > -1; i--) {
            if (currentState.getGameGrid().get(index).get(i) != playerSymbol) {
                return false;
            }
            index++;
        }
        return true;
    }


    public Boolean isDraw(int currentPlayerSymbol) {
        if (availablePositions().size() == 0 && !hasWon(currentPlayerSymbol)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDraw() {
        return availablePositions().isEmpty();
    }

}
