package com.cloud.userservice.controller;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.service.UserService;
import com.cloud.userservice.vo.Greeting;
import com.cloud.userservice.vo.UserCond;
import com.cloud.userservice.vo.UserResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment environment;
    private final Greeting greeting;
    private final UserService userService;

    @GetMapping("/health-check")
    public String status() {
        return environment.getProperty("greeting.message");
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.message();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserCond userCond) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userCond, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        UserResponse userResponse = modelMapper.map(createdUser, UserResponse.class);
        return ResponseEntity.created(URI.create("/users/" + userDto.getUserId())).body(userResponse);
    }

}
