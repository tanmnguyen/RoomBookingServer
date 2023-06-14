package service.reservationhandlingservice.validate;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import service.reservationhandlingservice.authentications.Validate;

public class TestValidate {

    private HttpClient httpClient = Mockito.mock(HttpClient.class);
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private HttpResponse<String> response = Mockito.mock(HttpResponse.class);

    /**
     * Test the case where the authentication header field is is missing.
     */
    @Test
    void testMissingAuthenticationToken() {

        try {
            when(request.getHeader("Authorization")).thenReturn(null);

            assertThrows(Exception.class, () -> {
                Validate.validate(request);
            });
        } catch (Exception e) {
            System.out.println(e);
            assertTrue("This should not raise any exception", false);
        }
    }

    /**
     * Test the case where token bearer is in wrong format.
     */
    @Test
    void testWrongFormatToken() {

        try {
            when(request.getHeader("Authorization")).thenReturn("Holder token");
            assertThrows(Exception.class, () -> {
                Validate.validate(request);
            });
        } catch (Exception e) {
            assertTrue("This should not raise any exception", false);
        }
    }

    /**
     * Test the case where communication with the authentication service is error.
     */
    @Test
    void testErrorCommunication() {
        try {
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(HttpTimeoutException.class);

            Whitebox.setInternalState(Validate.class, "CLIENT", httpClient);

            when(request.getHeader("Authorization")).thenReturn("Bearer token");

            assertThrows(Exception.class, () -> {
                Validate.validate(request);
            });
        } catch (Exception e) {
            assertTrue("This should not raise any exception", false);
        }
    }

    /**
     * Test the case where response status is not success.
     */
    @Test
    void testNotSuccessResponse() {
        try {
            when(response.statusCode()).thenReturn(400);

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

            Whitebox.setInternalState(Validate.class, "CLIENT", httpClient);

            when(request.getHeader("Authorization")).thenReturn("Bearer token");

            assertThrows(Exception.class, () -> {
                Validate.validate(request);
            });
        } catch (Exception e) {
            assertTrue("This should not raise any exception", false);
        }
    }

    /**
     * Test the case where response is received from an unknown url.
     */
    @Test
    void testResponseFromWrongUrl() {

        try {
            when(response.statusCode()).thenReturn(200);
            when(response.uri()).thenReturn(URI.create("https://maliciouslocation"));

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

            Whitebox.setInternalState(Validate.class, "CLIENT", httpClient);

            when(request.getHeader("Authorization")).thenReturn("Bearer token");

            assertThrows(Exception.class, () -> {
                Validate.validate(request);
            });
        } catch (Exception e) {
            assertTrue("This should not raise any exception", false);
        }
    }

    /**
     * Test the case where the authentication is successful.
     */
    @Test
    void testAuthenticationSuccessful() {

        try {
            when(response.statusCode()).thenReturn(200);
            when(response.uri()).thenReturn(URI.create("http://localhost:8084/authenticate/validatejwt"));

            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

            Whitebox.setInternalState(Validate.class, "CLIENT", httpClient);

            when(request.getHeader("Authorization")).thenReturn("Bearer token");

            Validate.validate(request);

            verify(response).body();
        } catch (Exception e) {
            assertTrue("This should not raise any exception", false);
        }
    }
}

