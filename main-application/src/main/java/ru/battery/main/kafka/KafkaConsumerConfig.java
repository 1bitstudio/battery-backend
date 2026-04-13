package ru.battery.main.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.battery.main.response.dto.PredictionResponseDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, PredictionResponseDto> mlConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        //properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "soh-ml-worker");

        JsonDeserializer<PredictionResponseDto> jsonDeserializer = new JsonDeserializer<PredictionResponseDto>
                (PredictionResponseDto.class, objectMapper);

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PredictionResponseDto> mlContainerFactory(
            ConsumerFactory<String, PredictionResponseDto> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, PredictionResponseDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
