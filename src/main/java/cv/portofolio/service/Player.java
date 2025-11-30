package cv.portofolio.service;

import java.util.List;

public class Player {

    private final DecisionMaker decisionMaker = new DecisionMaker();

    private final String name;
    private final int id;
    private int lastPosition;
    private int winningCount;


    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public int getLastPosition() {
        return lastPosition;
    }


    public int getWinningCount() {
        return winningCount;
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
}