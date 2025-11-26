package cv.portofolio.service;

import java.util.ArrayList;
import java.util.List;

public class PlayerGenerator {
    public List<Player> generatePlayers(int playersNumber) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Tic", 1));
        players.add(new Player("Tac", 2));
        return players;
    }
}
