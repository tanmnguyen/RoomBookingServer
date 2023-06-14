package service.reservationhandlingservice.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ReservationHolderTest {

    private ReservationHolder reservationHolder;

    @BeforeEach
    void setUp() {
        reservationHolder = new ReservationHolder("12", "14", "2");
    }

    @Test
    void getStartTime() {
        assertEquals("12", reservationHolder.getStartTime());
    }

    @Test
    void getEndTime() {
        assertEquals("14", reservationHolder.getEndTime());
    }

    @Test
    void getRoomId() {
        assertEquals("2", reservationHolder.getRoomId());
    }

    @Test
    void setStartTime() {
        reservationHolder.setStartTime("11");
        assertEquals("11", reservationHolder.getStartTime());
    }

    @Test
    void setEndTime() {
        reservationHolder.setEndTime("13");
        assertEquals("13", reservationHolder.getEndTime());
    }

    @Test
    void setRoomId() {
        reservationHolder.setRoomId("1");
        assertEquals("1", reservationHolder.getRoomId());
    }
}