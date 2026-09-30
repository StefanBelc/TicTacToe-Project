package cv.portofolio.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GameStateTest {

    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;

    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
    }

    @Test
    void should_have_all_positions_free_and_no_winner_when_board_is_new() {
        assertThat(gameState.availablePositions()).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
        assertThat(gameState.hasWon(PLAYER_TWO)).isFalse();
        assertThat(gameState.isDraw(PLAYER_ONE)).isFalse();
    }

    @ParameterizedTest(name = "should_win_with_positions_{0}_{1}_{2}")
    @CsvSource({
            "0,1,2", "3,4,5", "6,7,8",   // rows
            "0,3,6", "1,4,7", "2,5,8",   // columns
            "0,4,8", "2,4,6"             // diagonals
    })
    void should_declare_win_only_for_that_player_when_three_in_a_line(int a, int b, int c) {
        gameState.move(a, PLAYER_ONE);
        gameState.move(b, PLAYER_ONE);
        gameState.move(c, PLAYER_ONE);

        assertThat(gameState.hasWon(PLAYER_ONE)).isTrue();
        assertThat(gameState.hasWon(PLAYER_TWO)).isFalse();
        assertThat(gameState.isDraw(PLAYER_ONE)).isFalse();
    }

    @Test
    void should_not_declare_win_when_only_two_in_a_line() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(1, PLAYER_ONE);
        gameState.move(2, PLAYER_TWO);

        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
    }

    @Test
    void should_declare_draw_when_board_is_full_without_a_line() {
        // 1 2 1
        // 1 2 2
        // 2 1 1
        int[] board = {1, 2, 1, 1, 2, 2, 2, 1, 1};
        for (int position = 0; position < board.length; position++) {
            gameState.move(position, board[position]);
        }

        assertThat(gameState.availablePositions()).isEmpty();
        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
        assertThat(gameState.hasWon(PLAYER_TWO)).isFalse();
        assertThat(gameState.isDraw(PLAYER_ONE)).isTrue();
        assertThat(gameState.isDraw(PLAYER_TWO)).isTrue();
    }

    @Test
    void should_remove_position_from_available_when_it_is_taken() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(4, PLAYER_TWO);

        assertThat(gameState.availablePositions())
                .hasSize(7)
                .doesNotContain(0, 4);
    }

    @Test
    void should_clear_board_when_init_is_called() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(1, PLAYER_ONE);
        gameState.move(2, PLAYER_ONE);

        gameState.init();

        assertThat(gameState.availablePositions()).hasSize(9);
        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
    }
}
