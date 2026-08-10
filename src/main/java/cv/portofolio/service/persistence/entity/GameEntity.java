package cv.portofolio.service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEntity {

    @Id
    @Column(name = "game_id")
    private String gameId;

    @Column(name = "tournament_id")
    private String tournamentId;

    @Column(name = "status")
    private String gameStatus;

    @Column(name = "player1")
    private String player1;

    @Column(name = "player2")
    private String player2;

    @Column(name = "winner")
    private String winner;

    @Column(name = "loser")
    private String loser;

    @Column(name = "is_draw")
    private Boolean isDraw;

    @Column(name = "duration")
    private Long duration;

}
