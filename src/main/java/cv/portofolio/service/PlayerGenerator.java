package cv.portofolio.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class PlayerGenerator {
    // minimum tournament players is 3
    public List<Player> generatePlayers(int playersNumber) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i < playersNumber +1 ; i++) {
            players.add(new Player("Player " + i, i));
        }
        return players;
    }
}
