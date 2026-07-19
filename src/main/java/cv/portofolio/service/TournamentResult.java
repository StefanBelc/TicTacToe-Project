package cv.portofolio.service;

public record TournamentResult(Winner firstWinner,
                               Winner secondWinner,
                               Winner thirdWinner,
                               String tournamentId,
                               int totalPlayers,
                               int rounds) {
}
