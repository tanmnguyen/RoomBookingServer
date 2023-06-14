package service.authenticationservice.handlers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import service.authenticationservice.entities.JwtVerification;

public class BaseHandlerTests {


    /**
     * Test when there is no handler in the chain.
     */
    @Test 
    void handleTest() {
        BaseHandler handler = new BaseHandler();
        JwtVerification jwtVerification = new JwtVerification("token");

        assertTrue("The method should return true in case of no handler",
            handler.handle(jwtVerification));
    }

    /**
     * Test the extract claims function when the error is raised.
     */
    @Test
    void testExtractClaimsFunction() {
        String token = "invalid token";

        BaseHandler handler = new BaseHandler();
        assertNull("the method should return null when the token is invalid",
            handler.extractClaim(token, Claims::getExpiration));

    }
}
