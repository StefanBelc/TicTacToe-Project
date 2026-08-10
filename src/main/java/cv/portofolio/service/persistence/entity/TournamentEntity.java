package cv.portofolio.service.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "tournaments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentEntity {

    @Id
    @Column(name = "tournament_id")
    private String tournamentId;

    @Column(name = "status")
    private String tournamentStatus;

    @Column(name = "matches")
    private int totalMatches;

    @Column(name = "total_players")
    private int totalPlayers;

    @Column(name = "total_duration_seconds")
    private long totalDuration;

    @Column(name = "avg_match_duration_seconds")
    private long averageMatchDuration;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
