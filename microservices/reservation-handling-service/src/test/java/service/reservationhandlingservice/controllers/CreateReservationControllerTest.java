package service.reservationhandlingservice.controllers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import service.reservationhandlingservice.authentications.Validate;
import service.reservationhandlingservice.entities.AuthenticationResponse;
import service.reservationhandlingservice.entities.Reservation;
import service.reservationhandlingservice.entities.ReservationHolder;
import service.reservationhandlingservice.repositories.ResearchGroupRepository;
import service.reservationhandlingservice.repositories.ReservationRepository;


@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc
class CreateReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationController reservationController;

    private final AuthenticationResponse authenticationResponse =
        Mockito.mock(AuthenticationResponse.class);
    private HttpServletRequest request =
        Mockito.mock(HttpServletRequest.class);

    private Validate validate =
        Mockito.mock(Validate.class);

    private ReservationHolder temp;

    private DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static MockedStatic<Validate> validateMockedStatic;

    private LocalDateTime today;

    private AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "admin");

    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private ResearchGroupRepository researchGroupRepository;

    private static WireMockServer wm;

    @BeforeAll
    static void setupWireMock() {
        wm = new WireMockServer(8082);
        wm.start();
        configureFor("localhost", 8082);
    }

    @BeforeEach
    void init() {
        validateMockedStatic = Mockito.mockStatic(Validate.class);
        today = LocalDateTime.now();
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
        today = LocalDateTime.now();
    }

    @AfterAll
    static void resetWm() {
        wm.stop();
    }

    @Test
    void createReservationUnauthorized() throws Exception {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(10, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(14, 0))), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            reservationController.createReservation(request, temp));

    }

    @Test
    void createReservationBeforeToday() throws Exception {
        temp = new ReservationHolder(formatter1.format(today.minusDays(1)), formatter1.format(today.plusHours(2)), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can not create reservations before today"),
            reservationController.createReservation(request, temp));
    }

    @Test
    void createReservationAfter2Weeks() throws Exception {
        temp = new ReservationHolder(formatter1.format(today.plusMonths(1).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusMonths(1).with(LocalTime.of(15, 0))), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You can only create reservations 2 weeks in advance"),
            reservationController.createReservation(request, temp));
    }

    @Test
    void createReservationEndBeforeStart() throws Exception {
        temp = new ReservationHolder(formatter1.format(today.plusDays(3).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(3).with(LocalTime.of(11, 0))), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The end time can not be before the start time"),
            reservationController.createReservation(request, temp));
    }

    @Test
    void createReservationEndNotOnSameDay() throws Exception {
        temp = new ReservationHolder(formatter1.format(today.plusDays(3).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(4).with(LocalTime.of(15, 0))), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The end time should be on the same day as the start time"),
            reservationController.createReservation(request, temp));
    }

    @Test
    void createReservationInvalidDate() throws Exception {
        temp = new ReservationHolder("12345", "12234453", "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please enter a date in the format : yyyy-MM-dd HH:mm"),
            reservationController.createReservation(request, temp));
    }

    @Test
    void createReservationValid() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=51")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("11:00, 20:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))), "51");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        when(reservationRepository.save(any())).thenReturn(null);

        assertEquals(ResponseEntity.status(HttpStatus.OK).body("Created the reservation succesfully"), reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=51")));
        verify(reservationRepository).save(any());
    }

    @Test
    void createReservationInvalidRoomId() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=50")
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("Building with id : 51 does not exist")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))), "50");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=50")));
    }

    @Test
    void createReservationResponseFromDifferentUrl() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=49")
            .willReturn(aResponse()
                .proxiedFrom("/dangerous/url")
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("11:00, 20:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))), "49");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=49")));
    }

    @Test
    void createReservationBuildingThrowsException() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=48")
            .willReturn(aResponse()
                .withFault(Fault.EMPTY_RESPONSE)
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("11:00, 20:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))), "48");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=48")));
    }

    @Test
    void createReservationInvalidDates() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=47")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("2020-11:00, 2020-20:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))), "47");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=47")));
    }

    @Test
    void createReservationOutsideOpeningHours() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=46")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("15:00, 16:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(16, 0))), "46");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        when(reservationRepository.save(any())).thenReturn(null);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=46")));
    }

    @Test
    void createReservationOutsideClosingHours() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=45")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("15:00, 16:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(15, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "45.00");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        when(reservationRepository.save(any())).thenReturn(null);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=45")));
    }

    @Test
    void createReservationOutsideHours() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=44")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("15:00, 16:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "44");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        when(reservationRepository.save(any())).thenReturn(null);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(1, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=44")));
    }

    @Test
    void createReservationOverlappingEqual() throws Exception {
        wm.stubFor(get("/buildingandrooms/getOpeningHours?buildingId=43")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("10:00, 20:00")));

        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(18, 0))));

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        when(reservationRepository.save(any())).thenReturn(null);
        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Please check if the reservation is inside the building's opening hours and not overlapping with other reservations"),
            reservationController.createReservation(request, temp));

        wm.verify(0, getRequestedFor(urlEqualTo("/buildingandrooms/getOpeningHours?buildingId=43")));
        verify(reservationRepository, never()).save(any());
        verify(reservationRepository, times(1)).findAllByRoomId("43");
    }

    @Test
    void noOverlappingReservationsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(18, 0)),
            today.plusDays(2).with(LocalTime.of(20, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(18, 0)));
        assertFalse(reservationController.overlappingReservations(reservation));
    }



    @Test
    void overlappingReservationsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(18, 0)),
            today.plusDays(2).with(LocalTime.of(20, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(19, 0)));
        assertTrue(reservationController.overlappingReservations(reservation));
    }

    @Test
    void overlappingReservationsStartIsBeforeNewStartTest() {

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(18, 0)),
            today.plusDays(2).with(LocalTime.of(20, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(19, 0)),
            today.plusDays(2).with(LocalTime.of(19, 0)));
        assertTrue(reservationController.overlappingReservations(reservation));
    }

    @Test
    void overlappingReservationsSameEndTimeTest() {

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(18, 0)),
            today.plusDays(2).with(LocalTime.of(17, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(17, 0)),
            today.plusDays(2).with(LocalTime.of(17, 0)));
        assertTrue(reservationController.overlappingReservations(reservation));
    }



    @Test
    void differentDaysOverlappingReservationsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(4).with(LocalTime.of(18, 0)),
            today.plusDays(4).with(LocalTime.of(20, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(19, 0)));
        assertFalse(reservationController.overlappingReservations(reservation));
    }

    @Test
    void multipleNoOverlappingReservationsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(4).with(LocalTime.of(14, 0))));
        tempList.add(new Reservation("user1", "43", today.plusDays(4).with(LocalTime.of(18, 0)),
            today.plusDays(4).with(LocalTime.of(20, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(4).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(2).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(16, 0)),
            today.plusDays(2).with(LocalTime.of(18, 0)));
        assertFalse(reservationController.overlappingReservations(reservation));
    }

    @Test
    void multipleOverlappingReservationsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(4).with(LocalTime.of(14, 0))));
        tempList.add(new Reservation("user1", "43", today.plusDays(4).with(LocalTime.of(18, 0)),
            today.plusDays(4).with(LocalTime.of(20, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(4).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(2).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));

        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(new ArrayList<Reservation>()));
        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(19, 0)));
        assertTrue(reservationController.overlappingReservations(reservation));
    }

    @Test
    void overlappingReservationsDifferentRoomsTest() {
        temp = new ReservationHolder(formatter1.format(today.plusDays(2).with(LocalTime.of(12, 0))),
            formatter1.format(today.plusDays(2).with(LocalTime.of(18, 0))), "43");

        List<Reservation> tempList = new ArrayList<Reservation>();
        List<Reservation> tempList2 = new ArrayList<Reservation>();
        tempList.add(new Reservation("admin", "43", today.plusDays(3).with(LocalTime.of(12, 0)),
            today.plusDays(4).with(LocalTime.of(14, 0))));
        tempList2.add(new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(18, 0)),
            today.plusDays(4).with(LocalTime.of(20, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(4).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));
        tempList.add(new Reservation("user2", "43", today.plusDays(3).with(LocalTime.of(15, 0)),
            today.plusDays(4).with(LocalTime.of(16, 0))));

        when(reservationRepository.findAllByRoomId("43")).thenReturn(Optional.of(tempList));
        when(reservationRepository.findAllByHostId("user1")).thenReturn(Optional.of(tempList2));

        Reservation reservation = new Reservation("user1", "43", today.plusDays(2).with(LocalTime.of(12, 0)),
            today.plusDays(2).with(LocalTime.of(19, 0)));
        assertTrue(reservationController.overlappingReservations(reservation));
    }
}

