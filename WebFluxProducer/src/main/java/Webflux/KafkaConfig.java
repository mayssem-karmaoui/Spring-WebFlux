package Webflux;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ReactiveKafkaProducerTemplate<String, Object> reactiveKafkaProducerTemplate() {
        Map<String, Object> senderProps = new HashMap<>();
        senderProps.put("bootstrap.servers", "localhost:9092"); // Kafka broker addresses
        senderProps.put("key.serializer", StringSerializer.class); // Key serializer
        senderProps.put("value.serializer", JsonSerializer.class); // Value serializer

        SenderOptions<String, Object> senderOptions = SenderOptions.create(senderProps);
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}
