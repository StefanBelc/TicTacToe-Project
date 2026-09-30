package cv.portofolio.service.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("a fresh board has all nine positions free and no winner")
    void freshBoard() {
        assertThat(gameState.availablePositions()).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
        assertThat(gameState.hasWon(PLAYER_TWO)).isFalse();
        assertThat(gameState.isDraw(PLAYER_ONE)).isFalse();
    }

    @ParameterizedTest(name = "positions {0},{1},{2} win")
    @CsvSource({
            "0,1,2", "3,4,5", "6,7,8",   // rows
            "0,3,6", "1,4,7", "2,5,8",   // columns
            "0,4,8", "2,4,6"             // diagonals
    })
    @DisplayName("three in a line is a win for that player only")
    void winningLines(int a, int b, int c) {
        gameState.move(a, PLAYER_ONE);
        gameState.move(b, PLAYER_ONE);
        gameState.move(c, PLAYER_ONE);

        assertThat(gameState.hasWon(PLAYER_ONE)).isTrue();
        assertThat(gameState.hasWon(PLAYER_TWO)).isFalse();
        assertThat(gameState.isDraw(PLAYER_ONE)).isFalse();
    }

    @Test
    @DisplayName("two in a line is not a win")
    void twoInALineIsNotAWin() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(1, PLAYER_ONE);
        gameState.move(2, PLAYER_TWO);

        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
    }

    @Test
    @DisplayName("a full board with no line is a draw")
    void fullBoardWithoutWinnerIsDraw() {
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
    @DisplayName("taken positions are no longer available")
    void takenPositionsAreRemoved() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(4, PLAYER_TWO);

        assertThat(gameState.availablePositions())
                .hasSize(7)
                .doesNotContain(0, 4);
    }

    @Test
    @DisplayName("init clears the board")
    void initResetsBoard() {
        gameState.move(0, PLAYER_ONE);
        gameState.move(1, PLAYER_ONE);
        gameState.move(2, PLAYER_ONE);

        gameState.init();

        assertThat(gameState.availablePositions()).hasSize(9);
        assertThat(gameState.hasWon(PLAYER_ONE)).isFalse();
    }
}
