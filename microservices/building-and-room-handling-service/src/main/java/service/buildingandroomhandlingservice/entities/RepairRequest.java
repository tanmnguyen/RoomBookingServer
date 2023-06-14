package service.buildingandroomhandlingservice.entities;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "repair_request")
public class RepairRequest {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "roomId")
    private String roomId;

    @Column(name = "equipmentId")
    private Long equipmentId;

    @Column(name = "create_at")
    private Timestamp createdAt;

    @Column(name = "description")
    private String description;

    /**
     * Default constructor.
     */
    public RepairRequest() {

    }


    /**
     * Main Constructor for this class.

     * @param id The id of the RepairRequest
     * @param roomId The roomId of the Room the Equipment that is to be repaired is in
     * @param equipmentId The EquipmentId of the Equipment the RepairRequest is about
     */
    public RepairRequest(Long id, String roomId, Long equipmentId) {
        this.id = id;
        this.roomId = roomId;
        this.equipmentId = equipmentId;
        Date date = new Date();
        createdAt = new Timestamp(date.getTime());

    }

    /**
     * Constructor.

     * @param equipmentId The EquipmentId of the Equipment the RepairRequest is about
     * @param description The description about what are wrong with the equipment.
     */
    public RepairRequest(long equipmentId, String description) {
        this.equipmentId = equipmentId;
        this.description = description;

        Date date = new Date();
        createdAt = new Timestamp(date.getTime());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RepairRequest that = (RepairRequest) o;
        return Objects.equals(id, that.id) && roomId.equals(that.roomId)
            && Objects.equals(equipmentId, that.equipmentId)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomId, equipmentId, createdAt, description);
    }
}
