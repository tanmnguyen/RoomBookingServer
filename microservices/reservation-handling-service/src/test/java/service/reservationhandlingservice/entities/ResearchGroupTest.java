package service.reservationhandlingservice.entities;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




class ResearchGroupTest {

    private ResearchGroup firstresearchGroup;

    @BeforeEach
    void setUp() {
        firstresearchGroup = new ResearchGroup("secretary",  Arrays.asList("member1", "member2", "member3"));
        firstresearchGroup.setGroupId(1L);
    }

    @Test
    void getGroupId() {
        assertEquals(1L, firstresearchGroup.getGroupId());
    }

    @Test
    void setGroupId() {
        firstresearchGroup.setGroupId(2L);
        assertEquals(2L, firstresearchGroup.getGroupId());
    }

    @Test
    void getSecretaryId() {
        assertEquals("secretary", firstresearchGroup.getSecretaryId());
    }

    @Test
    void setSecretaryId() {
        firstresearchGroup.setSecretaryId("secretary2");
        assertEquals("secretary2", firstresearchGroup.getSecretaryId());
    }

    @Test
    void getGroup_members() {
        assertEquals(Arrays.asList("member1", "member2", "member3"), firstresearchGroup.getGroup_members());
    }

    @Test
    void setGroup_members() {
        firstresearchGroup.setGroup_members(Arrays.asList("member4", "member5", "member6"));
        assertEquals(Arrays.asList("member4", "member5", "member6"), firstresearchGroup.getGroup_members());
    }

    @Test
    void addExistingGroupMember() {
        assertThrows(Exception.class, () -> firstresearchGroup.addGroupMember("member3"));
    }

    @Test
    void addNewGroupMember() throws Exception {
        firstresearchGroup.addGroupMember("member4");
        assertEquals(Arrays.asList("member1", "member2", "member3", "member4"), firstresearchGroup.getGroup_members());
    }

    @Test
    void removeNonExistingGroupMember() {
        assertThrows(Exception.class, () -> firstresearchGroup.removeGroupMember("member4"));
    }

    @Test
    void removeExistingGroupMember() throws Exception {
        firstresearchGroup.removeGroupMember("member3");
        assertEquals(Arrays.asList("member1", "member2"), firstresearchGroup.getGroup_members());
    }

    @Test
    void testSameResearchGroup() {
        assertTrue(firstresearchGroup.equals(firstresearchGroup));
    }

    @Test
    void testDifferentSecretaryResearchGroup() {

        ResearchGroup secondresearchGroup = new ResearchGroup("secretary2",  Arrays.asList("member1", "member2", "member3"));
        assertFalse(firstresearchGroup.equals(secondresearchGroup));
    }

    @Test
    void testsSameSecretaryDifferentResearchGroup() {
        ResearchGroup secondresearchGroup = new ResearchGroup("secretary",  Arrays.asList("member3", "member4", "member5"));
        assertFalse(firstresearchGroup.equals(secondresearchGroup));
    }

    @Test
    void testsSameSecretarySameResearchGroup() {
        ResearchGroup secondresearchGroup = new ResearchGroup("secretary",  Arrays.asList("member1", "member2", "member3"));
        secondresearchGroup.setGroupId(1L);
        assertTrue(firstresearchGroup.equals(secondresearchGroup));
    }


    @Test
    void testToStringResearchGroup() {
        assertEquals("The group of secretary with group members:\n"
            + "+member1\n"
            + "+member2\n"
            + "+member3\n", firstresearchGroup.toString());
    }

    @Test
    void testToStringNoResearchGroup() {
        assertEquals("The group of secretary2 has no group members\n", new ResearchGroup("secretary2",  Arrays.asList()).toString());
    }

    @Test
    void testHashCode() {
        assertEquals(190595039, firstresearchGroup.hashCode());
    }

    //Boundary testing: one under
    @Test
    void testInvalidUnderHashCode() {
        assertNotEquals(190595038, firstresearchGroup.hashCode());
    }

    //Boundary testing: one Above
    @Test
    void testInvalidAboveHashCode() {
        assertNotEquals(190595040, firstresearchGroup.hashCode());
    }


    @Test
    void testEqualsNotSameInstance() {
        assertFalse(firstresearchGroup.equals("String"));
    }

    @Test
    void testEqualsNotSameSecretary() {
        ResearchGroup secondResearchGroup = new ResearchGroup("secretary2",  Arrays.asList("member1", "member2", "member3"));
        secondResearchGroup.setGroupId(1L);
        assertFalse(firstresearchGroup.equals(secondResearchGroup));
    }

    @Test
    void testEqualsNotSameGroupMembers() {
        ResearchGroup secondResearchGroup = new ResearchGroup("secretary",  Arrays.asList("member1", "member2", "member4"));
        secondResearchGroup.setGroupId(1L);
        assertFalse(firstresearchGroup.equals(secondResearchGroup));
    }



}