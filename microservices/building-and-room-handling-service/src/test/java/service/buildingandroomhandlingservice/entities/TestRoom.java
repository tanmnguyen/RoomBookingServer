package service.buildingandroomhandlingservice.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestRoom {
    @Test
    void constructor() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        assertEquals("1.2", room.getId());
        assertEquals(1, room.getRoomNumber());
        assertEquals(2, room.getBuildingNumber());
        assertEquals(30, room.getCapacity());
        assertEquals(equipment, room.getEquipment());
        Status status = Status.AVAILABLE;
        assertEquals(status, room.getStatus());
    }


    @Test
    void testSetBuildingNumber() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        room.setBuildingNumber(5);
        assertEquals("1.5", room.getId());
        assertEquals(5, room.getBuildingNumber());
    }

    @Test
    void testSetRoomNumber() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        room.setRoomNumber(19);
        assertEquals("19.2", room.getId());
        assertEquals(19, room.getRoomNumber());
    }

    @Test
    void testSetCapacity() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        room.setCapacity(19);
        assertEquals(19, room.getCapacity());
    }

    @Test
    void testSetEquipment() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        List<Equipment> newEquipment = new ArrayList<>();
        newEquipment.add(new Equipment("computers"));
        room.setEquipment(newEquipment);
        assertEquals(newEquipment, room.getEquipment());
    }

    @Test
    void testSetStatus() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        room.setStatus(Status.UNDER_MAINTENANCE);
        assertEquals(Status.UNDER_MAINTENANCE, room.getStatus());
    }

    @Test
    void testSetId() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));
        Room room = new Room(1, 2, 30, equipment);
        room.setId("1.3");
        assertEquals("1.3", room.getId());
    }

    @Test
    void equalsTest() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("board"));
        equipment.add(new Equipment("projector"));

        List<Equipment> equipmentForRoom6 = new ArrayList<>();
        equipmentForRoom6.add(new Equipment("screen"));

        Room room1 = new Room(1, 2, 30, equipment); //Base
        assertEquals(room1, room1);
        assertNotEquals(room1, "Type String");

        Room room2 = new Room(2, 2, 30, equipment); //Diff room number
        assertNotEquals(room1, room2);

        Room room3 = new Room(1, 2, 30, equipment); //Same
        assertEquals(room1, room3);

        Room room4 = new Room(1, 3, 30, equipment); //Diff  building nb
        assertNotEquals(room1, room4);

        Room room5 = new Room(1, 2, 40, equipment); //Diff capacity
        assertNotEquals(room1, room5);

        Room room6 = new Room(1, 2, 30, equipmentForRoom6); //Diff equipment
        assertNotEquals(room1, room6);

        Room room7 = new Room(1, 2, 30, equipment); //Diff availability.
        room7.setStatus(Status.UNDER_MAINTENANCE);
        assertNotEquals(room1, room7);
    }

    @Test
    void toStringTest() {
        List<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment(1L, "board"));
        equipment.add(new Equipment(2L, "projector"));
        Room room = new Room(1, 2, 30, equipment);
        assertEquals("Room{id=1.2, buildingNumber=2, roomNumber=1, capacity=30, equipment=[Equipment{id=1, type='board'}"
            + ", Equipment{id=2, type='projector'}], status=AVAILABLE}", room.toString());
    }

    @Test
    void testHashCode() {
        List<Equipment> equipment = new ArrayList<>();
        Room room = new Room(1, 2, 30, equipment);
        assertNotNull(room.hashCode());
    }

}
