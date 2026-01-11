package cv.portofolio.service;

import com.company.promobridge.GameEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class KafkaProducer {


    private final GameEventProducer gameEventProducer;



}
