package service.reservationhandlingservice.authentications;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.reservationhandlingservice.entities.AuthenticationResponse;

@Log
public class Validate {

    private static HttpClient CLIENT = HttpClient.newBuilder().build();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String AUTHENTICATE_PATH = "http://localhost:8084/authenticate/validatejwt";
    private static final URI AUTHENTICATION_URI = URI.create(AUTHENTICATE_PATH);
    private static final Integer ok = 200;

    /**
     * Validation for the jwt token.

     * @param request http request.
     * @return an authentication response object,

     * @throws Exception exception.
     */
    public static AuthenticationResponse validate(HttpServletRequest request) throws Exception {

        // Header fields
        String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        // if the header caries jwt token, extract it
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        } else {
            throw new Exception("Missing authentication token");
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(AUTHENTICATION_URI)
                .header("Content-Type", "text/plain; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(jwt));

        // Create request.
        HttpRequest httpRequest = builder.build();

        // Get response
        HttpResponse<String> response;
        try {
            response = CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("Error while communicating with authentication server", e);
        }

        // Status code != successful
        if (response.statusCode() != ok) {
            throw new Exception("Error - Status code = " + response.statusCode());
        }

        // Response is received from an undesired address.
        if (!response.uri().equals(AUTHENTICATION_URI)) {
            throw new Exception("Response from unknown address");
        }

        return GSON.fromJson(response.body(), AuthenticationResponse.class);
    }
}
