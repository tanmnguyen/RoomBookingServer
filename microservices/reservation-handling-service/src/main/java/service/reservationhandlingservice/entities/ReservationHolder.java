package service.reservationhandlingservice.entities;

public class ReservationHolder {

    private String startTime;
    private String endTime;
    private String roomId;


    /**
     * Constructor.

     * @param startTime starting time of the reservation.
     * @param endTime ending time of the reservation.
     * @param roomId ID of the reserved room.
     */
    public ReservationHolder(String startTime, String endTime, String roomId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

}
