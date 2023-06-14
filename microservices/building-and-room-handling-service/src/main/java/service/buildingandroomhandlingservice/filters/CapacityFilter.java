package service.buildingandroomhandlingservice.filters;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import service.buildingandroomhandlingservice.entities.Room;

public class CapacityFilter implements Filter {
    public Stream<Room> filter(Stream<Room> rooms, SearchQuery searchQuery) {
        return rooms.filter(p -> p.getCapacity() >= searchQuery.getCapacity());
    }
}
