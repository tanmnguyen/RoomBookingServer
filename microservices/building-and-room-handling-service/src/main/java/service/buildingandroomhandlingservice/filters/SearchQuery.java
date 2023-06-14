package service.buildingandroomhandlingservice.filters;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchQuery {
    private int buildingNumber = -1;
    private List<Equipment> equipment;
    private int capacity = -1;
    private Status status = Status.UNDETERMINED;

    public boolean hasBuildingNumber() {
        return buildingNumber != -1;
    }

    /**
     * Check whether any Equipment is present in the search Query.

     * @return true when Equipment is present, false otherwise
     */
    public boolean hasEquipment() {
        if (equipment == null) {
            return false;
        }
        return !equipment.isEmpty();
    }

    public boolean hasCapacity() {
        return capacity != -1;
    }

    public boolean hasStatus() {
        return status != Status.UNDETERMINED;
    }

}
