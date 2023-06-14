package service.reservationhandlingservice.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class AuthenticationResponseTest {

    private AuthenticationResponse authenticationResponse;

    @BeforeEach
    void setUp() {
        authenticationResponse = new AuthenticationResponse("netId", "jwt", "admin");
    }

    @Test
    void testConstructor() {
        assertNotNull(authenticationResponse);
    }

    @Test
    void getJwt() {
        assertEquals("jwt", authenticationResponse.getJwt());
    }

    @Test
    void getNetId() {
        assertEquals("netId", authenticationResponse.getNetId());
    }

    @Test
    void getRole() {
        assertEquals("admin", authenticationResponse.getRole());
    }
}