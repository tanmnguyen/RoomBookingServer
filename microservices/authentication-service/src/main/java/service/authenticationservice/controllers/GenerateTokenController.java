package service.authenticationservice.controllers;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.authenticationservice.entities.AuthenticationRequest;
import service.authenticationservice.entities.AuthenticationResponse;
import service.authenticationservice.services.ManagerService;
import service.authenticationservice.services.MyUserDetailService;
import service.authenticationservice.utils.JwtUtil;

@Log
@RestController
@RequestMapping("/generate")
@ComponentScan(basePackages = {"service.authenticationservice.*"})
public class GenerateTokenController {

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint for login.

     * @param authenticationRequest Authentication Request.
     * @return an authentication response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> generateToken(
        @RequestBody AuthenticationRequest authenticationRequest) {

        log.info("checking " + authenticationRequest.getNetId() + " " + authenticationRequest.getPassword());
        try {
            // login
            managerService.manager(authenticationRequest);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // retrieve user information by netid.
        final UserDetails user =
            userDetailService.loadUserByUsername(authenticationRequest.getNetId());

        // receive token if login successfully
        final String jwt = jwtUtil.generateToken(user);

        // return the token for later authentication.
        return ResponseEntity.ok(new AuthenticationResponse(user.getUsername(), jwt,
            user.getAuthorities().toString()));
    }
}
