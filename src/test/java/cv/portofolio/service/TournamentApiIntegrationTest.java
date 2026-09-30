package cv.portofolio.service;

import cv.portofolio.service.persistence.GameRepository;
import cv.portofolio.service.persistence.TournamentRepository;
import cv.portofolio.service.persistence.entity.TournamentEntity;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests: the whole Spring Boot app starts against a real PostgreSQL and a real Kafka broker
 * (both in Docker via Testcontainers) and is called over HTTP.
 */
@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class TournamentApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private GameRepository gameRepository;

    @Test
    void startingATournamentReturnsAPodiumAndPersistsEveryGame() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/tournament/start/5", HttpMethod.POST, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull().containsKeys("firstWinner", "secondWinner", "thirdWinner");
        assertThat(body.get("totalPlayers")).isEqualTo(5);

        String tournamentId = (String) body.get("tournamentId");
        TournamentEntity saved = tournamentRepository.findById(tournamentId).orElseThrow();
        assertThat(saved.getTournamentStatus()).isEqualTo("FINISHED");
        assertThat(saved.getTotalPlayers()).isEqualTo(5);

        // 5 players round robin = 10 pairings, plus one replay per drawn pairing
        assertThat(gameRepository.findByTournamentId(tournamentId)).hasSizeGreaterThanOrEqualTo(10);
    }

    @Test
    void fewerThanThreePlayersIsRejectedWithBadRequest() {
        ResponseEntity<String> response = restTemplate.postForEntity("/tournament/start/2", null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void finishedTournamentEventIsPublishedToKafka() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/tournament/start/4", HttpMethod.POST, null, new ParameterizedTypeReference<>() {});
        String tournamentId = (String) response.getBody().get("tournamentId");

        List<String> events = readTournamentEvents(tournamentId, Duration.ofSeconds(20));

        assertThat(events)
                .as("CREATED, STARTED and FINISHED events for tournament %s", tournamentId)
                .anySatisfy(json -> assertThat(json).contains("\"tournamentStatus\":\"CREATED\""))
                .anySatisfy(json -> assertThat(json).contains("\"tournamentStatus\":\"STARTED\""))
                .anySatisfy(json -> assertThat(json).contains("\"tournamentStatus\":\"FINISHED\""));

        ResponseEntity<Map<String, Object>> active = restTemplate.exchange(
                "/tournament/active", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertThat(active.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(active.getBody()).containsKeys("tournamentId", "status", "games");
    }

    private List<String> readTournamentEvents(String tournamentId, Duration timeout) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "it-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        List<String> matching = new ArrayList<>();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of("tournament.events"));
            Instant deadline = Instant.now().plus(timeout);
            while (Instant.now().isBefore(deadline) && !containsFinished(matching)) {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(500))) {
                    if (tournamentId.equals(record.key())) {
                        matching.add(record.value());
                    }
                }
            }
        }
        return matching;
    }

    private static boolean containsFinished(List<String> events) {
        return events.stream().anyMatch(json -> json.contains("\"tournamentStatus\":\"FINISHED\""));
    }
}
