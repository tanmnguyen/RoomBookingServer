package service.buildingandroomhandlingservice.entities;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import org.junit.Test;

public class RepairRequestTest {

    @Test
    public void constructorAndGetterTest() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        assertEquals(1, repairRequest.getId());
        assertEquals("51", repairRequest.getRoomId());
        assertEquals(5, repairRequest.getEquipmentId());
        Date date = new Date();
        assertTrue(Math.abs(repairRequest.getCreatedAt().compareTo(new Timestamp(date.getTime()))) < 1000000000);
        //System.out.println(repairRequest.getCreated_at().toString());
    }

    @Test
    public void equalsTestEqual() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 5L);

        assertTrue(repairRequest.equals(repairRequest1));
        assertTrue(repairRequest.equals(repairRequest));

    }

    @Test
    public void equalsTestDifferent() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest3 = new RepairRequest(2L, "52", 6L);

        assertFalse(repairRequest.equals(repairRequest3));
    }

    // PMD does not allow to call .equals(null)
    //    @Test
    //    public void equalsTestNull() {
    //        RepairRequest repairRequest = new RepairRequest(1, "51", new Equipment(1L, "Whiteboard"));
    //        assertFalse(repairRequest.equals(null));
    //    }

    @Test
    public void setterTestId() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest1 = new RepairRequest(2L, "51", 5L);
        repairRequest.setId(2L);
        assertEquals(repairRequest1, repairRequest);
    }

    @Test
    public void setterTestRoomId() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 5L);
        repairRequest.setRoomId("51");

        assertEquals(repairRequest1, repairRequest);
    }

    @Test
    public void setterTestEquipment() {
        RepairRequest repairRequest = new RepairRequest(1L,  "51", 5L);
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 6L);
        repairRequest.setEquipmentId(6);
        assertEquals(repairRequest1, repairRequest);
    }

    // Can't realistically test the inverse due to small passage of time during execution of the test
    @Test
    public void hashCodeTest() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest1 = new RepairRequest(2L, "51", 6L);
        assertNotEquals(repairRequest.hashCode(), repairRequest1.hashCode());
    }


    /**
     * The object must not equal to null.
     */
    @Test
    public void testEqualMethodNotEqualNull() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        assertNotEquals(null, repairRequest);
        assertNotEquals(repairRequest, null);
    }

    /**
     * The object must not equal to another type object.
     */
    @Test
    public void testEqualMethodNotEqualDifferentType() {
        RepairRequest repairRequest = new RepairRequest(1L, "51", 5L);
        String other = "other";


        assertNotEquals(other, repairRequest);
        assertNotEquals(repairRequest, other);
    }

    /**
     * The object must not equal to different first or second attribute.
     */
    @Test
    public void testEqualMethodNotEqualDifferentFirstAttribute() {
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest2 = new RepairRequest(2L, "51", 5L);
        RepairRequest repairRequest3 = new RepairRequest(1L, "52", 5L);
        assertNotEquals(repairRequest1, repairRequest2);
        assertNotEquals(repairRequest1, repairRequest3);


    }

    /**
     * The object must not equal to different third attribute.
     */
    @Test
    public void testEqualMethodNotEqualDifferentThirdAttribute() {
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest2 = new RepairRequest(1L, "51", 6L);

        assertNotEquals(repairRequest1, repairRequest2);

    }

    /**
     * The object must not equal to when the discription is different.
     */
    @Test
    public void testEqualMethodNotEqualDifferentDescription() {
        RepairRequest repairRequest1 = new RepairRequest(1L, "broken");
        RepairRequest repairRequest2 = new RepairRequest(1L, "bad");
        repairRequest1.setRoomId("1.1");
        repairRequest2.setRoomId("1.1");
        assertNotEquals(repairRequest1, repairRequest2);

    }

    /**
     * The object must be equal when the attributes are similar.
     */
    @Test
    public void testEqualMethodEqualWhenIdAndDescriptionIsSimilar() {
        RepairRequest repairRequest1 = new RepairRequest(1L, "broken");
        RepairRequest repairRequest2 = new RepairRequest(1L, "broken");
        repairRequest1.setRoomId("1.1");
        repairRequest2.setRoomId("1.1");

        assertEquals(repairRequest1, repairRequest2);

    }

    /**
     * The object must be equal when comparing the similar set of attributes.
     */
    @Test
    public void testEqualMethodMustEqualSimilarAttributes() {
        RepairRequest repairRequest1 = new RepairRequest(1L, "51", 5L);
        RepairRequest repairRequest2 = new RepairRequest(1L, "51", 5L);

        assertEquals(repairRequest1, repairRequest2);

    }
}