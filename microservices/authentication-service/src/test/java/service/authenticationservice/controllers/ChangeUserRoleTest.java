package service.authenticationservice.controllers;



import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import service.authenticationservice.authentications.Validate;
import service.authenticationservice.entities.AuthenticationResponse;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;


@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
public class ChangeUserRoleTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationController authenticationController;

    private final AuthenticationResponse authenticationResponse =
        Mockito.mock(AuthenticationResponse.class);
    private HttpServletRequest request =
        Mockito.mock(HttpServletRequest.class);

    private Validate validate =
        Mockito.mock(Validate.class);

    private static MockedStatic<Validate> validateMockedStatic;

    @BeforeEach
    void init() {
        validateMockedStatic = Mockito.mockStatic(Validate.class);
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
    }

    @Test
    void unautorizedTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        String netId = verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), authenticationController.changeRole(request, "EMPLOYEE", "Test"));
    }

    @Test
    void incorrectRoleTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(), authenticationController.changeRole(request, "BLANK", "Test"));
    }

    @Test
    void notAdminTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "EMPLOYEE");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), authenticationController.changeRole(request, "EMPLOYEE", "Test"));
    }

    @Test
    void nonExistingUserTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(), authenticationController.changeRole(request, "EMPLOYEE", "Test"));
    }

    @Test
    void goodRequestTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);
        Optional<User> optUser = Optional.of(new User("Test", "pwd", "EMPLOYEE"));
        when(userRepository.findUserByNetIdEquals("Test")).thenReturn(optUser);

        assertEquals(ResponseEntity.ok("Succes"), authenticationController.changeRole(request, "ADMIN", "Test"));
        verify(userRepository).save(any());
    }
}
