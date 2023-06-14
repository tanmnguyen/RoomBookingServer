package service.authenticationservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import service.authenticationservice.entities.User;
import service.authenticationservice.repositories.UserRepository;

@Log
@Service
public class JwtUtil {
    private final String secretKey = "sem-2021-2022-group07a-super-duper-security-key";

    @Autowired
    private UserRepository userRepository;

    /**
     * Get netID using the jwt token.

     * @param token jwt token.
     * @return netId.
     */
    public String extractNetId(String token) {


        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract claim using a specific function.

     * @param token jwt token.
     * @param claimsFunction claim function.
     * @param <T> type of object.

     * @return claim associated with the provided token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    /**
     * Extract all claims.

     * @param token Jwt token.

     * @return All claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }


    /**
     * Generate token using the provided user information.

     * @param user user information.

     * @return The newly generated token.
     */
    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user.getUsername());
    }


    /**
     * Create token.

     * @param claims claims.
     * @param subject netId.

     * @return token.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long current = System.currentTimeMillis();
        long duration = 1000 * 60 * 60 * 10;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(current))
                .setExpiration(new Date(current + duration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }



}
