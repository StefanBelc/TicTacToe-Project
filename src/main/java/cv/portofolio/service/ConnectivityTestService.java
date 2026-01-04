package cv.portofolio.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConnectivityTestService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TEST_TOPIC = "test-topic";

    @PostConstruct
    public void startConnectivity() {
        System.out.println("Starting Connectivity Verification ------");
        startRedis();
        startKafka();
        System.out.println("Connectivity Verification Complete ------");
    }

    private void startRedis() {
        try {
            // Test Redis: Set a value and get it back
            stringRedisTemplate.opsForValue().set("app:status", "connected");
            String status = stringRedisTemplate.opsForValue().get("app:status");
            if ("connected".equals(status)) {
                System.out.println("✅ Redis Connection: SUCCESS");
            }
        } catch (Exception e) {
            System.err.println("❌ Redis Connection: FAILED - " + e.getMessage());
        }
    }

    private void startKafka() {
        try {
            // Test Kafka: Send a simple message
            kafkaTemplate.send(TEST_TOPIC, "connection-test-key", "Hello Kafka!");
            System.out.println("✅ Kafka Producer: Message sent to topic " + TEST_TOPIC);
            // We'd need a consumer method with @KafkaListener to verify message receipt,
            // but this confirms the producer connection is active.
        } catch (Exception e) {
            System.err.println("❌ Kafka Producer: FAILED - " + e.getMessage());
        }
    }

    @KafkaListener(topics = TEST_TOPIC, groupId = "my-local-dev-group")
    public void listen(String message) {
        System.out.println("✅ Kafka Consumer: Received message: " + message);
    }
}

