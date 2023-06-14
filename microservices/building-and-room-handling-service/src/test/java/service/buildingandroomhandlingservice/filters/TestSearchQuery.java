package service.buildingandroomhandlingservice.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Status;

public class TestSearchQuery {


    @Test
    void testHasBuildingNumber() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery();

        assertTrue(searchQuery.hasBuildingNumber());
        assertFalse(searchQuery1.hasBuildingNumber());
    }

    @Test
    void testBuildingNumberGetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery(2, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getBuildingNumber() == 1);
        assertTrue(searchQuery1.getBuildingNumber() == 2);
    }

    @Test
    void testBuildingNumberSetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getBuildingNumber() == 1);

        searchQuery.setBuildingNumber(2);
        assertTrue(searchQuery.getBuildingNumber() == 2);
    }

    @Test
    void testHasEquipment() {
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("whiteboard"));
        SearchQuery searchQuery = new SearchQuery(1, equipment,
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery();
        SearchQuery searchQuery2 = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.hasEquipment());
        assertFalse(searchQuery1.hasEquipment());
        assertFalse(searchQuery2.hasEquipment());
    }

    @Test
    void testEquipmentGetter() {
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("whiteboard"));
        SearchQuery searchQuery = new SearchQuery(1, equipment,
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery();
        SearchQuery searchQuery2 = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getEquipment().equals(equipment));
        assertTrue(searchQuery1.getEquipment() == null);
        assertTrue(searchQuery2.getEquipment().equals(new ArrayList<>()));
    }

    @Test
    void testEquipmentSetter() {
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("whiteboard"));
        SearchQuery searchQuery = new SearchQuery(1, equipment,
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getEquipment().equals(equipment));

        equipment.add(new Equipment("blackboard"));
        searchQuery.setEquipment(equipment);

        assertTrue(searchQuery.getEquipment().equals(equipment));
    }

    /**
     * Test when the capacity is available for booking.
     */
    @Test
    void testHasCapacity() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.hasCapacity());
    }

    @Test
    void testCapacityGetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery(2, new ArrayList<>(),
            20, Status.AVAILABLE);

        assertTrue(searchQuery.getCapacity() == 10);
        assertTrue(searchQuery1.getCapacity() == 20);
    }

    @Test
    void testCapacitySetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getCapacity() == 10);

        searchQuery.setCapacity(20);
        assertTrue(searchQuery.getCapacity() == 20);
    }

    /**
     * Test when the capacity is not available for booking.
     */
    @Test
    void testNoCapacity() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            -1, Status.AVAILABLE);

        assertFalse(searchQuery.hasCapacity());
    }

    /**
     * Test when the status is set.
     */
    @Test
    void testHasStatus() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.hasStatus());
    }

    @Test
    void testStatusGetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);
        SearchQuery searchQuery1 = new SearchQuery(2, new ArrayList<>(),
            10, Status.UNDER_MAINTENANCE);

        assertTrue(searchQuery.getStatus() == Status.AVAILABLE);
        assertTrue(searchQuery1.getStatus() == Status.UNDER_MAINTENANCE);
    }

    @Test
    void testStatusSetter() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertTrue(searchQuery.getStatus() == Status.AVAILABLE);

        searchQuery.setStatus(Status.UNDER_MAINTENANCE);
        assertTrue(searchQuery.getStatus() == Status.UNDER_MAINTENANCE);
    }

    /**
     * Test when the status is not set.
     */
    @Test
    void testNotStatus() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.UNDETERMINED);

        assertFalse(searchQuery.hasStatus());
    }

    @Test
    void testEquals() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.UNDETERMINED);
        SearchQuery searchQuery1 = new SearchQuery(1, new ArrayList<>(),
            10, Status.UNDETERMINED);

        assertEquals(searchQuery, searchQuery1);
        assertEquals(searchQuery, searchQuery1);
        assertEquals(searchQuery.hashCode(), searchQuery1.hashCode());
    }

    @Test
    void testNotEquals() {
        ArrayList<Equipment> equipment = new ArrayList<>();
        equipment.add(new Equipment("Whiteboard"));
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.UNDETERMINED);
        SearchQuery searchQuery1 = new SearchQuery(2, new ArrayList<>(),
            10, Status.UNDETERMINED);
        SearchQuery searchQuery2 = new SearchQuery(1, equipment,
            10, Status.UNDETERMINED);
        SearchQuery searchQuery3 = new SearchQuery(1, new ArrayList<>(),
            10, Status.AVAILABLE);

        assertNotEquals(searchQuery, searchQuery1);
        assertNotEquals(searchQuery, searchQuery2);
        assertNotEquals(searchQuery, searchQuery3);
        assertNotEquals(searchQuery.hashCode(), searchQuery1.hashCode());
        assertNotEquals(searchQuery.hashCode(), searchQuery2.hashCode());
        assertNotEquals(searchQuery.hashCode(), searchQuery3.hashCode());
    }

    @Test
    void testToString() {
        SearchQuery searchQuery = new SearchQuery(1, new ArrayList<>(),
            10, Status.UNDETERMINED);

        assertEquals(searchQuery.toString(), "SearchQuery(buildingNumber=1, equipment=[], capacity=10, status=UNDETERMINED)");
    }
}
