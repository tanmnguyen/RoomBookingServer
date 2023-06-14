package service.reservationhandlingservice.controllers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import service.reservationhandlingservice.authentications.Validate;
import service.reservationhandlingservice.entities.AuthenticationResponse;
import service.reservationhandlingservice.entities.ResearchGroup;
import service.reservationhandlingservice.entities.Reservation;
import service.reservationhandlingservice.repositories.ResearchGroupRepository;
import service.reservationhandlingservice.repositories.ReservationRepository;


@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc
class ReservationControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationController reservationController;

    private final AuthenticationResponse authenticationResponse =
        Mockito.mock(AuthenticationResponse.class);
    private final HttpServletRequest request =
        Mockito.mock(HttpServletRequest.class);

    private final Validate validate =
        Mockito.mock(Validate.class);

    private static MockedStatic<Validate> validateMockedStatic;

    private DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
    }

    @AfterAll
    static void resetWm() {
        wm.stop();
    }


    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private ResearchGroupRepository researchGroupRepository;

    /**Checks when the validate token is wrong.
     *
     * @throws Exception exception.
     */
    @Test
    void listReservationsException() throws Exception {

        //So if the request is not validated it return a 401
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);
        //making sure that the exception comes from the catch clause.
        // And that nothing is executed afterwards.
        Mockito.verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            reservationController.listReservations(request));
    }

    /**Checks when the validate token is correct but the user has no reservations to his name.
     *
     * @throws Exception exception.
     */
    @Test
    void listReservationsNoReservations() throws Exception {

        AuthenticationResponse response =
            new AuthenticationResponse("noReservations", "pwd", "admin");


        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.ok("No reservations found"),
            reservationController.listReservations(request));
    }

    /**Checks when the validate token is correct and the user has one reservation to his name.
     *
     * @throws Exception exception.
     */
    @Test
    void listReservationsOneReservations() {
        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(a);
        Optional<List<Reservation>> opt = Optional.of(reservationList);

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);
            Mockito.when(reservationRepository.findAllByHostId(response.getNetId()))
                .thenReturn(opt);

            assertEquals(ResponseEntity.ok("All your reservations:\n"
                    + "You reserved room: 5L from " + formatter1.format(start) + " to " + formatter1.format(end) + "\n"),
                reservationController.listReservations(request));
        } catch (Exception e) {
            assertTrue("exception raised", false);
        }


    }

    /**Checks when the validate token is correct and the user has multiple reservation to his name.
     *
     * @throws Exception exception.
     */
    @Test
    void listReservationsMultipleReservations() {
        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "admin");

        Reservation a = new Reservation("admin", "4L", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Reservation b = new Reservation("admin", "5L", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));

        List reservationList = new ArrayList();
        reservationList.add(a);
        reservationList.add(b);
        Optional<List<Reservation>> opt = Optional.of(reservationList);

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);
            Mockito.when(reservationRepository.findAllByHostId(response.getNetId()))
                .thenReturn(opt);

            assertEquals(ResponseEntity.ok("All your reservations:\n"
                    + "You reserved room: 4L from " + formatter1.format(LocalDateTime.now())
                    + " to " + formatter1.format(LocalDateTime.now().plusHours(1))
                    + "\n" + "You reserved room: 5L from " + formatter1.format(LocalDateTime.now().plusDays(1))
                    + " to " + formatter1.format(LocalDateTime.now().plusDays(1).plusHours(1)) + "\n"),
                reservationController.listReservations(request));
        } catch (Exception e) {
            assertTrue("Exception raised", false);
        }

    }

    /**
     * Test endpoint for hello.
     * the first endpoint to indicate the testing for endpoints work.
     */
    @Test
    public void endpointHelloTest() {
        RequestBuilder request = MockMvcRequestBuilders.get("/reservation/hello");
        try {
            MvcResult response = mockMvc.perform(request).andReturn();
            assertEquals("hello", response.getResponse().getContentAsString());
        } catch (Exception e) {
            assertTrue("error while communicating", false);
        }
    }

    @Test
    public void listReservationsWithMvc() throws Exception {
        //So if the request is not validated it return a 401
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        // Set up the request
        mockMvc.perform(get("/myReservations")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(404));

    }

    /**
     * Edit an existing reservation to an available time-slot.
     */
    @Test
    void testEditReservation() {
        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "user");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);
        a.setId(1L);

        reservationRepository.save(a);


        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.empty());
        Mockito.when(reservationRepository.findByHostIdAndReservationId("john", 1L))
            .thenReturn(Optional.of(a));

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("Edit successful"),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }


    }

    /**
     * Edit an existing reservation to an available time-slot.
     */
    @Test
    void testEditReservationAsAdmin() {
        AuthenticationResponse response = new AuthenticationResponse("Ben", "pwd", "admin");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);
        a.setId(1L);

        reservationRepository.save(a);


        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.empty());
        Mockito.when(reservationRepository.findById(1L))
            .thenReturn(Optional.of(a));

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("Edit successful"),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }


    }

    /**
     * Edit a non existing reservation.
     */
    @Test
    void testEditReservationMissing() {
        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);
        a.setId(1L);

        reservationRepository.save(a);


        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.empty());
        Mockito.when(reservationRepository.findByHostIdAndReservationId("john", 2L))
            .thenReturn(Optional.empty());

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 2L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Edit an existing reservation to an unavailable time slot.
     */
    @Test
    void testEditReservationNotAvailable() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);
        a.setId(1L);


        reservationRepository.save(a);


        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Reservation b  = new Reservation("john", "5L", newStart, newFinish);
        a.setId(2L);

        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.of(b));
        Mockito.when(reservationRepository.findByHostIdAndReservationId("john", 2L))
            .thenReturn(Optional.of(a));

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }


    @Test
    void testEditSecretaryReservationNotAvailable() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("liam", "5L", start, end);
        a.setId(1L);
        Reservation b = new Reservation("lola", "6L", start, end);
        b.setId(2L);

        reservationRepository.save(a);
        reservationRepository.save(b);

        List<String> members = new ArrayList<>();
        members.add("lola");
        members.add("liam");

        ResearchGroup researchGroup = new ResearchGroup("john", members);
        researchGroupRepository.save(researchGroup);

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "secretary");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        List<Reservation> reservationsLiam = new ArrayList<>();
        reservationsLiam.add(a);
        List<Reservation> reservationsLola = new ArrayList<>();
        reservationsLola.add(b);

        Mockito.when(researchGroupRepository.findAllBySecretaryId("john")).thenReturn(Optional.empty());

        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.empty());

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }


    @Test
    void testEditSecretaryReservation() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("liam", "5L", start, end);
        a.setId(1L);
        Reservation b = new Reservation("lola", "6L", start, end);
        b.setId(2L);

        reservationRepository.save(a);
        reservationRepository.save(b);

        List<String> members = new ArrayList<>();
        members.add("lola");
        members.add("liam");

        ResearchGroup researchGroup = new ResearchGroup("john", members);
        researchGroupRepository.save(researchGroup);

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "secretary");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        List<Reservation> reservationsLiam = new ArrayList<>();
        reservationsLiam.add(a);
        List<Reservation> reservationsLola = new ArrayList<>();
        reservationsLola.add(b);

        Optional<List<String>> optionalMembers = Optional.of(members);
        Optional<List<Reservation>> optionalReservationsLiam = Optional.of(reservationsLiam);
        Optional<List<Reservation>> optionalReservationsLola = Optional.of(reservationsLola);

        Mockito.when(researchGroupRepository.findAllBySecretaryId("john")).thenReturn(optionalMembers);
        Mockito.when(reservationRepository.findAllByHostId("liam")).thenReturn(optionalReservationsLiam);
        Mockito.when(reservationRepository.findAllByHostId("lola")).thenReturn(optionalReservationsLola);


        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.empty());
        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("Edit successful"),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }



    @Test
    void testEditSecretaryReservationAlreadyTaken() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("liam", "5L", start, end);
        a.setId(1L);
        Reservation b = new Reservation("lola", "6L", start, end);
        b.setId(2L);

        reservationRepository.save(a);
        reservationRepository.save(b);

        List<String> members = new ArrayList<>();
        members.add("lola");
        members.add("liam");

        ResearchGroup researchGroup = new ResearchGroup("john", members);
        researchGroupRepository.save(researchGroup);

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "secretary");

        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        List<Reservation> reservationsLiam = new ArrayList<>();
        reservationsLiam.add(a);
        List<Reservation> reservationsLola = new ArrayList<>();
        reservationsLola.add(b);

        Optional<List<String>> optionalMembers = Optional.of(members);
        Optional<List<Reservation>> optionalReservationsLiam = Optional.of(reservationsLiam);
        Optional<List<Reservation>> optionalReservationsLola = Optional.of(reservationsLola);

        Mockito.when(researchGroupRepository.findAllBySecretaryId("john")).thenReturn(optionalMembers);
        Mockito.when(reservationRepository.findAllByHostId("liam")).thenReturn(optionalReservationsLiam);
        Mockito.when(reservationRepository.findAllByHostId("lola")).thenReturn(optionalReservationsLola);
        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.of(a));


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }


    @Test
    void editReservationsException() throws Exception {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        //So if the request is not validated it return a 401
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);
        //making sure that the exception comes from the catch clause.
        // And that nothing is executed afterwards.
        Mockito.verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            reservationController.editReservation(request, 1L, "5L", start, end));
    }

    @Test
    void testEditSecretaryReservationForHerself() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("john", "5L", start, end);
        a.setId(1L);

        reservationRepository.save(a);

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "secretary");

        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        Mockito.when(researchGroupRepository.findAllBySecretaryId("john")).thenReturn(Optional.empty());

        Mockito.when(reservationRepository.findByHostIdAndReservationId("john", 1L)).thenReturn(Optional.of(a));

        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.of(a));

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }




    @Test
    void testEditSecretaryReservationNoReservationInHerGroup() {

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Reservation a  = new Reservation("liam", "5L", start, end);
        a.setId(1L);
        Reservation b = new Reservation("lola", "6L", start, end);
        b.setId(2L);

        reservationRepository.save(a);
        reservationRepository.save(b);

        List<String> members = new ArrayList<>();
        members.add("lola");
        members.add("liam");

        ResearchGroup researchGroup = new ResearchGroup("john", members);
        researchGroupRepository.save(researchGroup);

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "secretary");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        List<Reservation> reservationsLiam = new ArrayList<>();
        reservationsLiam.add(a);
        List<Reservation> reservationsLola = new ArrayList<>();
        reservationsLola.add(b);

        Optional<List<String>> optionalMembers = Optional.of(members);


        Mockito.when(researchGroupRepository.findAllBySecretaryId("john")).thenReturn(optionalMembers);
        Mockito.when(reservationRepository.findAllByHostId("liam")).thenReturn(Optional.empty());
        LocalDateTime newStart = start.plusHours(2);
        LocalDateTime newFinish = end.plusHours(3);
        Mockito.when(reservationRepository.findByRoomIdAndStartTimeAndEndTime("5L", newStart, newFinish))
            .thenReturn(Optional.of(a));

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
                reservationController.editReservation(request, 1L, "5L", newStart, newFinish));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }

    /**
     * Check two overlapping reservations.
     */
    @Test
    void testCheckOverlappingReservations() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "52";
        String netId = "john";
        Reservation a  = new Reservation("john", "52", start, end);
        Reservation b  = new Reservation("john", "52", start, end);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(a);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(false,
                reservationController.checkReservation(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Check valid reservation.
     */
    @Test
    void testCheckValidReservation() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "4.1";
        String netId = "net";
        //Reservation a  = new Reservation("john", "5.2", start, end);
        Reservation b  = new Reservation(netId, buildingId, start, end);
        List<Reservation> reservationList = new ArrayList<>();


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        wm.stubFor(WireMock.get("/buildingandrooms/getOpeningHours?buildingId=4")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("07:00, 19:00")));

        AuthenticationResponse response = new AuthenticationResponse(netId, "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertFalse(reservationController.overlappingReservations(b));
            assertEquals(true,
                reservationController.checkReservation(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Check two overlapping reservations.
     */
    @Test
    void testOverlappingReservations() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "52";
        String netId = "john";
        Reservation a  = new Reservation("john", "52", start, end);
        Reservation b  = new Reservation("john", "52", start, end);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(a);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertTrue(reservationController.overlappingReservations(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Check a reservation for overlapping when there are no other reservations.
     */
    @Test
    void testOverlappingReservationsEmpty() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "52";
        String netId = "john";
        List<Reservation> reservationList = new ArrayList<>();
        Reservation b  = new Reservation(netId, buildingId, start, end);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertFalse(
                reservationController.overlappingReservations(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }

    /**
     * Check a reservation for overlapping when there are no other reservations by the host.
     */
    @Test
    void testOverlappingReservationsEmptyHost() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "52";
        String netId = "john";
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(new Reservation("1", buildingId, start, end));
        Reservation b  = new Reservation(netId, buildingId, start, end);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId("1"))
            .thenReturn(Optional.of(new ArrayList<>()));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertTrue(
                reservationController.overlappingReservations(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }
    }

    /**
     * Check reservation outside building hours.
     */
    @Test
    void testCheckReservationOutsideHours() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "5.2";
        String netId = "john";
        List<Reservation> reservationList = new ArrayList<>();
        Reservation b  = new Reservation(netId, buildingId, start, end);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        wm.stubFor(WireMock.get("/buildingandrooms/getOpeningHours?buildingId=5")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("08:00, 09:00")));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertFalse(reservationController.checkReservation(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Check two overlapping reservations with the same end time, but different start times.
     */
    @Test
    void testOverlappingReservationsSameEnd() {
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        String buildingId = "5.2";
        String netId = "john";
        Reservation a  = new Reservation("alice", "5.2", start, end);
        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(a);
        Reservation b  = new Reservation(netId, buildingId, start, end);


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertTrue(reservationController.overlappingReservations(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    /**
     * Check a reservation that starts before the opening time and ends after.
     */
    @Test
    void testCheckReservationOutsideBothHours() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        String buildingId = "4.1";
        String netId = "net";
        //Reservation a  = new Reservation("john", "5.2", start, end);
        Reservation b  = new Reservation(netId, buildingId, start, end);
        List<Reservation> reservationList = new ArrayList<>();


        Mockito.when(reservationRepository.findAllByRoomId(buildingId))
            .thenReturn(Optional.of(reservationList));

        Mockito.when(reservationRepository.findAllByHostId(netId))
            .thenReturn(Optional.of(reservationList));

        wm.stubFor(WireMock.get("/buildingandrooms/getOpeningHours?buildingId=4")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("10:00, 11:00")));

        AuthenticationResponse response = new AuthenticationResponse(netId, "pwd", "admin");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);


        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(false,
                reservationController.checkReservation(b));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }



}


