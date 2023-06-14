package service.buildingandroomhandlingservice.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.entities.Status;
import service.buildingandroomhandlingservice.filters.SearchQuery;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;


@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc
public class RoomControllerTest {
    private Room roomA;
    private Room roomB;
    private Room roomC;
    private Room roomD;
    private Room roomE;
    private Room roomF;
    private Room roomG;
    private Room roomH;

    private List<Equipment> equipmentA;
    private List<Equipment> equipmentB;
    private List<Equipment> equipmentC;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomController roomController;

    private AuthenticationResponse response =
        new AuthenticationResponse("noReservations", "pwd", "admin");

    private final AuthenticationResponse authenticationResponse =
        Mockito.mock(AuthenticationResponse.class);
    private final HttpServletRequest request =
        Mockito.mock(HttpServletRequest.class);

    private final Validate validate =
        Mockito.mock(Validate.class);

    private static MockedStatic<Validate> validateMockedStatic;


    @BeforeEach
    void init() {
        validateMockedStatic = Mockito.mockStatic(Validate.class);
    }

    /**
     * Create Rooms and Equipment to use in the tests.
     */
    @BeforeEach
    public void createRooms() {
        equipmentA = new ArrayList<>();
        equipmentB = new ArrayList<>();
        equipmentC = new ArrayList<>();

        equipmentA.add(new Equipment(0L, "Whiteboard"));
        equipmentA.add(new Equipment(1L, "Projector"));
        equipmentB.add(new Equipment(0L, "Whiteboard"));
        equipmentB.add(new Equipment(1L, "Projector"));
        equipmentB.add(new Equipment(2L, "Computer"));
        equipmentC.add(new Equipment(3L, "Camera"));
        equipmentC.add(new Equipment(4L, "Pencil Sharpener"));

        roomA = new Room(51, 16, 4, equipmentA); // Generic room
        roomB = new Room(52, 17, 4, equipmentA); // Room with a different roomNumber than room A
        roomC = new Room(53, 16, 3, equipmentA); // Room with less capacity than room A
        roomD = new Room(54, 16, 4, equipmentB); // Room with equipment proper superset of room A
        roomE = new Room(55, 16, 4, equipmentC); // Room with no intersection between it's equipment and that of room A
        roomF = new Room(56, 16, 4, equipmentA); // Room similar to room A which is under maintenance
        roomG = new Room(51, 17, 4, equipmentA); // Room with the same roomNumber and a different buildingNumber as room A
        roomH = new Room(51, 16, 4, equipmentA); // A duplicate of room A

        roomF.setStatus(Status.UNDER_MAINTENANCE);
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


    /**Checks when the validate token is wrong.
     *
     * @throws Exception exception.
     */
    @Test
    public void listAuthenticationException() throws Exception {

        //So if the request is not validated it return a 401
        validateMockedStatic.when(() -> Validate.validate(request)).thenThrow(Exception.class);
        //making sure that the exception comes from the catch clause.
        // And that nothing is executed afterwards.
        Mockito.verify(authenticationResponse, never()).getNetId();

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
            roomController.searchRooms(new SearchQuery(), request));
    }

    //test for adding a new room
    @Test
    public void testAddRoom() {
        //roomRepository = Mockito.mock(RoomRepository.class);
        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);

        List<Room> roomList = new ArrayList<>();
        roomList.add(room);

        when(roomRepository.save(any()))
            .thenReturn(null);
        //Mockito.when(roomRepository.save(room)).thenReturn(room);
        Room testRoom = roomController.addRoom(room);

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            Mockito.verify(roomRepository).save(room);
            assertEquals(room, testRoom);
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    //get an existing room
    @Test
    public void testGetRoom() {
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);
        roomRepository.save(room);

        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");



        List<Room> roomList = new ArrayList<>();
        roomList.add(room);

        when(roomRepository.findById("1.3"))
            .thenReturn(Optional.of(room));
        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.ACCEPTED).body("Succes!" + room),
                roomController.getRoom("1.3"));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    //get a non-existing room
    @Test
    public void testGetRoomNotFound() {
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);
        roomRepository.save(room);

        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");



        List<Room> roomList = new ArrayList<>();
        roomList.add(room);

        when(roomRepository.findById("1.3"))
            .thenReturn(Optional.of(room));
        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("no room with given id"),
                roomController.getRoom("2.3"));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }


    }

    //view all rooms
    @Test
    public void testGetAllRooms() {
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);
        roomRepository.save(room);

        Room room2 = new Room(2, 3, 30, equipment);
        roomRepository.save(room2);

        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");



        List<Room> roomList = new ArrayList<>();
        roomList.add(room);
        roomList.add(room2);

        when(roomRepository.findAll())
            .thenReturn(roomList);

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(roomList, roomController.getAllRooms());

            Mockito.verify(roomRepository).findAll();
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    //change the status of an existing room
    @Test
    public void testChangeStatus() {
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);
        roomRepository.save(room);
        room.setStatus(Status.UNDER_MAINTENANCE);

        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");


        when(roomRepository.findById("1.3"))
            .thenReturn(Optional.of(room));
        when(roomRepository.save(any()))
            .thenReturn(null);
        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.ACCEPTED).body("Succes!" + room),
                roomController.changeStatus("1.3", Status.UNDER_MAINTENANCE));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }

    //change the status of a non-existing room
    @Test
    public void testChangeStatusError() {
        List<Equipment> equipment = new ArrayList<Equipment>();
        equipment.add(new Equipment(1L, "board"));
        Room room = new Room(1, 3, 30, equipment);
        roomRepository.save(room);
        room.setStatus(Status.UNDER_MAINTENANCE);

        //AuthenticationResponse response = new AuthenticationResponse("john", "pwd", "admin");


        when(roomRepository.findById("1.3"))
            .thenReturn(Optional.of(room));
        when(roomRepository.save(any()))
            .thenReturn(null);
        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("no room with given id"),
                roomController.changeStatus("2.3", Status.UNDER_MAINTENANCE));
        } catch (Exception e1) {
            assertTrue("exception raised: " + e1, false);
        }

    }


    //Test if searchRoom finds the correct room based on building number
    @Test
    public void searchRoomBuildingNumberTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomB);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.add(roomA);
        resultList.add(roomC);

        SearchQuery searchQuery = new SearchQuery(16, new ArrayList<Equipment>(), 0, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            validateMockedStatic.when(() -> Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom finds the correct room based on capacity
    @Test
    public void searchRoomCapacityTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomB);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.add(roomA);
        resultList.add(roomB);

        SearchQuery searchQuery = new SearchQuery(-1, new ArrayList<Equipment>(), 4, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom finds the correct room based on equipment
    @Test
    public void searchRoomEquipmentTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomB);
        roomList.add(roomC);
        roomList.add(roomD);

        List<Room> resultListA = new ArrayList<>();
        resultListA.addAll(roomList);

        roomList.add(roomE);

        List<Room> resultListB = new ArrayList<>();
        resultListB.add(roomD);

        SearchQuery searchQueryA = new SearchQuery(-1, equipmentA, 0, Status.AVAILABLE);
        SearchQuery searchQueryB = new SearchQuery(-1, equipmentB, 0, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultListA),
                roomController.searchRooms(searchQueryA, request));
            assertEquals(ResponseEntity.ok("succes!" + resultListB),
                roomController.searchRooms(searchQueryB, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom finds the correct room based on status
    @Test
    public void searchRoomStatusTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomB);
        roomList.add(roomC);
        roomList.add(roomF);

        List<Room> resultListA = new ArrayList<>();
        resultListA.add(roomA);
        resultListA.add(roomB);
        resultListA.add(roomC);

        List<Room> resultListB = new ArrayList<>();
        resultListB.add(roomF);

        SearchQuery searchQueryA = new SearchQuery(-1, new ArrayList<Equipment>(), 0, Status.AVAILABLE);
        SearchQuery searchQueryB = new SearchQuery(-1, new ArrayList<Equipment>(), 0, Status.UNDER_MAINTENANCE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultListA),
                roomController.searchRooms(searchQueryA, request));
            assertEquals(ResponseEntity.ok("succes!" + resultListB),
                roomController.searchRooms(searchQueryB, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom finds the correct room based on status
    @Test
    public void searchRoomNoSearchQueryTest() {


        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not read Search Query"),
                roomController.searchRooms(null, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }


    //Test if searchRoom finds the correct room if multiple rooms in different buildings have the same room number
    @Test
    public void searchRoomDifferentBuildingNumberSameRoomNumberTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomG);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.addAll(roomList);

        SearchQuery searchQuery = new SearchQuery(-1, new ArrayList<Equipment>(), 0, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    @Test
    public void searchRoomDifferentCapacityTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomG);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.addAll(roomList);

        SearchQuery searchQuery = new SearchQuery(-1, new ArrayList<Equipment>(), -1, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    @Test
    public void searchRoomDifferentStatusTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomG);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.addAll(roomList);

        SearchQuery searchQuery = new SearchQuery(-1, new ArrayList<Equipment>(), 0, Status.UNDETERMINED);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom finds the correct room if a room exists twice
    @Test
    public void searchDuplicateRoomTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomH);
        roomList.add(roomC);

        List<Room> resultList = new ArrayList<>();
        resultList.addAll(roomList);

        SearchQuery searchQuery = new SearchQuery(16, new ArrayList<Equipment>(), 0, Status.AVAILABLE);

        when(roomRepository.findAll()).thenReturn(roomList);

        try {
            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + resultList),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }

    //Test if searchRoom works correctly if there are no results
    @Test
    public void searchNoRoomsTest() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(roomA);
        roomList.add(roomB);
        roomList.add(roomC);

        SearchQuery searchQuery = new SearchQuery(16, new ArrayList<Equipment>(), 6, Status.AVAILABLE);

        try {
            when(roomRepository.findAll()).thenReturn(roomList);

            when(Validate.validate(request)).thenReturn(response);

            assertEquals(ResponseEntity.ok("succes!" + new ArrayList<Room>()),
                roomController.searchRooms(searchQuery, request));

            when(roomRepository.findAll()).thenReturn(new ArrayList<Room>());

            assertEquals(ResponseEntity.ok("succes!" + new ArrayList<Room>()),
                roomController.searchRooms(searchQuery, request));
        } catch (Exception e) {
            assertTrue("exception raised: " + e, false);
        }
    }
}
