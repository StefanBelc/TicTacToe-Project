package cv.portofolio.service;

import java.util.List;
import java.util.Random;

public class DecisionMaker {
    private final Random random = new Random();

    protected int pickPosition(List<Integer> freePositions) {
        int randomIndex = random.nextInt(freePositions.size());
        return freePositions.get(randomIndex);
    }
}
