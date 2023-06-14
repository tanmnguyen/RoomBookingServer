package service.authenticationservice.entities;


public class AuthenticationRequest {
    private String netId;
    private String password;

    /**
     * Empty constructor.
     */
    public AuthenticationRequest() {
    }

    /**
     * Constructor.

     * @param netId netid.
     * @param password password.
     */
    public AuthenticationRequest(String netId, String password) {
        this.netId = netId;
        this.password = password;
    }

    /**
     * Get netId.

     * @return netId.
     */
    public String getNetId() {
        return netId;
    }

    /**
     * Get password.

     * @return password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set netId.

     * @param netId netId.
     */
    public void setNetId(String netId) {
        this.netId = netId;
    }

    /**
     * Set password.

     * @param password password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
