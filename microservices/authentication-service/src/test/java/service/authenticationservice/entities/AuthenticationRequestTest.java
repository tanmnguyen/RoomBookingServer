package service.authenticationservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;



public class AuthenticationRequestTest {

    private String defaultPassword = "password";
    
    /**
     * Test the empty constructor.
     */
    @Test
    void emptyConstructorTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        assertNotNull(authenticationRequest);
    }

    /**
     * Test non-empty constructor.
     */
    @Test
    void constructorTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("id",
                defaultPassword);
        assertNotNull(authenticationRequest);
    }

    /**
     * Test get methods.
     */
    @Test
    void getTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("id",
                defaultPassword);
        assertEquals(authenticationRequest.getNetId(), "id");
        assertEquals(authenticationRequest.getPassword(), defaultPassword);
    }


    /**
     * Test set methods.
     */
    @Test
    void setTest() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("id",
                defaultPassword);
        authenticationRequest.setPassword("new password");
        authenticationRequest.setNetId("new id");

        assertEquals(authenticationRequest.getPassword(), "new password");
        assertEquals(authenticationRequest.getNetId(), "new id");

    }
}
