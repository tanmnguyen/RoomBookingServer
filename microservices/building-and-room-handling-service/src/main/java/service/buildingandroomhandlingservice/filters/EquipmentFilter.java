package service.buildingandroomhandlingservice.filters;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import service.buildingandroomhandlingservice.entities.Room;

public class EquipmentFilter implements Filter {
    public Stream<Room> filter(Stream<Room> rooms, SearchQuery searchQuery) {
        return rooms.filter(r -> r.getEquipment().containsAll(searchQuery.getEquipment()));
    }
}
