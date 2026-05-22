package ru.battery.main.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.battery.main.data.dto.FileUploadEventDto;
import ru.battery.main.data.dto.MlRequestForRecommendation;
import ru.battery.main.data.dto.MlRequestForSoh;
import ru.battery.main.users.dto.SendEmailDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, SendEmailDto> emailProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = kafkaConfig();

        JsonSerializer<SendEmailDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, SendEmailDto> emailKafkaTemplate(ProducerFactory<String, SendEmailDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, FileUploadEventDto> fileUploadProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = kafkaConfig();

        JsonSerializer<FileUploadEventDto> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, FileUploadEventDto> fileUploadKafkaTemplate(ProducerFactory<String,
            FileUploadEventDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, MlRequestForSoh> mlForSohProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = kafkaConfig();
        configProperties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 200 * 1024 * 1024);
        configProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 256L * 1024 * 1024);

        JsonSerializer<MlRequestForSoh> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, MlRequestForSoh> mlForSohKafkaTemplate(ProducerFactory<String, MlRequestForSoh>
                                                                        producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, MlRequestForRecommendation> mlForRecommendationProducerFactory(
            ObjectMapper objectMapper) {
        Map<String, Object> configProperties = kafkaConfig();
        configProperties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 200 * 1024 * 1024);
        configProperties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 256L * 1024 * 1024);

        JsonSerializer<MlRequestForRecommendation> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, MlRequestForRecommendation> mlForRecommendationKafkaTemplate(ProducerFactory<String,
            MlRequestForRecommendation> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    private Map<String, Object> kafkaConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        //config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        return config;
    }

}
