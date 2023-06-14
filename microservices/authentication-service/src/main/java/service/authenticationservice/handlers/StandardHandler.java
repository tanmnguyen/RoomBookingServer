package service.authenticationservice.handlers;

import service.authenticationservice.entities.JwtVerification;

public class StandardHandler {
    public Handler handler;

    public StandardHandler() {
        this.handler = new ExistenceHandler();
        this.handler.setNextHandler(new ExpirationHandler());
    }

    public boolean handle(String token) {
        return handler.handle(new JwtVerification(token));
    }


}
