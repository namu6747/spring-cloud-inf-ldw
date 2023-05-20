package com.cloud.userservice.security;

import com.cloud.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final ApplicationContext applicationContext;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final AuthenticationConfiguration authenticationConfiguration;

    private static final String[] WHITE_LIST = {
            "/users/**",
            "/",
            "/**"
    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.authorizeHttpRequests(authorize ->
                    authorize.requestMatchers(WHITE_LIST).permitAll()
                             .requestMatchers(PathRequest.toH2Console()).permitAll()
                             .requestMatchers(new IpAddressMatcher("127.0.0.1")).permitAll()
        );
        http.addFilter(authenticationFilter());
        return http.build();
    }

    private AuthenticationFilter authenticationFilter() throws Exception {
        var authenticationFilter = new AuthenticationFilter(authenticationManager());
        return authenticationFilter;
    }

    private AuthenticationManager authenticationManager() throws Exception {
        var authenticationManagerBuilder = authenticationConfiguration.authenticationManagerBuilder(objectPostProcessor, applicationContext);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
