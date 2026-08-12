package cv.portofolio.service;

import cv.portofolio.service.tournament.TournamentService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "cv.portofolio",
        "com.company.promobridge"
})
public class TicTacToeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicTacToeServiceApplication.class, args);
    }

    @Bean
    public ApplicationRunner recoveryRunner(TournamentService tournamentService) {
        return args -> tournamentService.recoverActiveTournaments();
    }
}
