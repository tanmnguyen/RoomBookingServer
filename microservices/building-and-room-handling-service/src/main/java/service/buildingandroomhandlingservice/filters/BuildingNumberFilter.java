package service.buildingandroomhandlingservice.filters;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import service.buildingandroomhandlingservice.entities.Room;

public class BuildingNumberFilter implements Filter {
    public Stream<Room> filter(Stream<Room> rooms, SearchQuery searchQuery) {
        return rooms.filter(r -> r.getBuildingNumber() == searchQuery.getBuildingNumber());
    }
}
