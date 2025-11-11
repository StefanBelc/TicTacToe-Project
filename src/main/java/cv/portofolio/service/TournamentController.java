package cv.portofolio.service;

import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tournament")
public class TournamentController {
    private Tournament tournament;


    public TournamentController(Tournament tournament) {
        this.tournament = tournament;
    }

    @PostMapping("/start/{numberOfPlayers}")
    public TournamentResult startTournament(@PathVariable @Min(3) int numberOfPlayers) {
        // min numberOfPlayers = 3; min rounds = 1;
       return tournament.startTournament(numberOfPlayers);
    }
}
