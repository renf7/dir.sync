package pl.dir.sync.common.kafka;

import pl.dir.sync.common.dto.FileKeyDto;

public class FileKeyDeserializer extends KafkaJsonDeserializer<FileKeyDto> {

    public FileKeyDeserializer() {
        super(FileKeyDto.class);
    }
}