package service.reservationhandlingservice.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "reservationId")
    private Long reservationId;

    @Column(name = "host_id")
    private String hostId;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "begin_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    public Reservation() {}

    /**
     * Constructor.

     * @param hostId id of host in type string.
     * @param roomId id of room in type string.
     * @param startTime begin of the reserved time slot.
     * @param endTime end of the reserved time slot.
     */
    public Reservation(String hostId, String roomId,
                       LocalDateTime startTime, LocalDateTime endTime) {
        this.hostId = hostId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Get reservation id.

     * @return id of this reservation.
     */
    public Long getReservationId() {
        return reservationId;
    }

    /**
     * Get host id.

     * @return id of host.
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Get room id.

     * @return id of the room.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Get the beginning of the reserved time.

     * @return the beginning of the reserved timeslot.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Get the end of the reserved time.

     * @return the end of the reserved timeslot.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }


    /**
     * Set the id of the room.

     * @param reservationId is the id of the room.
     */
    public void setId(long reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Set the id of the host for the room.

     * @param hostId is the id of the host who reserved the room.
     */
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    /**
     * Set the room id.

     * @param roomId is the id of the reserved room.
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Set the reserved starting time.

     * @param startTime is the beginning time slot at which the room is reserved.
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Set the reserved ending time.

     * @param endTime is the end of the time slot at which the room is reserved.
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        Reservation that = (Reservation) o;
        if (!getReservationId().equals(that.getReservationId())
            || !Objects.equals(getHostId(), that.getHostId())
            || !Objects.equals(getRoomId(), that.getRoomId())
            || !Objects.equals(getStartTime(), that.getStartTime())
            || !Objects.equals(getEndTime(), that.getEndTime())) {
            return false;
        }
        return true;
    }


    /**
     * Description: This is a temporary format, since the
     * reservation attributes are going to change.
     *
     * @return A user-friendly string format of the room.
     **/
    public String toString() {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String result = "";
        result = "You reserved room: " + this.roomId + " from " + formatter1.format(startTime) + " to " + formatter1.format(endTime);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, hostId, roomId, startTime, endTime);
    }
}
