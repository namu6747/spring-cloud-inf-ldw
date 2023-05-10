package com.cloud.catalogservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final Environment environment;

    @EventListener(classes = {ApplicationReadyEvent.class})
    void init() {
        log.warn("\n\nserver port : {}\n", environment.getProperty("local.server.port"));
    }

}
