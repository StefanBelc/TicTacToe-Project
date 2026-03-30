package cv.portofolio.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
}
