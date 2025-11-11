package cv.portofolio.service;

import java.util.List;

public class Player {

    private final DecisionMaker decisionMaker = new DecisionMaker();

    private final String name;
    private final int id;
    private int lastPosition;
    private int winningCount;
    private int loseCount;
    private int drawCount;
    private int playerSymbol;

    public void resetCounts() {
        winningCount = 0;
        loseCount = 0;
        drawCount = 0;
    }


    public void incrementLoseCount() {
        this.loseCount++;
    }

    public void incrementDrawCount() {
        this.drawCount++;
    }

    public void incrementWinningCount() {
        this.winningCount++;
    }


    public Player(String name, int playerId) {
        this.name = name;
        this.id = playerId;
    }

    public int pickPosition(List<Integer> freePositions) {
        return lastPosition = decisionMaker.pickPosition(freePositions);
    }

    public int assignPlayerSymbol(int currentSymbol) {
        return playerSymbol = currentSymbol + 1;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getLastPosition() {
        return this.lastPosition;
    }

    public int getPlayerSymbol() {
        return this.playerSymbol;
    }

    public int getWinningCount() {
        return this.winningCount;
    }

    public int getLoseCount() {
        return this.loseCount;
    }

    public int getDrawCount() {
        return this.drawCount;
    }
}