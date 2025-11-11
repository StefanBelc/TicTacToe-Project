package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GameState {


    private GameGrid currentState = new GameGrid();


    public GameGrid getCurrentState() {
        return currentState;
    }

    public List<Integer> availablePositions() {

       return IntStream.range(0,9)
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
        currentState.updateGrid(1,position);
    }

//    tac = 0
    public void tac(int position) {
        currentState.updateGrid(2,position);
    }

    public ArrayList<Integer> getTicFreePositions() {

            ArrayList<Integer> ticFreePositions = new ArrayList<>();
            ticFreePositions.addAll(0,currentState.flatGrid());
            for(int i=0; i<ticFreePositions.size(); i++) {
                if (ticFreePositions.get(i) == 1) {
                    ticFreePositions.set(i,0);
                }
            }
        return ticFreePositions;
    }

    public ArrayList<Integer> getTacFreePositions() {
        ArrayList<Integer> tacFreePositions = new ArrayList<>();
        tacFreePositions.addAll(0,currentState.flatGrid());
        for(int i=0; i<tacFreePositions.size(); i++) {
            if (tacFreePositions.get(i) == 2) {
                tacFreePositions.set(i,0);
            }
        }
        return tacFreePositions;
    }

//    TODO: implement
    public Boolean hasTicWon() {
        return false;
    }
    //    TODO: implement
    public Boolean hasTacWon() {
        return false;
    }
    //    TODO: implement
    public Boolean isDraw() {
        return false;
    }

    @Override
    public String toString() {
        return currentState.getGameGrid().get(0) + "\n" +
                currentState.getGameGrid().get(1) + "\n" +
                currentState.getGameGrid().get(2);
    }
}
