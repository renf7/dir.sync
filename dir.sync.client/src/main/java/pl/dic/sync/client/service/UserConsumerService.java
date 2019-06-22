package pl.dic.sync.client.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import org.apache.kafka.clients.consumer.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pl.dir.sync.common.dto.FileKeyDto;

@Slf4j
@Service
public class UserConsumerService {
    private static final int _1_SECOND = 1000;
    @Autowired
    private Consumer<FileKeyDto, byte[]> consumer;
    private boolean trunOff;

    @Async
    public void consumeTopic(String topicName, String destDir) {
        consumer.subscribe(Collections.singletonList(topicName));
        ConsumerRecords<FileKeyDto, byte[]> consumerRecords = consumer.poll(_1_SECOND);

        while (!trunOff) {
            consumerRecords.forEach(record -> {
                log.debug("Consumer Record: {}, {}, {}", record.key(), record.partition(), record.offset());
                FileKeyDto key = record.key();
                String destPath = destDir + File.separator + key.getRelativePath();
                log.debug("Detected file to save: {}", destPath);
                if (record.key().isDir()) {
                    new File(destPath).mkdir();
                } else {
                    try (FileOutputStream outputStream = new FileOutputStream(destPath, true)) {
                        outputStream.write(record.value());
                    } catch (IOException e) {
                        log.debug("Cannot write to file", e);
                    }
                }
            });
            consumer.commitAsync();
            consumerRecords = consumer.poll(_1_SECOND);
        }
    }

    public void close() throws InterruptedException {
        trunOff = true;
        Thread.sleep(_1_SECOND);
        consumer.close();
    }
}