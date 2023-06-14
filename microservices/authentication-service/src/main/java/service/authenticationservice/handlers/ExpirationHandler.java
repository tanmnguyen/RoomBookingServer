package service.authenticationservice.handlers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Date;
import service.authenticationservice.entities.JwtVerification;

public class ExpirationHandler extends BaseHandler {
    /**
     * Handle if jwt is indeed valid and not out of date.

     * @param jwtVerification jwt.

     * @return boolean value for verification.
     */
    @Override
    public boolean handle(JwtVerification jwtVerification) {

        String jwt = jwtVerification.getJwt();

        try {
            Date expiration = extractClaim(jwt, Claims::getExpiration);
            if (expiration == null) {
                return false;
            }
        } catch (ExpiredJwtException e) {
            // token has expired
            return false;
        }

        return super.handlerNext(jwtVerification);
    }
}
