package service.buildingandroomhandlingservice.entities;

import java.util.Objects;

public class RepairRequestCreation {
    private Long equipmentId;
    private String description;

    public RepairRequestCreation(Long equipmentId, String description) {
        this.equipmentId = equipmentId;
        this.description = description;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RepairRequestCreation that = (RepairRequestCreation) o;
        return Objects.equals(equipmentId, that.equipmentId)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, description);
    }
}
