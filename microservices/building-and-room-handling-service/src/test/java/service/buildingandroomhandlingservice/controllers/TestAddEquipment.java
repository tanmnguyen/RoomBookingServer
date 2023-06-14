package service.buildingandroomhandlingservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
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
import service.buildingandroomhandlingservice.authentications.Validate;
import service.buildingandroomhandlingservice.entities.AuthenticationResponse;
import service.buildingandroomhandlingservice.entities.EquipmentCreateRequest;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;


@WebMvcTest(EquipmentController.class)
@AutoConfigureMockMvc
public class TestAddEquipment {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentController equipmentController;

    private AuthenticationResponse response =
        new AuthenticationResponse("noReservations", "pwd", "admin");


    private static MockedStatic<Validate> validateMockedStatic;

    private Gson gson = new Gson();

    /**
     * Create Rooms and Equipment to use in the tests.
     */
    @BeforeEach
    public void createRooms() {
        validateMockedStatic = Mockito.mockStatic(Validate.class);
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
    }

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private EquipmentRepository equipmentRepository;

    /**
     * Test when an unregistered user tries to add an equipment to a room.
     */
    @Test
    void addEquipmentTestUnregistered() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("monitor",
            "id");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            equipmentController.addEquipment(request, equipmentCreateRequest));
    }

    /**
     * Test when an unauthorized user tries to add an equipment to a room.
     * Boundary test: employee case.
     */
    @Test
    void addEquipmentTestUnauthorizedEmployee() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "EMPLOYEE");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("monitor",
            "id");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            equipmentController.addEquipment(request, equipmentCreateRequest));
    }

    /**
     * Test when an unauthorized user tries to add an equipment to a room.
     * Boundary test: secretary case.
     */
    @Test
    void addEquipmentTestUnauthorizedSecretary() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "SECRETARY");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("monitor",
            "id");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            equipmentController.addEquipment(request, equipmentCreateRequest));
    }

    /**
     * Test when admin add new equipment into a non existing room.
     */
    @Test
    void addEquipmentTestNonExistRoom() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        String roomId = "id";
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("monitor",
            roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            equipmentController.addEquipment(request, equipmentCreateRequest));
    }

    /**
     * Test when admin successfully adds equipment.
     */
    @Test
    void addEquipmentSuccess() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        String roomId = "id";
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("monitor",
            roomId);

        Room room = new Room(1, 1, 10,
            new ArrayList<>());

        Optional<Room> roomOptional = Optional.of(room);

        when(roomRepository.findById(roomId)).thenReturn(roomOptional);

        ResponseEntity<?> response = equipmentController
            .addEquipment(request, equipmentCreateRequest);

        assertEquals(ResponseEntity.status(HttpStatus.OK).build().getStatusCode(),
            response.getStatusCode());

        Room responseRoom = (Room) response.getBody();

        assertEquals(1, responseRoom.getEquipment().size());
        assertEquals("monitor", responseRoom.getEquipment().get(0).getType());
    }
}
