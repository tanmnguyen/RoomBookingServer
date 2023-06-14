package service.authenticationservice.controllers;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.authenticationservice.authentications.AuthenticateRequest;
import service.authenticationservice.authentications.Validate;
import service.authenticationservice.entities.AuthenticationResponse;
import service.authenticationservice.entities.User;
import service.authenticationservice.handlers.StandardHandler;
import service.authenticationservice.repositories.UserRepository;
import service.authenticationservice.utils.JwtUtil;


@Log
@RestController
@RequestMapping("/authenticate")
@ComponentScan(basePackages = {"service.authenticationservice.*"})
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private StandardHandler standardHandler;

    /**
     * Endpoint to validate jwt token.
     *

     * @param jwt jwt.
     * @return Authentication Repsonse.
     */
    @PostMapping("/validatejwt")
    public ResponseEntity<?> validateJwt(@RequestBody String jwt) {

        standardHandler = new StandardHandler();

        if (standardHandler.handle(jwt)) {
            String netId = jwtUtil.extractNetId(jwt);
            Optional<User> users = userRepository.findUserByNetIdEquals(netId);

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = users.get();
            return ResponseEntity.ok(new AuthenticationResponse(jwtUtil.extractNetId(jwt),
                jwt, user.getRole().toString()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }


    /**
     * Post mapping for changing the user role.
     */
    @PostMapping("/changeRole")
    public ResponseEntity<?> changeRole(HttpServletRequest request, @RequestParam("role") String newRole, @RequestParam("netId") String changeNetId) {
        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        //Check if user that made the request is an admin
        String role = AuthenticateRequest.getAuthenticationResponse().getRole();
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //validate that the new role provided is an existing role
        boolean contains = false;
        for (User.Role r : User.Role.values()) {
            contains = contains | r.toString().equals(newRole);
        }

        if (!contains) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //prevent method beeing called on non existing users
        Optional<User> optionalUser = userRepository.findUserByNetIdEquals(changeNetId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //Now we change role
        User user = optionalUser.get();
        user.setRole(User.Role.valueOf(newRole));
        userRepository.save(user);
        return ResponseEntity.ok("Succes");
    }

    /**
     * Request mapping to test the first end point for this service.
     */
    @RequestMapping("/hello")
    public ResponseEntity<?> hello() {

        //log.info("endpoint reached: hello world is being screamed out!");
        return ResponseEntity.ok("hello");
    }
}
