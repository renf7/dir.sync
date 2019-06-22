package pl.dic.sync.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pl.dir.sync.common.dto.FileKeyDto;
import pl.dir.sync.common.kafka.FileKeyDeserializer;
import pl.dir.sync.common.kafka.KafkaJsonSerializer;

import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class KafkaConfig {
    private static final String KAFKA_DIC_SYNC_CLIENT_GROUP = "pl.dic.sync.client";
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public Producer<FileKeyDto, byte[]> KafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaJsonSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_DIC_SYNC_CLIENT_GROUP);

        return new KafkaProducer<>(props);
    }

    @Bean
    public Consumer<FileKeyDto, byte[]> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, FileKeyDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_DIC_SYNC_CLIENT_GROUP);

        // Create the consumer using props.
        final Consumer<FileKeyDto, byte[]> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        return consumer;
    }
}