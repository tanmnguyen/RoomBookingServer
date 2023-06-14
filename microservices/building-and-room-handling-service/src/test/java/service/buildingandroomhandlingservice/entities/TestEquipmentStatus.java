package service.buildingandroomhandlingservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestEquipmentStatus {
    /**
     * Test constructors.
     */
    @Test
    void testConstructor() {
        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");
        assertNotNull(equipmentStatus);

        equipmentStatus = new EquipmentStatus();
        assertNotNull(equipmentStatus);
    }

    /**
     * Test get methods.
     */
    @Test
    void testGetMethods() {
        Long id = 1L;
        String availability = "AVAILABLE";
        EquipmentStatus equipmentStatus = new EquipmentStatus(id, availability);

        assertEquals(id, equipmentStatus.getId());
        assertEquals(availability, equipmentStatus.getAvailability());
    }

    /**
     * Test equals when comparing the same object.
     */
    @Test
    void testEqualSameObject() {
        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(equipmentStatus, equipmentStatus);
    }

    /**
     * Test not equal when comparing with null.
     */
    @Test
    void testNotEqualsNull() {
        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        assertNotEquals(null, equipmentStatus);
        assertNotEquals(equipmentStatus, null);
    }

    /**
     * Test not equal when comparing with a different type.
     */
    @Test
    void testNotEqualsDifferentType() {
        EquipmentStatus equipmentStatus = new EquipmentStatus(1L, "AVAILABLE");

        String object = "something different";
        assertNotEquals(object, equipmentStatus);
        assertNotEquals(equipmentStatus, object);
    }

    /**
     * Test not equal when comparing 2 object with different first attribute.
     */
    @Test
    void testEqualsDifferentFirstAttribute() {
        EquipmentStatus equipmentStatus1 = new EquipmentStatus(1L, "AVAILABLE");

        EquipmentStatus equipmentStatus2 = new EquipmentStatus(2L, "AVAILABLE");

        assertNotEquals(equipmentStatus1, equipmentStatus2);
    }

    /**
     * Test not equal when comparing 2 object with different second attribute.
     */
    @Test
    void testEqualsDifferentSecondAttribute() {
        EquipmentStatus equipmentStatus1 = new EquipmentStatus(1L, "UNDER_MAINTENANCE");

        EquipmentStatus equipmentStatus2 = new EquipmentStatus(1L, "AVAILABLE");

        assertNotEquals(equipmentStatus1, equipmentStatus2);
    }

    /**
     * Test equal when comparing 2 object with same attributes.
     */
    @Test
    void testEqualsSameAttribute() {
        EquipmentStatus equipmentStatus1 = new EquipmentStatus(1L, "AVAILABLE");

        EquipmentStatus equipmentStatus2 = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(equipmentStatus1, equipmentStatus2);
    }

    /**
     * Test different object must have different hashcode.
     */
    @Test
    void testHashCodeNotEqual() {
        EquipmentStatus equipmentStatus1 = new EquipmentStatus(1L, "UNDER_MAINTENANCE");

        EquipmentStatus equipmentStatus2 = new EquipmentStatus(1L, "AVAILABLE");

        assertNotEquals(equipmentStatus1.hashCode(), equipmentStatus2.hashCode());
    }

    /**
     * Test similar object must have similar hashcode.
     */
    @Test
    void testHashCodeEqual() {
        EquipmentStatus equipmentStatus1 = new EquipmentStatus(1L, "AVAILABLE");

        EquipmentStatus equipmentStatus2 = new EquipmentStatus(1L, "AVAILABLE");

        assertEquals(equipmentStatus1.hashCode(), equipmentStatus2.hashCode());
    }

}
