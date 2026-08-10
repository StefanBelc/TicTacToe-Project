package cv.portofolio.service.game;

import cv.portofolio.service.player.Player;

import lombok.Builder;

@Builder

public record GameResult(Player player1,
                         Player player2,
                         boolean isDraw,
                         boolean player1Won,
                         boolean player2Won,
                         long matchDuration) {


    public boolean isDraw() {
        return this.isDraw;
    }

    public static GameResult player1Winner(Player player1, Player player2, long duration) {
        player1.incrementWinningCount();
        player2.incrementLoseCount();

        return GameResult.builder()
                .player1(player1)
                .player2(player2)
                .isDraw(false)
                .player1Won(true)
                .player2Won(false)
                .matchDuration(duration)
                .build();
    }

    public static GameResult player2Winner(Player player1, Player player2, long duration) {
        player1.incrementLoseCount();
        player2.incrementWinningCount();

        return GameResult.builder()
                .player1(player1)
                .player2(player2)
                .isDraw(false)
                .player1Won(false)
                .player2Won(true)
                .matchDuration(duration)
                .build();
    }

    public static GameResult drawResult(Player player1, Player player2, long duration) {
        player1.incrementDrawCount();
        player2.incrementDrawCount();

        return GameResult.builder()
                .player1(player1)
                .player2(player2)
                .isDraw(true)
                .player1Won(false)
                .player2Won(false)
                .matchDuration(duration)
                .build();
    }

    public Player getWinner() {
        if (player1Won) {
            return player1;
        } else if (player2Won) {
            return player2;
        }
        return null;
    }

    public Player getLoser() {
        if (player1Won) {
            return player2;
        } else if (player2Won) {
            return player1;
        }
        return null;
    }
}
