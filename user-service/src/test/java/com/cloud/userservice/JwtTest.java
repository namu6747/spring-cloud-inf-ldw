package com.cloud.userservice;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@SpringBootTest
public class JwtTest {

    @Autowired
    Environment environment;

    @Test
    void tet() {
        SecretKey secretKey = Keys.hmacShaKeyFor(environment.getProperty("token.secret").getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject("12378924")
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("token.expiration_time"))))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        Key key = Keys.hmacShaKeyFor(environment.getProperty("token.secret").getBytes(StandardCharsets.UTF_8));
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        Jwt parse = jwtParser.parse(token);
        System.out.println("parse = " + parse);
    }

}
