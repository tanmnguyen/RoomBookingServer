package service.authenticationservice.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import service.authenticationservice.entities.JwtVerification;
import service.authenticationservice.handlers.ExistenceHandler;
import service.authenticationservice.handlers.ExpirationHandler;
import service.authenticationservice.handlers.Handler;
import service.authenticationservice.handlers.StandardHandler;
import service.authenticationservice.services.MyUserDetailService;
import service.authenticationservice.utils.JwtUtil;

@Log
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private JwtUtil jwtUtil;

    private StandardHandler standardHandler;

    /**
     * Apply the filter to receive the jwt token and do chain of responsibility filter.

     * @param request incoming request.
     * @param response response.
     * @param filterChain chain of filters.

     * @throws ServletException exception.
     * @throws IOException exception.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String netId = null;
        String jwt = null;


        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            netId = jwtUtil.extractNetId(jwt);
        }

        standardHandler = new StandardHandler();
        if (netId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //user found in database by netid from token
            UserDetails userDetails = userDetailService.loadUserByUsername(netId);

            if (standardHandler.handle(jwt) && netId.equals(userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);

    }
}
