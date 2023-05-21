package com.cloud.userservice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {

    @Test
    @DisplayName("패스워드 인코딩")
    void passwordEncoding() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwd = "6747";
        String encoded = encoder.encode(pwd);
        boolean matches = encoder.matches(pwd, encoded);
        Assertions.assertThat(matches).isTrue();
    }
}
