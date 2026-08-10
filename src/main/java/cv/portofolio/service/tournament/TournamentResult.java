package cv.portofolio.service.tournament;

import cv.portofolio.service.game.Winner;

public record TournamentResult(Winner firstWinner,
                               Winner secondWinner,
                               Winner thirdWinner,
                               String tournamentId,
                               int totalPlayers,
                               int rounds) {
}
