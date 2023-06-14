package service.buildingandroomhandlingservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
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
import service.buildingandroomhandlingservice.entities.EquipmentStatus;
import service.buildingandroomhandlingservice.entities.RepairRequestCreation;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;

@WebMvcTest(EquipmentController.class)
@AutoConfigureMockMvc
public class TestUpdateEquipmentStatus {
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
     * Unregistered users must not be able to make this operation.
     */
    @Test
    void testUnregisteredAttempt() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.updateEquipmentStatus(request, equipmentStatus));

    }

    /**
     * Unauthorized users cannot make this operation.
     * Boundary test: Normal employee case.
     */
    @Test
    void testUnauthorizedEmployee() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "EMPLOYEE");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.updateEquipmentStatus(request, equipmentStatus));

    }

    /**
     * Unauthorized users cannot make this operation.
     * Boundary test: secretary case.
     */
    @Test
    void testUnauthorizedSecretary() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "SECRETARY");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.updateEquipmentStatus(request, equipmentStatus));

    }

    /**
     * Test the case where the equipment is not found.
     */
    @Test
    void testEquipmentNotFound() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        Long id = 1L;
        EquipmentStatus equipmentStatus = new EquipmentStatus(id, "AVAILABLE");

        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());


        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            roomController.updateEquipmentStatus(request, equipmentStatus));

    }

    /**
     * Test update the equipment availability to under_maintenance.
     */
    @Test
    void testEquipmentUpdateUnderMaintenance() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        Long id = 1L;
        EquipmentStatus equipmentStatus = new EquipmentStatus(id, "UNDER_MAINTENANCE");

        Equipment equipment = new Equipment("monitor");
        equipment.setStatus(Equipment.Availability.AVAILABLE);

        Optional<Equipment> equipmentOptional = Optional.of(equipment);

        when(equipmentRepository.findById(id)).thenReturn(equipmentOptional);

        assertEquals(ResponseEntity.status(HttpStatus.OK).build().getStatusCode(),
            roomController.updateEquipmentStatus(request, equipmentStatus).getStatusCode());

        assertEquals(Equipment.Availability.UNDER_MAINTENANCE, equipment.getStatus());

    }

    /**
     * Test update the equipment availability to available.
     */
    @Test
    void testEquipmentUpdateAvailable() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        Long id = 1L;
        EquipmentStatus equipmentStatus = new EquipmentStatus(id, "AVAILABLE");

        Equipment equipment = new Equipment("monitor");
        equipment.setStatus(Equipment.Availability.UNDER_MAINTENANCE);

        Optional<Equipment> equipmentOptional = Optional.of(equipment);

        when(equipmentRepository.findById(id)).thenReturn(equipmentOptional);

        assertEquals(ResponseEntity.status(HttpStatus.OK).build().getStatusCode(),
            roomController.updateEquipmentStatus(request, equipmentStatus).getStatusCode());

        assertEquals(Equipment.Availability.AVAILABLE, equipment.getStatus());

    }

    /**
     * Test update the equipment availability to a non-existing status.
     * The controller must return bad-request.
     */
    @Test
    void testEquipmentUpdateNonExistStatus() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "ADMIN");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(authenticationResponse);

        Long id = 1L;
        EquipmentStatus equipmentStatus = new EquipmentStatus(id, "!$@!#2");

        Equipment equipment = new Equipment("monitor");
        equipment.setStatus(Equipment.Availability.UNDER_MAINTENANCE);

        Optional<Equipment> equipmentOptional = Optional.of(equipment);

        when(equipmentRepository.findById(id)).thenReturn(equipmentOptional);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode(),
            roomController.updateEquipmentStatus(request, equipmentStatus).getStatusCode());


    }

}
