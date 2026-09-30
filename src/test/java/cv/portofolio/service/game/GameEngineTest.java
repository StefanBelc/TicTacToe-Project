package cv.portofolio.service.game;

import cv.portofolio.service.infrastructure.DurationStopWatch;
import cv.portofolio.service.player.Player;
import cv.portofolio.service.player.PlayersPair;
import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Players pick random free positions, so each run plays a different game.
 * The test repeats many games and checks the rules that must always hold.
 */
class GameEngineTest {

    @RepeatedTest(200)
    void should_end_with_exactly_one_consistent_outcome_when_game_is_played() {
        GameState gameState = new GameState();
        GameEngine engine = new GameEngine(gameState, new DurationStopWatch());
        Player player1 = new Player("Player 1", 1);
        Player player2 = new Player("Player 2", 2);

        GameResult result = engine.startGame(new PlayersPair(player1, player2));

        int outcomes = (result.isDraw() ? 1 : 0) + (result.player1Won() ? 1 : 0) + (result.player2Won() ? 1 : 0);
        assertThat(outcomes).as("exactly one outcome").isEqualTo(1);

        int movesPlayed = 9 - gameState.availablePositions().size();
        assertThat(movesPlayed).as("a game needs at least 5 moves").isBetween(5, 9);
        assertThat(result.matchDuration()).isNotNegative();

        if (result.isDraw()) {
            assertThat(gameState.availablePositions()).as("a draw means a full board").isEmpty();
            assertThat(gameState.hasWon(1)).isFalse();
            assertThat(gameState.hasWon(2)).isFalse();
        } else {
            Player winner = result.getWinner();
            assertThat(gameState.hasWon(winner.getPlayerSymbol())).as("the winner owns a line").isTrue();
            assertThat(gameState.hasWon(result.getLoser().getPlayerSymbol())).as("the loser does not").isFalse();
            assertThat(winner.getWinningCount()).isEqualTo(1);
            assertThat(result.getLoser().getLoseCount()).isEqualTo(1);
        }
    }

    @RepeatedTest(20)
    void should_assign_different_symbols_when_game_starts() {
        GameEngine engine = new GameEngine(new GameState(), new DurationStopWatch());
        Player player1 = new Player("Player 1", 1);
        Player player2 = new Player("Player 2", 2);

        engine.startGame(new PlayersPair(player1, player2));

        assertThat(player1.getPlayerSymbol()).isEqualTo(1);
        assertThat(player2.getPlayerSymbol()).isEqualTo(2);
    }
}
