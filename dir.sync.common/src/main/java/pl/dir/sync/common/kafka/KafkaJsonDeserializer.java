package pl.dir.sync.common.kafka;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaJsonDeserializer<T> implements Deserializer {
    private Class<T> type;

    public KafkaJsonDeserializer(Class<T> type) {
        this.type = type;
    }

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            obj = mapper.readValue(bytes, type);
        } catch (Exception e) {
            log.error("Cannot deserialize", e);
        }
        return obj;
    }

    @Override
    public void close() {

    }
}