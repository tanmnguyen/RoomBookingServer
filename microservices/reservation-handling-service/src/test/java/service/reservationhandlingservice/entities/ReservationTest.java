package service.reservationhandlingservice.entities;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation("hostId", "roomId", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation.setId(1L);
    }

    @Test
    void testHashCode() {
        assertNotNull(reservation.hashCode());
    }

    @Test
    void getReservationId() {
        assertEquals(1L, reservation.getReservationId());
    }

    @Test
    void getHostId() {

        assertEquals("hostId", reservation.getHostId());
    }

    @Test
    void getRoomId() {
        assertEquals("roomId", reservation.getRoomId());
    }

    @Test
    void getStartTime() {
        assertEquals(LocalDateTime.of(2021, 12, 16, 12, 00), reservation.getStartTime());
    }

    @Test
    void getEndTime() {
        assertEquals(LocalDateTime.of(2021, 12, 16, 14, 00), reservation.getEndTime());
    }

    @Test
    void setId() {
        reservation.setId(2L);
        assertEquals(2L, reservation.getReservationId());
    }

    @Test
    void setHostId() {
        reservation.setHostId("hostId2");
        assertEquals("hostId2", reservation.getHostId());
    }

    @Test
    void setRoomId() {
        reservation.setRoomId("roomId2");
        assertEquals("roomId2", reservation.getRoomId());
    }

    @Test
    void setStartTime() {
        reservation.setStartTime(LocalDateTime.of(2021, 12, 16, 13, 00));
        assertEquals(LocalDateTime.of(2021, 12, 16, 13, 00), reservation.getStartTime());
    }

    @Test
    void setEndTime() {
        reservation.setEndTime(LocalDateTime.of(2021, 12, 16, 15, 00));
        assertEquals(LocalDateTime.of(2021, 12, 16, 15, 00), reservation.getEndTime());
    }

    @Test
    void testToString() {
        assertEquals("You reserved room: roomId from 2021-12-16 12:00 to 2021-12-16 14:00", reservation.toString());
    }

    @Test
    void testEqualsSameObject() {
        assertTrue(reservation.equals(reservation));
    }

    @Test
    void testEqualsDifferentObject() {
        assertFalse(reservation.equals("String Object"));
    }

    @Test
    void testEqualsDifferentReservationId() {
        Reservation reservation2 = new Reservation("hostId", "roomId", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation2.setId(2L);
        assertFalse(reservation.equals(reservation2));
    }

    @Test
    void testEqualsDifferentHostId() {
        Reservation reservation2 = new Reservation("hostId2", "roomId", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation2.setId(1L);
        assertFalse(reservation.equals(reservation2));
    }

    @Test
    void testEqualsDifferentRoomId() {
        Reservation reservation2 = new Reservation("hostId", "roomId2", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation2.setId(1L);
        assertFalse(reservation.equals(reservation2));
    }

    @Test
    void testEqualsDifferentStartTime() {
        Reservation reservation2 = new Reservation("hostId", "roomId", LocalDateTime.of(2021, 12, 16, 11, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation2.setId(1L);
        assertFalse(reservation.equals(reservation2));
    }

    @Test
    void testEqualsDifferentEndTime() {
        Reservation reservation2 = new Reservation("hostId", "roomId", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 15, 00));
        reservation2.setId(1L);
        assertFalse(reservation.equals(reservation2));
    }

    @Test
    void testEqualsSameValues() {
        Reservation reservation2 = new Reservation("hostId", "roomId", LocalDateTime.of(2021, 12, 16, 12, 00), LocalDateTime.of(2021, 12, 16, 14, 00));
        reservation2.setId(1L);
        assertTrue(reservation.equals(reservation2));
    }
}