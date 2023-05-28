package com.cloud.userservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final Environment environment;

    @PostConstruct
    void init() {
        log.info("environment.getProperty(\"spring.datasource.password\") :{}",
                environment.getProperty("spring.datasource.password"));

    }


}
