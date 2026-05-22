package ru.battery.file.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.battery.file.dto.MlRequestForRul;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, MlRequestForRul> mlForRulProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = kafkaConfig();

        JsonSerializer<MlRequestForRul> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, MlRequestForRul> mlForRulKafkaTemplate(ProducerFactory<String, MlRequestForRul>
                                                                                producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    private Map<String, Object> kafkaConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        //config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 200 * 1024 * 1024);
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 256L * 1024 * 1024);
        return config;
    }
}
