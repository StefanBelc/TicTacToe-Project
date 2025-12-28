package cv.portofolio.service;

public record TournamentResult(Winner firstWinner,
                               Winner secondWinner,
                               Winner thirdWinner,
                               int totalPlayers,
                               int rounds) {
}
