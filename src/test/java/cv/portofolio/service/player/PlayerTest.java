package cv.portofolio.service.player;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

    @Test
    void generatorCreatesNumberedPlayers() {
        List<Player> players = new PlayerGenerator().generatePlayers(4);

        assertThat(players).extracting(Player::getName)
                .containsExactly("Player 1", "Player 2", "Player 3", "Player 4");
        assertThat(players).extracting(Player::getId).containsExactly(1, 2, 3, 4);
    }

    @Test
    void pickPositionChoosesAFreePositionAndRemembersIt() {
        Player player = new Player("Alice", 1);
        List<Integer> free = List.of(2, 5, 7);

        for (int i = 0; i < 50; i++) {
            int picked = player.pickPosition(free);
            assertThat(free).contains(picked);
            assertThat(player.getLastPosition()).isEqualTo(picked);
        }
    }

    @Test
    void assignPlayerSymbolIsIndexPlusOne() {
        Player player = new Player("Alice", 1);

        assertThat(player.assignPlayerSymbol(0)).isEqualTo(1);
        assertThat(player.assignPlayerSymbol(1)).isEqualTo(2);
        assertThat(player.getPlayerSymbol()).isEqualTo(2);
    }

    @Test
    void resetCountsZeroesAllCounters() {
        Player player = new Player("Alice", 1);
        player.incrementWinningCount();
        player.incrementLoseCount();
        player.incrementDrawCount();

        player.resetCounts();

        assertThat(player.getWinningCount()).isZero();
        assertThat(player.getLoseCount()).isZero();
        assertThat(player.getDrawCount()).isZero();
    }
}
