package com.cloud.userservice.service;

import com.cloud.userservice.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
