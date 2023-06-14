package service.buildingandroomhandlingservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestRepairRequestCreation {

    /**
     * Test constructor not null.
     */
    @Test
    void testConstructor() {
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(1L,
            "Description");

        assertNotNull(repairRequestCreation);
    }

    /**
     * Test get methods.
     */
    @Test
    void testGetMethods() {
        Long id = 1L;
        String description = "Description";
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            description);


        assertEquals(id, repairRequestCreation.getEquipmentId());
        assertEquals(description, repairRequestCreation.getDescription());
    }

    /**
     * Test equal method.
     * Same object must be equal
     */
    @Test
    void testEqualSameObject() {
        Long id = 1L;
        String description = "Description";
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            description);


        assertEquals(repairRequestCreation, repairRequestCreation);
    }

    /**
     * Test equal method.
     * The object must not be equal with null.
     */
    @Test
    void testNotEqualNull() {
        Long id = 1L;
        String description = "Description";
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            description);


        assertNotEquals(null, repairRequestCreation);
        assertNotEquals(repairRequestCreation, null);
    }

    /**
     * Test equal method.
     * The object must not be equal with an object in different type.
     */
    @Test
    void testNotEqualDifferentType() {
        Long id = 1L;
        String description = "Description";
        RepairRequestCreation repairRequestCreation = new RepairRequestCreation(id,
            description);


        String object = "other object";

        assertNotEquals(object, repairRequestCreation);
        assertNotEquals(repairRequestCreation, object);
    }

    /**
     * Test equal method.
     * The object must not be equal when any of the attributes are different.
     * In this test, the first attribute will be different
     */
    @Test
    void testNotEqualDifferentAttributeValueFirstAttribute() {

        RepairRequestCreation repairRequestCreation1 = new RepairRequestCreation(1L,
            "description1");

        RepairRequestCreation repairRequestCreation2 = new RepairRequestCreation(2L,
            "description1");


        assertNotEquals(repairRequestCreation1, repairRequestCreation2);
    }

    /**
     * Test equal method.
     * The object must not be equal when any of the attributes are different.
     * In this test, the second attribute will be different
     */
    @Test
    void testNotEqualDifferentAttributeValueSecondAttribute() {

        RepairRequestCreation repairRequestCreation1 = new RepairRequestCreation(1L,
            "description1");

        RepairRequestCreation repairRequestCreation2 = new RepairRequestCreation(1L,
            "description2");


        assertNotEquals(repairRequestCreation1, repairRequestCreation2);
    }

    /**
     * Test equal method.
     * Test when all the attributes are similar.
     */
    @Test
    void testEqual() {

        RepairRequestCreation repairRequestCreation1 = new RepairRequestCreation(1L,
            "description1");

        RepairRequestCreation repairRequestCreation2 = new RepairRequestCreation(1L,
            "description1");


        assertEquals(repairRequestCreation1, repairRequestCreation2);
    }

    /**
     * Test hash code.
     * Hash code of 2 different object must be different.
     */
    @Test
    void testHashCode() {

        RepairRequestCreation repairRequestCreation1 = new RepairRequestCreation(1L,
            "description1");

        RepairRequestCreation repairRequestCreation2 = new RepairRequestCreation(1L,
            "description2");

        assertNotEquals(repairRequestCreation1.hashCode(), repairRequestCreation2.hashCode());
    }

    /**
     * Test hash code.
     * Hash code of 2 similar object must be similar.
     */
    @Test
    void testHashCodeEqual() {

        RepairRequestCreation repairRequestCreation1 = new RepairRequestCreation(1L,
            "description1");

        RepairRequestCreation repairRequestCreation2 = new RepairRequestCreation(1L,
            "description1");

        assertEquals(repairRequestCreation1.hashCode(), repairRequestCreation2.hashCode());
    }



}
