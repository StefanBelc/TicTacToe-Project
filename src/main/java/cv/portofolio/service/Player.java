package cv.portofolio.service;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
public class Player {

    private final DecisionMaker decisionMaker;

    private final String name;
    private final int id;
    private int lastPosition;
    private int winningCount;
    private int loseCount;
    private int drawCount;
    private int playerSymbol;

    public Player(String name, int playerId) {
        this(name, playerId, new DecisionMaker());
    }

    Player(String name, int playerId, DecisionMaker decisionMaker) {
        this.name = name;
        this.id = playerId;
        this.decisionMaker = decisionMaker;
    }

    public int pickPosition(List<Integer> freePositions) {
        return lastPosition = decisionMaker.pickPosition(freePositions);
    }

    //TODO this is very bad...code smell, try to not use mutable data
    public int assignPlayerSymbol(int currentSymbol) {
        return playerSymbol = currentSymbol + 1;
    }

    public void incrementLoseCount() {
        loseCount++;
    }

    public void incrementDrawCount() {
        drawCount++;
    }

    public void incrementWinningCount() {
        winningCount++;
    }

    public int getPlayerSymbol() {
        return playerSymbol;
    }

    public int getWinningCount() {
        return winningCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getDrawCount() {
        return drawCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
