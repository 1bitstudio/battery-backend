package ru.battery.main.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.battery.main.users.dto.SendEmailDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, SendEmailDto> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        //configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");

        JsonSerializer<SendEmailDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, SendEmailDto> kafkaTemplate(ProducerFactory<String, SendEmailDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
