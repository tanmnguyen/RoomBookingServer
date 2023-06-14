package service.authenticationservice.handlers;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJws;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.DefaultJwtParser;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jose4j.jwt.JwtClaims;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import service.authenticationservice.entities.JwtVerification;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;
import service.authenticationservice.utils.JwtUtil;

public class ExpirationHandlerTests {

    private final String secretKey = "sem-2021-2022-group07a-super-duper-security-key";


    @Mock
    private UserRepository userRepository;

    /**
     * Test when the token has already expired.
     */
    @Test
    void testExpiration() {

        // define test user netId
        String subject = "netid";

        Map<String, Object> claims = new HashMap<>();

        // define expiration date (in the past)
        long past = new Date().getTime() - 1;

        // create token
        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(past))
            .setExpiration(new Date(past))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        // define chain
        Handler handler = new BaseHandler();
        handler.setNextHandler(new ExpirationHandler());

        assertFalse(handler.handle(new JwtVerification(token)));
    }

    /**
     * Test when the token is not valid.
     */
    @Test
    void testInvalidToken() {

        String token = "invalid token";

        // define chain
        Handler handler = new BaseHandler();
        handler.setNextHandler(new ExpirationHandler());

        assertFalse(handler.handle(new JwtVerification(token)));
    }
}

