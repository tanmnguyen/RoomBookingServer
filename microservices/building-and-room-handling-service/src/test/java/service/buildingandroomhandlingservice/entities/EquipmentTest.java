package service.buildingandroomhandlingservice.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class EquipmentTest {


    @Test
    void testHashCode() {
        Equipment equipment = new Equipment("board");
        assertNotNull(equipment.hashCode());
    }


    @Test
    void getType() {
        Equipment equipment = new Equipment("board");
        String result = equipment.getType();
        assertEquals("board", result);
    }

    @Test
    void setType() {

        Equipment equipment = new Equipment("board");
        String insert = "computer";
        equipment.setType(insert);

        assertEquals(insert, equipment.getType());
    }


    @Test
    void testEquals() {
        Equipment equipment = new Equipment(1L, "board");
        Equipment equipment1 = new Equipment(2L, "table");
        assertFalse(equipment.equals(equipment1));

        Equipment equipment2 = new Equipment(3L, "board");
        assertFalse(equipment.equals(equipment2));

        Equipment equipment3 = new Equipment(4L, "chairs");
        assertFalse(equipment.equals(equipment3));

        Equipment equipment4 = new Equipment(1L, "board");
        assertTrue(equipment.equals(equipment4));
        assertTrue(equipment.equals(equipment));
        assertFalse(equipment.equals("Type string")); //If the class type is not the same
    }

    @Test
    void testToString() {
        Equipment equipment = new Equipment("board");
        equipment.setId(1L);

        String expected = "Equipment{id=" + "1" + ", type='" + "board"  + "'}";
        assertEquals(expected, equipment.toString());
    }

    @Test
    void testGetStatus() {
        Equipment equipment = new Equipment("board");
        assertEquals(Equipment.Availability.AVAILABLE, equipment.getStatus());
    }

    @Test
    void testSetStatus() {
        Equipment equipment = new Equipment("board");
        equipment.setStatus(Equipment.Availability.UNDER_MAINTENANCE);
        assertEquals(Equipment.Availability.UNDER_MAINTENANCE, equipment.getStatus());
    }

    @Test
    void testGetId() {
        Equipment equipment = new Equipment(1L, "board");
        assertEquals(1L, equipment.getId());
    }

    /**
     * The object must not be equal to null.
     */
    @Test
    void testNotEqualNull() {
        Equipment equipment = new Equipment("board");
        assertNotEquals(equipment, null);
        assertNotEquals(null, equipment);
    }

    /**
     * The object must not be equal to a different type.
     */
    @Test
    void testNotEqualDifferentType() {
        Equipment equipment = new Equipment("board");
        String other = "other";

        assertNotEquals(equipment, other);
        assertNotEquals(other, equipment);
    }

    /**
     * The object must be equal when the attributes are similar.
     */
    @Test
    void testEqualSimilarAttributes() {
        Equipment equipment1 = new Equipment(1L, "board");
        Equipment equipment2 = new Equipment(1L, "board");

        assertEquals(equipment1, equipment2);
    }

    /**
     * The object must be not equal when the attributes are different.
     */
    @Test
    void testNotEqualDifferentAttributes() {
        Equipment equipment1 = new Equipment(1L, "board");
        Equipment equipment2 = new Equipment(1L, "monitor");

        assertNotEquals(equipment1, equipment2);
    }
}