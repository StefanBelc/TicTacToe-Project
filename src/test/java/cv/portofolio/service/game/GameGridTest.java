package cv.portofolio.service.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameGridTest {

    @Test
    void should_place_symbol_at_flat_index_when_grid_is_updated() {
        GameGrid grid = new GameGrid();

        grid.updateGrid(2, 5); // row 1, column 2

        assertThat(grid.getGameGrid().get(1).get(2)).isEqualTo(2);
        assertThat(grid.flatGrid()).containsExactly(0, 0, 0, 0, 0, 2, 0, 0, 0);
    }

    @Test
    void should_clear_every_cell_when_grid_is_reset() {
        GameGrid grid = new GameGrid();
        grid.updateGrid(1, 0);
        grid.updateGrid(2, 8);

        grid.resetGrid();

        assertThat(grid.flatGrid()).containsOnly(0).hasSize(9);
    }

    @Test
    void should_print_three_rows_when_converted_to_string() {
        GameGrid grid = new GameGrid();
        grid.updateGrid(1, 4);

        assertThat(grid.toString()).isEqualTo("[0, 0, 0]\n[0, 1, 0]\n[0, 0, 0]");
    }
}
