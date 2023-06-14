package service.authenticationservice.authentications;


import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.authenticationservice.entities.AuthenticationResponse;

public class AuthenticateRequest {

    private static AuthenticationResponse authenticationResponse;

    public static AuthenticationResponse getAuthenticationResponse() {
        return authenticationResponse;
    }

    /**
     * Authenticate a request.

     * @param request the request to authenticate.
     * @return a response entity.
     */
    public static ResponseEntity<Object> authenticate(HttpServletRequest request) {
        try {
            authenticationResponse = Validate.validate(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return null;
    }
}
