package service.buildingandroomhandlingservice.entities;

import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "buildingNumber")
    private int buildingNumber;

    @Column(name = "roomNumber")
    private int roomNumber;

    @Column(name = "capacity")
    private int capacity;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Column(name = "equipment")
    private List<Equipment> equipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) default 'AVAILABLE'")
    private Status status;

    /**
     * Default empty constructor.
     */
    public Room() {
    }

    /**
     * Construct a new room.
     *
     * @param roomNumber     - number of the room (unique per building)
     * @param buildingNumber - number of the building the room is located in
     * @param capacity       - total capacity of the room
     * @param equipment      - equipment available in the room
     */
    public Room(int roomNumber, int buildingNumber, int capacity, List<Equipment> equipment) {
        this.id = roomNumber + "." + buildingNumber;
        this.roomNumber = roomNumber;
        this.buildingNumber = buildingNumber;
        this.capacity = capacity;
        this.equipment = equipment;
        this.status = Status.AVAILABLE;
    }


    public String getId() {
        return id;
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
        this.id = roomNumber + "." + buildingNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
        this.id = roomNumber + "." + buildingNumber;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    @Transactional
    public void removeEquipment(Equipment e) {
        this.equipment.remove(e);
    }

    /**
     * Add new equipment to the room.

     * @param newEquipment the equipment that needs to be added.
     */
    public void addEquipment(Equipment newEquipment) {
        this.equipment.add(newEquipment);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }

        Room room = (Room) o;
        
        if (buildingNumber != room.buildingNumber
            || roomNumber != room.roomNumber
            || capacity != room.capacity
            || !Objects.equals(equipment, room.equipment)
            || status != room.status) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "Room{"
            + "id=" + id
            + ", buildingNumber="
            + buildingNumber
            + ", roomNumber=" + roomNumber
            + ", capacity=" + capacity
            + ", equipment=" + equipment
            + ", status=" + status
            + '}';
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, buildingNumber, roomNumber, capacity, equipment, status);
    }
}
