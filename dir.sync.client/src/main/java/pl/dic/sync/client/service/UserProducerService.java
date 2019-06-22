package pl.dic.sync.client.service;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pl.dir.sync.common.dto.FileKeyDto;

/**
 *
 * @author guys
 */
@Slf4j
@Service
public class UserProducerService {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Autowired
    private Producer<FileKeyDto, byte[]> producer;

    public void sendFile(String topicName, FileKeyDto fileName, byte[] file) throws InterruptedException, ExecutionException {
        long time = System.currentTimeMillis();
  
        try {
            final ProducerRecord<FileKeyDto, byte[]> record = new ProducerRecord<>(topicName, fileName, file);

            RecordMetadata metadata = producer.send(record).get();

            long elapsedTime = System.currentTimeMillis() - time;
            log.debug("sent record(key={}) meta(partition={}, offset={}) time={}",
                    record.key(), metadata.partition(),
                    metadata.offset(), elapsedTime);
        } finally {
            producer.flush();
        }
    }

    public void close() {
        producer.close();
    }

}