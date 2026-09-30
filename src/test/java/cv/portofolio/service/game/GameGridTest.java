package cv.portofolio.service.game;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameGridTest {

    @Test
    void updateGridPlacesSymbolAtFlatIndex() {
        GameGrid grid = new GameGrid();

        grid.updateGrid(2, 5); // row 1, column 2

        assertThat(grid.getGameGrid().get(1).get(2)).isEqualTo(2);
        assertThat(grid.flatGrid()).containsExactly(0, 0, 0, 0, 0, 2, 0, 0, 0);
    }

    @Test
    void resetGridClearsEveryCell() {
        GameGrid grid = new GameGrid();
        grid.updateGrid(1, 0);
        grid.updateGrid(2, 8);

        grid.resetGrid();

        assertThat(grid.flatGrid()).containsOnly(0).hasSize(9);
    }

    @Test
    void toStringPrintsThreeRows() {
        GameGrid grid = new GameGrid();
        grid.updateGrid(1, 4);

        assertThat(grid.toString()).isEqualTo("[0, 0, 0]\n[0, 1, 0]\n[0, 0, 0]");
    }
}
