package service.buildingandroomhandlingservice.entities;

import java.util.Objects;

public class EquipmentCreateRequest {
    private String type;
    private String roomId;


    /**
     * Constructor.

     * @param type type of the equipment.
     * @param roomId id of the room that will have this new equipment.
     */
    public EquipmentCreateRequest(String type, String roomId) {
        this.type = type;
        this.roomId = roomId;
    }

    public String getType() {
        return type;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EquipmentCreateRequest that = (EquipmentCreateRequest) o;
        return Objects.equals(type, that.type)
            && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, roomId);
    }
}
