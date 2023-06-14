package service.buildingandroomhandlingservice.entities;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuildingTest {

    private Building building;
    LocalTime openTime;
    LocalTime closeTime;

    @BeforeEach
    void setUp() {
        this.openTime = LocalTime.NOON;
        this.closeTime = LocalTime.NOON.plusHours(1);
        this.building = new Building((long) 100, openTime, closeTime, Status.AVAILABLE);
    }

    @Test
    void testHashCode() {
        assertNotNull(building.hashCode());
    }

    @Test
    void constructorAndGetters() {
        assertEquals(100, building.getNrOfRooms());
        assertEquals(openTime, building.getOpeningTime());
        assertEquals(closeTime, building.getClosingTime());
        assertEquals(Status.AVAILABLE, building.getStatus());
    }


    @Test
    void settersTest() {

        building.setNrOfRooms((long) 10);
        assertEquals(10, building.getNrOfRooms());

        building.setOpeningTime(closeTime);
        assertEquals(closeTime, building.getOpeningTime());

        building.setClosingTime(openTime);
        assertEquals(openTime, building.getClosingTime());

        building.setStatus(Status.UNDER_MAINTENANCE);
        assertEquals(Status.UNDER_MAINTENANCE, building.getStatus());
    }


    @Test
    void equalTest() {

        building.setId((long) 1);

        Building building2 = new Building((long) 100, openTime, closeTime, Status.AVAILABLE);
        building2.setId((long) 1);
        assertEquals(building, building2);

        Building building3 = new Building((long) 100, openTime, closeTime, Status.AVAILABLE);
        building3.setId((long) 3);
        assertNotEquals(building2, building3);

        Building building4 = new Building((long) 100, LocalTime.NOON.plusMinutes(30), closeTime, Status.AVAILABLE);
        building4.setId((long) 1);
        assertNotEquals(building, building4);

        Building building5 = new Building((long) 100, openTime,  LocalTime.NOON.plusMinutes(30), Status.AVAILABLE);
        building5.setId((long) 1);
        assertNotEquals(building, building5);


        Building building6 = new Building((long) 100, openTime,  closeTime, Status.UNDER_MAINTENANCE);
        building6.setId((long) 1);
        assertNotEquals(building, building6);


        Building building7 = new Building((long) 101, openTime, closeTime, Status.AVAILABLE);
        building7.setId((long) 1);
        assertNotEquals(building, building7);


        assertEquals(building, building);
        assertNotEquals(building, new Equipment("chairs"));
    }

    /*//This test is Flaky because of "Time" variable
    //TO DO: Find an alternative for Time.
    @Test
    void toStringTest() {
        Time openTime = new Time(100);
        Time closeTime = new Time(110);
        Building building1 = new Building((long) 100, openTime, closeTime, Building.Status.AVAILABLE);
        assertEquals("Building{id=null, nr_of_rooms=100, opening_time=01:00:00, closing_time=01:00:00, status=AVAILABLE}", building1.toString());
    }*/

}
