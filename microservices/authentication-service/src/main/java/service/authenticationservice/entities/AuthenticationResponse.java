package service.authenticationservice.entities;

public class AuthenticationResponse {

    private String jwt;
    private String netId;
    private String role;

    /**
     * Empty constructor.
     */
    public AuthenticationResponse() {
    }

    /**
     * Constructor.

     * @param jwt jwt.
     * @param netId netId.
     */
    public AuthenticationResponse(String netId, String jwt, String role) {
        this.netId = netId;
        this.jwt = jwt;
        this.role = role;
    }

    /**
     * Get Jwt.

     * @return jwt.
     */
    public String getJwt() {
        return jwt;
    }

    /**
     * Get NetId.

     * @return netId.
     */
    public String getNetId() {
        return netId;
    }

    /**
     * Get role.

     * @return role.
     */
    public String getRole() {
        return role;
    }
}
