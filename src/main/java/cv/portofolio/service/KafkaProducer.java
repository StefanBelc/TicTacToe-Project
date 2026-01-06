package cv.portofolio.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private  KafkaTemplate<String, String> kafkaTemplate;

    private static final String TEST_TOPIC = "test-topic";

    @PostConstruct
    public void send() {
        try {
            kafkaTemplate.send("test-topic", "connection-test-key", "Hello Kafka!");
            System.out.println("✅ Kafka Producer: Message sent to topic " + TEST_TOPIC);
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());;
        }
    }
}
