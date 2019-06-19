module dir.sync.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;

    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.kafka;
	requires kafka.clients;

	requires static org.mapstruct.processor;
    requires static lombok;

    opens pl.dic.sync.server to javafx.fxml, spring.core;
    opens pl.dic.sync.server.controler to javafx.fxml, spring.core, spring.beans, spring.context;
    opens pl.dic.sync.server.config to javafx.fxml, spring.core, spring.beans, spring.context;
    exports pl.dic.sync.server;
}
