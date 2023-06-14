package service.buildingandroomhandlingservice.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "equipment")
public class Equipment {
    public enum Availability {
        AVAILABLE,
        UNDER_MAINTENANCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) default 'AVAILABLE'")
    private Availability status;



    /**
     * Equipment Constructor.

     */
    public Equipment() {
    }

    /**
     * Equipment Constructor.

     */
    public Equipment(String type) {
        this.type = type;
        this.status = Availability.AVAILABLE;
    }

    /**
     * Equipment Constructor.

     * @param id the unique id of the equipment.
     * @param type wthe type of the equipment.
     */
    public Equipment(Long id, String type) {
        this.id = id;
        this.type = type;
        this.status = Availability.AVAILABLE;
    }

    /**
     * Get id.

     * @return the ID of the equipment.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.

     * @param id  new id for equipment.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get type.

     * @return the type of the equipment.
     */
    public String getType() {
        return type;
    }

    /**
     * Set type.

     * @param type the new type of equipment.
     */
    public void setType(String type) {
        this.type = type;
    }

    public Availability getStatus() {
        return status;
    }

    public void setStatus(Availability status) {
        this.status = status;
    }

    /**
     * Equals.

     * @param o The object you want to compare it with.

     * @return true if the objects are equal or the same. False if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Equipment)) {
            return false;
        }
        Equipment equipment = (Equipment) o;
        return id == equipment.id
            && type.equals(equipment.type);
    }


    /**
     * To string.

     * @return A string of JSON format of the equipment.
     */
    @Override
    public String toString() {
        return "Equipment{"
            + "id=" + id
            + ", type='" + type + '\''
            + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, status);
    }
}
