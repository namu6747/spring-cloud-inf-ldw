package com.cloud.userservice.security;

import com.cloud.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.util.ConditionalOnBootstrapEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/**")
                    .access(new WebExpressionAuthorizationManager("hasIpAddress('192.168.0.8')"))

        );
        http.authorizeRequests(auth ->
                auth.requestMatchers("/**")
                        .hasIpAddress("192.168.0.8")
        );
        http.addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter() {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
//        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
