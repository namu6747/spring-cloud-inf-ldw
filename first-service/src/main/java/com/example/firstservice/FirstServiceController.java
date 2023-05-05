package com.example.firstservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/first-service")
@RequiredArgsConstructor
public class FirstServiceController {

    private final Environment env;

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to the First Service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        return header;
    }

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());
        String[] activeProfiles = env.getActiveProfiles();
        for (String activeProfile : activeProfiles) {
            log.warn("activeProfile : {}", activeProfile);
        }

        String[] defaultProfiles = env.getDefaultProfiles();
        for (String defaultProfile : defaultProfiles) {
            log.error("defaultProfile: {}", defaultProfile);
        }
        return String.format("hi, message from first on PORT %s", env.getProperty("local.server.port"));
    }

}
