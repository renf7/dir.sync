package pl.dic.sync.server.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Bean
	public AdminClient kafkaAdminClient() {
		Map<String, Object> configs = new HashMap<>();
		configs.put("bootstrap.servers", this.bootstrapAddress);
		return AdminClient.create(configs);
	}
}