module dir.sync.common {
	requires static lombok;
	requires slf4j.api;
	requires kafka.clients;
	requires com.fasterxml.jackson.databind;
	
	exports pl.dir.sync.common.dto;
	exports pl.dir.sync.common.enums;
	exports pl.dir.sync.common.kafka;
}
