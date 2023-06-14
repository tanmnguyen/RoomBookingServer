package service.authenticationservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class JwtVerificationTest {

    /**
     * Test non-empty constructor.
     */
    @Test
    void constructorTest() {
        JwtVerification jwt = new JwtVerification("jwt");
        assertNotNull(jwt);

    }

    /**
     * Test get methods.
     */
    @Test
    void getTest() {
        JwtVerification jwt = new JwtVerification("jwt");
        assertEquals(jwt.getJwt(), "jwt");
    }
}
