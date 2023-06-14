package service.buildingandroomhandlingservice.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
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
import service.buildingandroomhandlingservice.entities.Building;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.entities.Status;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;



@WebMvcTest(StatusController.class)
@AutoConfigureMockMvc
public class StatusControllerTest {

    @Autowired
    private StatusController statusController;

    @Autowired
    private MockMvc mockMvc;


    private final AuthenticationResponse authenticationResponse =
        Mockito.mock(AuthenticationResponse.class);
    private HttpServletRequest request =
        Mockito.mock(HttpServletRequest.class);

    private Validate validate =
        Mockito.mock(Validate.class);

    private static MockedStatic<Validate> validateMockedStatic;

    private Building b1;
    private long buildingId = 5;
    private LocalTime openTime = LocalTime.MIDNIGHT;
    private LocalTime closeTime = LocalTime.NOON;

    @BeforeEach
    void init() {
        b1 = new Building((long) 1, (long) 100, LocalTime.NOON, LocalTime.NOON.plusMinutes(10), Status.AVAILABLE);
        validateMockedStatic = Mockito.mockStatic(Validate.class);
    }

    @AfterEach
    void reset() {
        validateMockedStatic.close();
    }

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private EquipmentRepository equipmentRepository;

    @MockBean
    private RepairRequestRepository repairRequestRepository;


    @Test
    void unauthChangeStatusTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        String netId = verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), statusController.changeStatus(request, 1, "AVAILABLE"));
    }

    @Test
    void nonAdminChangeStatusTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "EMPLOYEE");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), statusController.changeStatus(request, 1, "AVAILABLE"));
    }

    @Test
    void nonsenseStatusTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(), statusController.changeStatus(request, 1, "test"));
    }

    @Test
    void nonExistingBuildingChangeStatusTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        Optional<Building> optionalBuilding = Optional.empty();
        when(buildingRepository.findById((long) 1)).thenReturn(optionalBuilding);
        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build(), statusController.changeStatus(request, 1, "AVAILABLE"));
    }

    @Test
    void successChangeStatusTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        Optional<Building> optionalBuilding = Optional.of(b1);
        when(buildingRepository.findById(b1.getId())).thenReturn(optionalBuilding);

        List<Equipment> equipmentA = new ArrayList<>();
        equipmentA.add(new Equipment(0L, "Whiteboard"));
        equipmentA.add(new Equipment(1L, "Projector"));
        Room roomA = new Room(51, 1, 4, equipmentA);
        Room roomB = new Room(52, 1, 4, equipmentA);
        List<Room> rooms  = new ArrayList<>();
        rooms.add(roomA);
        rooms.add(roomB);
        Optional<List<Room>> optionalRooms = Optional.of(rooms);
        validateMockedStatic.when(() -> roomRepository.findByBuildingNumber(1)).thenReturn(optionalRooms);

        assertEquals(ResponseEntity.ok("Success"), statusController.changeStatus(request, 1, "AVAILABLE"));
        verify(buildingRepository).save(any());
        verify(roomRepository).save(roomA);
        verify(roomRepository).save(roomB);
    }


    @Test
    void successChangeStatusNoRoomsTest() {
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);

        AuthenticationResponse response = new AuthenticationResponse("admin", "pwd", "ADMIN");
        validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

        Optional<Building> optionalBuilding = Optional.of(b1);
        when(buildingRepository.findById(b1.getId())).thenReturn(optionalBuilding);


        validateMockedStatic.when(() -> roomRepository.findByBuildingNumber(1)).thenReturn(Optional.empty());

        assertEquals(ResponseEntity.ok("Success"), statusController.changeStatus(request, 1, "AVAILABLE"));
        verify(buildingRepository).save(any());
    }

}
