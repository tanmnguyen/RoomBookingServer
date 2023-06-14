package service.buildingandroomhandlingservice.entities;

import java.util.Objects;

public class EquipmentStatus {
    private Long id;
    private String availability;

    EquipmentStatus() {
    }

    public EquipmentStatus(Long id, String availability) {
        this.id = id;
        this.availability = availability;
    }

    public Long getId() {
        return id;
    }

    public String getAvailability() {
        return availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EquipmentStatus that = (EquipmentStatus) o;
        return Objects.equals(id, that.id)
            && Objects.equals(availability, that.availability);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, availability);
    }
}
