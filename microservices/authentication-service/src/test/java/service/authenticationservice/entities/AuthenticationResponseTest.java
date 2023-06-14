package service.authenticationservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;



public class AuthenticationResponseTest {
    /**
     * Test the empty constructor.
     */
    @Test
    void emptyConstructorTest() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        assertNotNull(authenticationResponse);
    }

    /**
     * Test non-empty constructor.
     */
    @Test
    void constructorTest() {
        AuthenticationResponse authenticationResponse =
                new AuthenticationResponse("id", "jwt", "role");

        assertNotNull(authenticationResponse);
    }

    /**
     * Test get methods.
     */
    @Test
    void getTest() {
        AuthenticationResponse authenticationResponse =
                new AuthenticationResponse("id", "jwt", "role");

        assertEquals(authenticationResponse.getNetId(), "id");
        assertEquals(authenticationResponse.getJwt(), "jwt");
        assertEquals(authenticationResponse.getRole(), "role");

    }
}
