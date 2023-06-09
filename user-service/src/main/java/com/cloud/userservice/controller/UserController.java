package com.cloud.userservice.controller;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.jpa.UserEntity;
import com.cloud.userservice.response.ResponseUser;
import com.cloud.userservice.service.UserService;
import com.cloud.userservice.vo.Greeting;
import com.cloud.userservice.vo.UserCond;
import com.cloud.userservice.vo.UserResponse;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final Environment environment;
    private final Greeting greeting;
    private final UserService userService;

    @EventListener(classes = {ApplicationReadyEvent.class})
    void init() {
        log.warn("\n\nserver port : {}\n", environment.getProperty("local.server.port"));
    }

    @GetMapping("/health_check")
    @Timed(value = "users.status", longTask = true)
    public Map<String, Object> status() throws Exception {
        String host = Inet4Address.getLocalHost().getHostAddress();
        String localPort = environment.getProperty("local.server.port");
        String message = greeting.message();
        String port = environment.getProperty("server.port");
        String tokenSecret = environment.getProperty("token.secret");
        String tokenExpirationTime = environment.getProperty("token.expiration_time");
        return Map.of("host", host,
                "localPort", localPort,
                "message", message,
                "port", port,
                "tokenSecret", tokenSecret,
                "tokenExpirationTime", tokenExpirationTime);
    }

    @GetMapping("/welcome")
    @Timed(value = "users.welcome", longTask = true)
    public String welcome(HttpServletRequest request) throws Exception {
        log.info("InetAddress.getByName(request.getRemoteAddr()) : {}", InetAddress.getByName(request.getRemoteAddr()));
        log.info("request.getRemoteAddr : {}", request.getRemoteAddr());
        return greeting.message();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCond userCond) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userCond, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        if (createdUser == null) {
            return ResponseEntity.badRequest().build();
        }
        UserResponse userResponse = modelMapper.map(createdUser, UserResponse.class);
        return ResponseEntity.created(URI.create("/users/" + userDto.getUserId())).body(userResponse);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();
        ModelMapper modelMapper = new ModelMapper();
        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(user -> result.add(modelMapper.map(user, ResponseUser.class)));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);
        ModelMapper modelMapper = new ModelMapper();
        return ResponseEntity.ok(modelMapper.map(userDto, ResponseUser.class));
    }

}
