package com.cloud.userservice.service;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.jpa.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);

    Iterable<UserEntity> getUserByAll();
}
