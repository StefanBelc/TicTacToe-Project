package cv.portofolio.service;

import java.util.List;

public class Player {

    private String name;


    public String getName() {
        return name;
    }

    public Player(String name) {
        this.name = name;
    }

    public int pickPosition(List<Integer> freePositions) {
        DecisionMaker decisionMaker = new DecisionMaker();
        return decisionMaker.pickPosition(freePositions);
    }
}