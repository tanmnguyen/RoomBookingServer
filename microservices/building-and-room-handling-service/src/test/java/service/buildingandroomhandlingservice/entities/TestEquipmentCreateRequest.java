package service.buildingandroomhandlingservice.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestEquipmentCreateRequest {

    /**
     * Test constructor.
     */
    @Test
    void testConstructor() {
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("type",
            "id");

        assertNotNull(equipmentCreateRequest);
    }

    /**
     * Test get methods.
     */
    @Test
    void testGetMethods() {
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("type",
            "id");

        assertEquals("type", equipmentCreateRequest.getType());
        assertEquals("id", equipmentCreateRequest.getRoomId());

    }

    /**
     * Test equals when comparing the same object.
     */
    @Test
    void testEqualSameObject() {
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("type",
            "id");

        assertEquals(equipmentCreateRequest, equipmentCreateRequest);
    }

    /**
     * Test not equal when comparing with null.
     */
    @Test
    void testNotEqualsNull() {
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("type",
            "id");

        assertNotEquals(null, equipmentCreateRequest);
        assertNotEquals(equipmentCreateRequest, null);
    }

    /**
     * Test not equal when comparing with a different type.
     */
    @Test
    void testNotEqualsDifferentType() {
        EquipmentCreateRequest equipmentCreateRequest = new EquipmentCreateRequest("type",
            "id");

        String object = "something different";
        assertNotEquals(object, equipmentCreateRequest);
        assertNotEquals(equipmentCreateRequest, object);
    }

    /**
     * Test not equal when comparing 2 object with different first attribute.
     */
    @Test
    void testEqualsDifferentTypeFirstAttribute() {
        EquipmentCreateRequest equipmentCreateRequest1 = new EquipmentCreateRequest("pen",
            "id");

        EquipmentCreateRequest equipmentCreateRequest2 = new EquipmentCreateRequest("pencil",
            "id");

        assertNotEquals(equipmentCreateRequest1, equipmentCreateRequest2);
    }

    /**
     * Test not equal when comparing 2 object with different second attribute.
     */
    @Test
    void testEqualsDifferentTypeSecondAttribute() {
        EquipmentCreateRequest equipmentCreateRequest1 = new EquipmentCreateRequest("pen",
            "id1");

        EquipmentCreateRequest equipmentCreateRequest2 = new EquipmentCreateRequest("pen",
            "id2");

        assertNotEquals(equipmentCreateRequest1, equipmentCreateRequest2);
    }

    /**
     * Test equal when comparing 2 object with same attribute.
     */
    @Test
    void testEqualsSameAttribute() {
        EquipmentCreateRequest equipmentCreateRequest1 = new EquipmentCreateRequest("pen",
            "id1");

        EquipmentCreateRequest equipmentCreateRequest2 = new EquipmentCreateRequest("pen",
            "id1");

        assertEquals(equipmentCreateRequest1, equipmentCreateRequest2);
    }

    /**
     * Test different object must have different hashcode.
     */
    @Test
    void testHashCode() {
        EquipmentCreateRequest equipmentCreateRequest1 = new EquipmentCreateRequest("pen",
            "id1");

        EquipmentCreateRequest equipmentCreateRequest2 = new EquipmentCreateRequest("pen",
            "id2");

        assertNotEquals(equipmentCreateRequest1.hashCode(), equipmentCreateRequest2.hashCode());
    }



}
