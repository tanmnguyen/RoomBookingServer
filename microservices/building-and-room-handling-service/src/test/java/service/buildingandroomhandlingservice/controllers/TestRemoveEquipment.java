package service.buildingandroomhandlingservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
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
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.EquipmentCreateRequest;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;

@WebMvcTest(EquipmentController.class)
@AutoConfigureMockMvc
public class TestRemoveEquipment {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipmentController roomController;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private EquipmentRepository equipmentRepository;

    @MockBean
    private RepairRequestRepository repairRequestRepository;

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


    /**
     * Test when an unregistered user tries to remove an equipment from a room.
     */
    @Test
    void removeEquipmentTestUnregistered() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        long equipmentId = 1;

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.removeEquipment(request, equipmentId));
    }

    /**
     * Test when an unauthorized user tries to remove an equipment from a room
     * Boundary test: employee case.
     */
    @Test
    void removeEquipmentTestUnauthorizedEmployee() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "EMPLOYEE");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        long equipmentId = 1;

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.removeEquipment(request, equipmentId));
    }

    /**
     *  Test when an unauthorized user tries to remove an equipment from a room
     * Boundary test: secretary case.
     */
    @Test
    void removeEquipmentTestUnauthorizedSecretary() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "SECRETARY");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        long equipmentId = 1;

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.removeEquipment(request, equipmentId));
    }

    /**
     * Test when admin remove a non existing equipment.
     */
    @Test
    void removeNonExistEquipmentTest() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        long equipmentId = 1;

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            roomController.removeEquipment(request, equipmentId));


    }

    /**
     * Test when admin remove an equipment which has not been assigned to any rooms.
     */
    @Test
    void removeNonExistRoomTest() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        long equipmentId = 1;
        Equipment equipment = new Equipment("monitor");

        Optional<Equipment> equipmentOptional = Optional.of(equipment);

        when(equipmentRepository.findById(equipmentId)).thenReturn(equipmentOptional);
        when(roomRepository.findRoomByEquipmentContaining(equipment)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            roomController.removeEquipment(request, equipmentId));


    }

    /**
     * Test when admin remove an equipment successfully.
     */
    @Test
    void removeEquipmentSucessTest() {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        long equipmentId = 1;
        Equipment equipment = new Equipment("monitor");

        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(equipment);

        Room room =  new Room(1, 1, 10, equipmentList);

        Optional<Equipment> equipmentOptional = Optional.of(equipment);
        Optional<Room> roomOptional = Optional.of(room);

        when(equipmentRepository.findById(equipmentId)).thenReturn(equipmentOptional);
        when(roomRepository.findRoomByEquipmentContaining(equipment)).thenReturn(roomOptional);

        ResponseEntity response = roomController.removeEquipment(request, equipmentId);

        assertEquals(ResponseEntity.status(HttpStatus.OK).build().getStatusCode(),
            response.getStatusCode());

        assertEquals(0, room.getEquipment().size());

    }
}
