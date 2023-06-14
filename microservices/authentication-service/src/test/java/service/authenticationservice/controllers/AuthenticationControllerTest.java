package service.authenticationservice.controllers;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.MockedStaticImpl;
import org.mockito.internal.verification.DefaultRegisteredInvocations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import service.authenticationservice.entities.AuthenticationRequest;
import service.authenticationservice.entities.AuthenticationResponse;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;
import service.authenticationservice.services.MyUserDetailService;
import service.authenticationservice.utils.JwtUtil;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private MyUserDetailService myUserDetailService;

    private final Integer unauthorizedCode = 401;
    private final Integer ok = 200;
    private final String secretKey = "sem-2021-2022-group07a-super-duper-security-key";

    // valid net id
    private final String defaultNetId = "tester";

    // valid password for validNetId
    private final String defaultPassword = "password123.";

    // default role
    private final String defaultRole = "ADMIN";

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Gson gson = new Gson();


    private Optional<User> usersValid;
    private Optional<User> usersInvalid;


    @BeforeEach
    void init() {
        usersValid = Optional.of(new User(defaultNetId, defaultPassword, defaultRole));
        usersInvalid = Optional.empty();
    }

    @BeforeTestMethod
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     * Test endpoint for hello.
     * This end point is ex-cheme-ly important as it is the first api for security as well
     * as the first endpoint to indicate the testing for endpoints work.
     */
    @Test
    public void endpointHelloTest() {
        RequestBuilder request = MockMvcRequestBuilders.get("/authenticate/hello");
        try {
            MvcResult response = mockMvc.perform(request).andReturn();
            assertEquals("hello", response.getResponse().getContentAsString());
        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    /**
     * Test when the user provides incorrect login details.
     */
    @Test
    public void loginFailRequestAccountNotMatchTest() {
        AuthenticationRequest authenticationRequest =
            new AuthenticationRequest("random", "random");

        try {
            String requestBody = objectMapper.writeValueAsString(authenticationRequest);

            RequestBuilder request = MockMvcRequestBuilders
                .post("/generate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

            MvcResult response = mockMvc.perform(request).andReturn();
            assertEquals(unauthorizedCode, response.getResponse().getStatus());

        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    /**
     * Test the controller for token verification.
     * Testing when the provided token is invalid
     */
    @Test
    void testValidateJwtControllerInvalidToken() {
        try {
            String invalidToken = "invalid+token";

            RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/validatejwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidToken);

            MvcResult response = mockMvc.perform(request).andReturn();
            assertEquals(unauthorizedCode, response.getResponse().getStatus());

        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    private String testLogin() {
        String token = null;
        AuthenticationRequest authenticationRequest =
            new AuthenticationRequest(defaultNetId, defaultPassword);

        try {
            String requestBody = objectMapper.writeValueAsString(authenticationRequest);

            RequestBuilder request = MockMvcRequestBuilders
                .post("/generate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

            MvcResult response = mockMvc.perform(request).andReturn();
            assertEquals(ok, response.getResponse().getStatus());

            // get token from the response
            token = gson
                .fromJson(response.getResponse().getContentAsString(), AuthenticationResponse.class)
                .getJwt();

        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }

        assertNotNull(token, "token is null");
        return token;
    }

    void testValidate(String token) {
        try {
            RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/validatejwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token);

            MvcResult response = mockMvc.perform(request).andReturn();

            // assert the communication is successful
            assertEquals(ok, response.getResponse().getStatus());


            AuthenticationResponse user = gson.fromJson(response.getResponse().getContentAsString(),
                AuthenticationResponse.class);

            assertEquals(token, user.getJwt(), "The retrieved token is not as expected");
            assertEquals(defaultNetId, user.getNetId(), "The retrieved netId is not as expected");
            assertEquals(defaultRole, user.getRole(), "The retrieved role is not as expected");
        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    /**
     * Test the controller for token verification.
     * Testing when the provided token is valid
     */
    @Test
    void testValidateJwtControllerValidToken() {
        when(userRepository.findUserByNetIdEquals(defaultNetId))
            .thenReturn(usersValid);

        String token = testLogin();
        testValidate(token);
    }

    void testValidateUserRemoved(String token) {
        try {
            RequestBuilder request = MockMvcRequestBuilders
                .post("/authenticate/validatejwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token);

            MvcResult response = mockMvc.perform(request).andReturn();

            // assert the communication is successful
            assertEquals(unauthorizedCode, response.getResponse().getStatus());

        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    /**
     * Test the controller for token verification.
     * Testing when the provided token is valid but the user is already removed
     */
    @Test
    void testInvalidateJwtControllerValidTokenUserRemoved() {
        when(userRepository.findUserByNetIdEquals(defaultNetId))
            .thenReturn(usersValid)
            .thenReturn(usersValid)
            .thenReturn(usersInvalid);

        String token = testLogin();
        testValidateUserRemoved(token);
    }
}
