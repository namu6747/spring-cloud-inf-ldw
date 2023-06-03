package com.cloud.userservice.config;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitConfig {

    private final Environment environment;
    private final UserService userService;

    @EventListener(classes = {ApplicationReadyEvent.class})
    void init() {
        log.info("environment.getProperty(\"spring.datasource.password\") :{}",
                environment.getProperty("spring.datasource.password"));

        userService.createUser(UserDto.builder()
                .email("namu6748@naver.com")
                .name("박재민")
                .pwd("woals")
                .createdAt(LocalDateTime.now())
                .build());
        userService.createUser(UserDto.builder()
                .email("namu6749@naver.com")
                .name("재민이")
                .pwd("woals")
                .createdAt(LocalDateTime.now())
                .build());
        userService.createUser(UserDto.builder()
                .email("namu6750@naver.com")
                .name("재민삼")
                .pwd("woals")
                .createdAt(LocalDateTime.now())
                .build());
    }
}

