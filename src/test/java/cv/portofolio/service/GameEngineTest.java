package cv.portofolio.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameEngineTest {

    @Mock
    GameState gameState;

    @Mock
    DecisionMaker decisionMaker1;

    @Mock
    DecisionMaker decisionMaker2;

    @InjectMocks
    GameEngine gameEngine;

    @Test
    void should_start_game_and_end_up_in_a_draw() {
        Player player1 = new Player("p1", 1, decisionMaker1);
        Player player2 = new Player("p2", 2, decisionMaker2);
        PlayersPair playersPair = new PlayersPair(player1, player2);

        when(gameState.availablePositions()).thenReturn(List.of(1));
        when(gameState.hasWon(anyInt())).thenReturn(false);
        when(gameState.isDraw()).thenReturn(true);

        GameResult result = gameEngine.startGame(playersPair);

        assertThat(result.isDraw()).isEqualTo(true);
        assertThat(player1.getDrawCount()).isEqualTo(1);
        assertThat(player2.getDrawCount()).isEqualTo(1);
    }

    @Test
    void should_start_game_and_player1_should_win() {
        Player player1 = new Player("p1", 1, decisionMaker1);
        Player player2 = new Player("p2", 2, decisionMaker2);
        PlayersPair playersPair = new PlayersPair(player1, player2);

        when(gameState.availablePositions()).thenReturn(List.of(1));
        when(gameState.hasWon(1)).thenReturn(true);

        GameResult result = gameEngine.startGame(playersPair);

        assertThat(result.player1Won()).isEqualTo(true);
        assertThat(player1.getWinningCount()).isEqualTo(1);
        assertThat(player2.getLoseCount()).isEqualTo(1);
    }
}
