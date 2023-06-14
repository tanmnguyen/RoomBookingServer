package service.authenticationservice.handlers;

import io.jsonwebtoken.Claims;
import service.authenticationservice.entities.JwtVerification;

public class ExistenceHandler extends BaseHandler {



    /**
     * Handle if jwt is indeed valid and not out of date.

     * @param jwtVerification jwt.

     * @return boolean value for verification.
     */
    @Override
    public boolean handle(JwtVerification jwtVerification) {

        String jwt = jwtVerification.getJwt();


        Claims claims = extractAllClaims(jwt);
        if (claims == null) {
            return false;
        }

        return super.handlerNext(jwtVerification);
    }
}
