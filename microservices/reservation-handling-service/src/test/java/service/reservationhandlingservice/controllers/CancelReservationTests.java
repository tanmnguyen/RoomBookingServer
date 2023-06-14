package service.reservationhandlingservice.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import service.reservationhandlingservice.authentications.Validate;
import service.reservationhandlingservice.entities.AuthenticationResponse;
import service.reservationhandlingservice.entities.Reservation;
import service.reservationhandlingservice.repositories.ResearchGroupRepository;
import service.reservationhandlingservice.repositories.ReservationRepository;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc
public class CancelReservationTests {

    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationController reservationController;


    @MockBean
    private ResearchGroupRepository researchGroupRepository;


    // mock the validation
    private static MockedStatic<Validate> validateMockedStatic;

    private final Integer unauthorizedCode = 404;

    @BeforeEach
    void init() {
        validateMockedStatic = Mockito.mockStatic(Validate.class);
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
    }

    /**
     * Test when the authenticate does not allow the attempt.
     */
    @Test
    void testAuthenticateNotAllow() {
        long requestBody = 01;

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            reservationController.cancelReservation(request, requestBody));
    }

    /**
     * Test when the authenticate does allow the attempt but
     * The reservation id does not match.
     */
    @Test
    void testReservationIdNotMatch() {
        long reservationId = 01;
        String netId = "netId";

        AuthenticationResponse response = new AuthenticationResponse(netId, "token",
            "EMPLOYEE");

        Optional<Reservation> reservationOptional = Optional.empty();

        // allow access
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        // define response of the repository
        when(reservationRepository.findByHostIdAndReservationId(netId, reservationId))
            .thenReturn(reservationOptional);

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            reservationController.cancelReservation(request, reservationId));
    }

    /**
     * Test when the provided information are valid.
     */
    @Test
    void testDeleteSuccess() {
        long reservationId = 01;
        String netId = "netId";

        AuthenticationResponse response = new AuthenticationResponse(netId, "token",
            "EMPLOYEE");

        Optional<Reservation> reservationOptional = Optional.of(new Reservation(netId,
            "roomid",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        // allow access
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        // define response of the repository
        when(reservationRepository.findByHostIdAndReservationId(netId, reservationId))
            .thenReturn(reservationOptional);

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.OK).build(),
            reservationController.cancelReservation(request, reservationId));
    }

    /**
     * Testcase where a non-admin tries to access another account's reservation.
     */
    @Test
    void testOtherAccountsReservationNoAdmin() {
        long reservationId = 01;
        String netId = "netId";

        AuthenticationResponse response = new AuthenticationResponse(netId, "token",
            "EMPLOYEE");

        Optional<Reservation> reservationOptional = Optional.empty();

        // allow access
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        // define response of the repository
        when(reservationRepository.findByHostIdAndReservationId(netId, reservationId))
            .thenReturn(reservationOptional);

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            reservationController.cancelReservation(request, reservationId));
    }

    /**
     * Testcase where an admin tries to access another accounts reservation.
     */
    @Test
    void testOtherAccountsReservationAdmin() {
        long reservationId = 01;
        String netId = "netId";

        AuthenticationResponse response = new AuthenticationResponse(netId, "token",
            "admin");

        Optional<Reservation> reservationOptional = Optional.of(new Reservation(netId,
            "roomid",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1)));

        // allow access
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        // define response of the repository
        when(reservationRepository.findByHostIdAndReservationId(netId, reservationId))
            .thenReturn(reservationOptional);

        Assertions.assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            reservationController.cancelReservation(request, reservationId));
    }

}
