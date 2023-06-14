package service.authenticationservice.filters;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import service.authenticationservice.services.MyUserDetailService;
import service.authenticationservice.utils.JwtUtil;

public class TestFilters {

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private HttpServletResponse response =  Mockito.mock(HttpServletResponse.class);
    private FilterChain filterChain = Mockito.mock(FilterChain.class);

    private final String secretKey = "sem-2021-2022-group07a-super-duper-security-key";

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MyUserDetailService userDetailService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @InjectMocks
    @Resource
    private JwtRequestFilter filter;

    @BeforeEach
    void init() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void reset() {
    }

    /**
     * Test the case where the authorization header is null.
     */
    @Test
    void testFilterAuthorizationHeaderNull() {

        when(request.getHeader("Authorization")).thenReturn(null);
        try {
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        } catch (Exception e) {
            assertTrue("No exception should be raised", false);
        }
    }

    /**
     * Test the case where the authorization does not start with bearer.
     */
    @Test
    void testFilterAuthorizationWrongFormat() {

        when(request.getHeader("Authorization")).thenReturn("Holder token");
        try {
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        } catch (Exception e) {
            assertTrue("No exception should be raised", false);
        }
    }

    /**
     * Kill mutant test: Test when the header starts with Bearer keyword.
     * The netId must get extracted.
     */
    @Test
    void testFilterAuthorizationNetIdExtracted() {

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        try {
            filter.doFilterInternal(request, response, filterChain);
            verify(jwtUtil).extractNetId("token");
        } catch (Exception e) {
            assertTrue("No exception should be raised", false);
        }
    }


    /**
     * Test the case where the internal filter is provided with incorrect token.
     */
    @Test
    void testFilterIncorrectToken() {
        // define test user netId
        String subject = "netid";
        String token = "token";

        UserDetails userDetails = new User(subject, "password",
            new ArrayList<>());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractNetId(token)).thenReturn(subject);
        when(userDetailService.loadUserByUsername(subject)).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(null);

        try {
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        } catch (Exception e) {
            assertTrue("This filter should not throw any exceptions", false);
        }
    }

    /**
     * Test the case where the internal filter is provided with incorrect token and then correct
     * token.
     * Kill mutant verification: Test when the netId is extracted correctly.
     * The userDetailService must load the information based on the received netId.
     * Kill mutant verification: Test the usernameAndPasswordAuthenticationToken and setAuthentication
     * must be called.
     */
    @Test
    void testFilterIncorrectAndCorrectUserDetail() {
        testFilterIncorrectToken();

        // define test user netId
        String subject = "netid";

        Map<String, Object> claims = new HashMap<>();

        long current = new Date().getTime();
        long expired = current + 10 * 60 * 10;

        // create token
        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(current))
            .setExpiration(new Date(expired))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();



        UserDetails userDetails1 = new User("wrong name", "password",
            new ArrayList<>());

        UserDetails userDetails2 = new User(subject, "password",
            new ArrayList<>());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractNetId(token)).thenReturn(subject);
        when(userDetailService.loadUserByUsername(subject))
            .thenReturn(userDetails1)
            .thenReturn(userDetails2);
        when(securityContext.getAuthentication()).thenReturn(null);

        try {
            filter.doFilterInternal(request, response, filterChain);
            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(3)).doFilter(request, response);

            // Test if the usernamePasswordAuthenticationToken and setAuthentication are called
            filter.doFilterInternal(request, response, filterChain);

            // The usernameDetailService can only be called 3 times if the setting command is available.
            // This verification can be used to kill the mutant / mistakes from the developer to unset the
            // configuration for doInternalFilter.
            verify(userDetailService, times(3)).loadUserByUsername(subject);
        } catch (Exception e) {
            assertTrue("This filter should not throw any exceptions", false);
        }
    }

}
