package cv.portofolio.service;

import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tournament")
public class TournamentController {
    private TournamentService tournamentService;


    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/start/{numberOfPlayers}")
    public TournamentResult startTournament(@PathVariable @Min(3) int numberOfPlayers) {
        // min numberOfPlayers = 3; min rounds = 1;
       return tournamentService.startTournament(numberOfPlayers);
    }
}
