module dir.sync.client {
	requires javafx.controls;
	requires javafx.fxml;
	// requires transitive javafx.graphics;

	requires spring.beans;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.core;

	requires static lombok;
	requires slf4j.api;

	requires dir.sync.common;
	requires spring.web;

	opens pl.dic.sync.client to javafx.fxml, spring.core;
	opens pl.dic.sync.client.controler to javafx.fxml, spring.core, spring.beans, spring.context;
	opens pl.dic.sync.client.service to spring.core, spring.beans, spring.context;
	opens pl.dic.sync.client.config to spring.core, spring.beans, spring.context;

	exports pl.dic.sync.client;
}
