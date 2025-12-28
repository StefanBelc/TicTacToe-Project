package cv.portofolio.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TournamentTest {

    @Test
    void pairPlayers() {
        Tournament tournament = new Tournament();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            players.add(new Player("Player " + (i+1), i));
        }
//        List<PlayersPair> pairedPlayers = tournament.pairPlayers(players, TournamentFormat.ROUND_ROBIN);

//        Assertions.assertEquals(45, pairedPlayers.size());
    }

    @Test
    void tournamentResults() {
        Tournament tournament = new Tournament();
        List<GameResult> gameResultsList = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            players.add(new Player("Player " + (i+1), i));
        }

         List<List<Integer>> gameGrid = List.of(
                new ArrayList<>(List.of(0, 0, 0)),
                new ArrayList<>(List.of(0, 0, 0)),
                new ArrayList<>(List.of(0, 0, 0))
        );
        gameResultsList.add(new GameResult(players.get(0), players.get(0),players.get(1),false, gameGrid));
        gameResultsList.add(new GameResult(players.get(2),players.get(2),players.get(3),false,gameGrid));
        gameResultsList.add(new GameResult(null,players.get(3),players.get(4),true,gameGrid));
        gameResultsList.add(new GameResult(players.get(0), players.get(0), players.get(4), false, gameGrid));
        gameResultsList.add(new GameResult(players.get(0),players.get(0),players.get(7),false, gameGrid));
        gameResultsList.add(new GameResult(players.get(2), players.get(2),players.get(6), false,gameGrid));
        gameResultsList.add(new GameResult(players.get(3),players.get(3),players.get(9),false,gameGrid));

//        TournamentResult tournamentResult = tournament.tournamentResult(gameResultsList,players.size(),9,TournamentFormat.SINGLE_ELIMINATION);
//
//        Assertions.assertEquals(new TournamentResult(
//                new Winner(players.get(0).getName(),
//                        3,
//                        players.get(0).getLoseCount(),
//                        players.get(0).getDrawCount()),
//                new Winner(players.get(2).getName(),
//                        2,
//                        players.get(2).getLoseCount(),
//                        players.get(2).getDrawCount()),
//                new Winner(players.get(3).getName(),
//                        1,
//                        players.get(3).getLoseCount(),
//                        players.get(3).getDrawCount()),
//                10,
//                9), tournamentResult);
    }
}