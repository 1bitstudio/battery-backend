package ru.battery.file.configuration;

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
import ru.battery.file.dto.FileUploadEventDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, FileUploadEventDto> fileUploadEventConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = kafkaConfig();

        JsonDeserializer<FileUploadEventDto> jsonDeserializer = new JsonDeserializer<FileUploadEventDto>(
                FileUploadEventDto.class, objectMapper
        );

        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FileUploadEventDto> fileUploadEventContainerFactory(
            ConsumerFactory<String, FileUploadEventDto> consumerFactory
    ) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, FileUploadEventDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }

    private Map<String, Object> kafkaConfig() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        //properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "file-read-worker");
        return properties;
    }

}
