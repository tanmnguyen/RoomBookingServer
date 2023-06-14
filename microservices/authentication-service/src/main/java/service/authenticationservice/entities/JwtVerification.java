package service.authenticationservice.entities;

public class JwtVerification {
    private String jwt;

    public JwtVerification(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
