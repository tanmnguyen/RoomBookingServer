package service.authenticationservice.handlers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.function.Function;
import service.authenticationservice.entities.JwtVerification;

public class BaseHandler implements Handler {
    private Handler nextHandler;

    @Override
    public void setNextHandler(Handler handler) {
        this.nextHandler = handler;
    }

    @Override
    public boolean handle(JwtVerification jwt) {
        return handlerNext(jwt);
    }

    private final String secretKey = "sem-2021-2022-group07a-super-duper-security-key";

    /**
     * Extract all claims.

     * @param jwt Jwt token.

     * @return All claims.
     */
    public Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Extract claim using a specific function.

     * @param jwt jwt token.
     * @param claimsFunction claim function.
     * @param <T> type of object.

     * @return claim associated with the provided token.
     */
    public <T> T extractClaim(String jwt, Function<Claims, T> claimsFunction) {
        final Claims claims = extractAllClaims(jwt);
        try {
            return claimsFunction.apply(claims);
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean handlerNext(JwtVerification jwt) {
        if (nextHandler == null) {
            return true;
        }
        return nextHandler.handle(jwt);
    }
}
