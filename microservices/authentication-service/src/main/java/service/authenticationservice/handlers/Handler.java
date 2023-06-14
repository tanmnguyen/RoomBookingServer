package service.authenticationservice.handlers;

import service.authenticationservice.entities.JwtVerification;

public interface Handler {
    public void setNextHandler(Handler handler);

    public boolean handle(JwtVerification jwt);
}
