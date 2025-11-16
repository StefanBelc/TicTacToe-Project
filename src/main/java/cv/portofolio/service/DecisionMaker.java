package cv.portofolio.service;

import java.util.List;
import java.util.Random;

public class DecisionMaker {


    protected int pickPosition(List<Integer> freePositions) {
        if(freePositions.isEmpty()) {
            return 0;
        }
        int randomIndex = integerGenerator(0, freePositions.size(), freePositions.size());
        return freePositions.get(randomIndex);
    }


    private int integerGenerator(int origin, int bound, int size) {
        Random random = new Random();
        return random.ints(size, origin, bound)
                .findFirst()
                .orElse(0);
    }
}
