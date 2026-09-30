package cv.portofolio.service.game;

import cv.portofolio.service.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameResultTest {

    private Player alice;
    private Player bob;

    @BeforeEach
    void setUp() {
        alice = new Player("Alice", 1);
        bob = new Player("Bob", 2);
    }

    @Test
    void should_update_counts_and_expose_winner_and_loser_when_player1_wins() {
        GameResult result = GameResult.player1Winner(alice, bob, 120);

        assertThat(result.isDraw()).isFalse();
        assertThat(result.getWinner()).isSameAs(alice);
        assertThat(result.getLoser()).isSameAs(bob);
        assertThat(result.matchDuration()).isEqualTo(120);
        assertThat(alice.getWinningCount()).isEqualTo(1);
        assertThat(bob.getLoseCount()).isEqualTo(1);
    }

    @Test
    void should_update_counts_and_expose_winner_and_loser_when_player2_wins() {
        GameResult result = GameResult.player2Winner(alice, bob, 80);

        assertThat(result.getWinner()).isSameAs(bob);
        assertThat(result.getLoser()).isSameAs(alice);
        assertThat(bob.getWinningCount()).isEqualTo(1);
        assertThat(alice.getLoseCount()).isEqualTo(1);
    }

    @Test
    void should_have_no_winner_and_count_draw_for_both_when_game_is_drawn() {
        GameResult result = GameResult.drawResult(alice, bob, 50);

        assertThat(result.isDraw()).isTrue();
        assertThat(result.getWinner()).isNull();
        assertThat(result.getLoser()).isNull();
        assertThat(alice.getDrawCount()).isEqualTo(1);
        assertThat(bob.getDrawCount()).isEqualTo(1);
    }
}
