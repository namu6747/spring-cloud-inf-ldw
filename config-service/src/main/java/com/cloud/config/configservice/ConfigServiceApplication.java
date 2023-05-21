package com.cloud.config.configservice;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {

	@Autowired
	private Environment environment;
	private final Logger logger = LoggerFactory.getLogger(ConfigServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConfigServiceApplication.class, args);
	}

	@PostConstruct
	void init() {
		logger.warn("environment.getProperty(\"users.home\") : {}", environment.getProperty("users.home"));
	}

}
