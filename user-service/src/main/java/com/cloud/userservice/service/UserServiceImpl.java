package com.cloud.userservice.service;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.jpa.UserEntity;
import com.cloud.userservice.jpa.UserRepository;
import com.cloud.userservice.response.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        UserEntity savedUserEntity = null;
        try {
            savedUserEntity = userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            log.warn("e.getRootCause().getClass().getSimpleName:{}", e.getRootCause().getClass().getSimpleName());
            log.warn("e.getCause().getClass().getSimpleName:{}", e.getCause().getClass().getSimpleName());
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                log.info("cause.getConstraintName():{}", cause.getConstraintName());
                log.info("cause.getSQLState():{}", cause.getSQLState());
                log.info("cause.getErrorCode():{}", cause.getErrorCode());
            }
            return null;
        }
        log.info("savedUserEntity: {}", savedUserEntity);
        return mapper.map(savedUserEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : " + userId));

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        List<ResponseOrder> ordersList = new ArrayList<>();
        userDto.setOrders(ordersList);
        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return User.withUsername(userEntity.getEmail()).password(userEntity.getEncryptedPwd()).authorities(AuthorityUtils.NO_AUTHORITIES).build();
    }
}
