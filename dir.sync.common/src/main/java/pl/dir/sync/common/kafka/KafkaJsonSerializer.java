package pl.dir.sync.common.kafka;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaJsonSerializer implements Serializer {

    @Override
    public byte[] serialize(String s, Object o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception e) {
            log.error("Cannot serialize", e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map configs, boolean isKey) {

    }
}