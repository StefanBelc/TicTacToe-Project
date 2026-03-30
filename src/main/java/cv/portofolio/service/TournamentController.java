package cv.portofolio.service;

import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
    private TournamentService tournamentService;


    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }
    private final Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @PostMapping("/start/{numberOfPlayers}")
    public TournamentResult startTournament(@PathVariable @Min(3) int numberOfPlayers) {
        logger.info("Start tournament with {} players", numberOfPlayers);
        // min numberOfPlayers = 3; min rounds = 1;
       return tournamentService.startTournament(numberOfPlayers);
    }
}
