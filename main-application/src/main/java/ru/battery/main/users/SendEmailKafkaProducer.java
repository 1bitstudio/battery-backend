package ru.battery.main.users;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.battery.main.users.dto.SendEmailDto;

@Service
@RequiredArgsConstructor
public class SendEmailKafkaProducer {

    private final KafkaTemplate<String, SendEmailDto> kafkaTemplate;

    public void sendEmailToKafka(SendEmailDto sendEmailDto) {
        kafkaTemplate.send("email", sendEmailDto);
    }
}
