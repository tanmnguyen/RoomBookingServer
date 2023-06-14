package service.authenticationservice.services;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.authenticationservice.entities.AuthenticationRequest;

@Log
@Service
public class ManagerService {

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Checks if the netId and password combination is correct.

     * @param authenticationRequest the authentication request of the user that wants to log in.

     * @throws BadCredentialsException when the login parameters are invalid.
     */
    public void manager(AuthenticationRequest authenticationRequest) throws
        BadCredentialsException {
        try {
            // login
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getNetId(),
                    authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Login failed");
        }
    }
}
