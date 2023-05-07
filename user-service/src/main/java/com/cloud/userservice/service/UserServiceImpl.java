package com.cloud.userservice.service;

import com.cloud.userservice.dto.UserDto;
import com.cloud.userservice.jpa.UserEntity;
import com.cloud.userservice.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                log.info("cause.getSQLState():{}", cause.getSQLState()); // todo
                log.info("cause.getErrorCode():{}", cause.getErrorCode()); // todo
            }
            log.warn("e.getSuppressed().getClass().getSimpleName:{}", e.getSuppressed().getClass().getSimpleName());
            log.warn("e.getMostSpecificCause().getCause().getClass().getSimpleName:{}", e.getMostSpecificCause().getCause().getClass().getSimpleName());
        }
        log.info("savedUserEntity: {}", savedUserEntity);
        return mapper.map(savedUserEntity, UserDto.class);
    }
}
