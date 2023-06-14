package service.buildingandroomhandlingservice.entities;

import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "building")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "building_id")
    private Long id;

    @Column(name = "nr_of_rooms", nullable = false)
    private Long nrOfRooms;

    @Column(name = "open_time", nullable = false)
    private LocalTime openingTime;
    //Could have would make this opening times a individual entity
    //that allows for multiple opening/closing times
    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) default 'AVAILABLE'")
    private Status status;

    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "building",
    // cascade = CascadeType.REMOVE, orphanRemoval = true)
    //private Set<Room> rooms;

    /**
     * Constructs a new Building.

     * @param nrOfRooms - number of rooms in this building.
     * @param openingTime - the opening localTime of this building.
     * @param closingTime - the closing localTime of this building.
     * @param status - the status of this building
     */
    public Building(Long nrOfRooms, LocalTime openingTime, LocalTime closingTime, Status status) {
        this.nrOfRooms = nrOfRooms;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.status = status;
    }

    /**
     * Constructor for just tests.

     * @param id - id of building
     * @param nrOfRooms - number of rooms in this building.
     * @param openingTime - the opening localTime of this building.
     * @param closingTime - the closing localTime of this building.
     * @param status - the status of this building
     */
    public Building(Long id, Long nrOfRooms, LocalTime openingTime, LocalTime closingTime, Status status) {
        this.id = id;
        this.nrOfRooms = nrOfRooms;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNrOfRooms() {
        return nrOfRooms;
    }

    public void setNrOfRooms(Long nrOfRooms) {
        this.nrOfRooms = nrOfRooms;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Building{"
            + "id=" + id
            + ", nr_of_rooms=" + nrOfRooms
            + ", opening_time=" + openingTime
            + ", closing_time=" + closingTime
            + ", status=" + status
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Building)) {
            return false;
        }
        Building building = (Building) o;

        return getId().equals(building.getId())
            && getNrOfRooms().equals(building.getNrOfRooms())
            && getOpeningTime().equals(building.getOpeningTime())
            && getClosingTime().equals(building.getClosingTime())
            && getStatus() == building.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nrOfRooms, openingTime, closingTime, status);
    }
}
