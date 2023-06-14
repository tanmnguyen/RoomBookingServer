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
import service.buildingandroomhandlingservice.entities.RepairRequestCreation;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;

@WebMvcTest(RepairRequestController.class)
@AutoConfigureMockMvc
public class TestCreateRepairRequest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepairRequestController repairRequestController;

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

        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(1L,
            "description");

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            repairRequestController.createRepairRequest(request, repairRequestCreation));

    }

    /**
     * Test when the equipment is not found.
     */
    @Test
    void testNotFoundEquipment() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "EMPLOYEE");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request))
            .thenReturn(authenticationResponse);

        Long id = 1L;
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            "description");

        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
            repairRequestController.createRepairRequest(request, repairRequestCreation));

    }

    /**
     * Test create repair request successfully.
     */
    @Test
    void testCreateRepairRequest() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("netid", "token",
            "EMPLOYEE");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        validateMockedStatic.when(() -> Validate.validate(request))
            .thenReturn(authenticationResponse);

        Long id = 1L;
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            "description");

        long equipmentId = 1;
        Equipment equipment = new Equipment("monitor");

        Optional<Equipment> equipmentOptional = Optional.of(equipment);

        when(equipmentRepository.findById(id)).thenReturn(equipmentOptional);

        ResponseEntity response =
            repairRequestController.createRepairRequest(request, repairRequestCreation);


        // successfully operated.
        assertEquals(ResponseEntity.status(HttpStatus.OK).build().getStatusCode(),
            response.getStatusCode());


        // check if the availability has been updated into unavailable.
        assertEquals(Equipment.Availability.UNDER_MAINTENANCE, equipment.getStatus());


    }
}

