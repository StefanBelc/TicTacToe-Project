package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class PlayerGenerator {
    public List<Player> generate(int playersNumber) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i <= playersNumber; i++) {
            Player player = new Player();
            players.add(player);
        }
        return players;
    }
}
