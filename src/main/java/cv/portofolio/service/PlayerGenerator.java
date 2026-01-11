package cv.portofolio.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerGenerator {
    public List<Player> generatePlayers(int playersNumber) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i < playersNumber +1 ; i++) {
            players.add(new Player("Player " + i, i));
        }
        return players;
    }
}
