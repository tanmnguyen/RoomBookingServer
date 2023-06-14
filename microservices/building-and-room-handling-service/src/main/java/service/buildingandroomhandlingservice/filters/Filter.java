package service.buildingandroomhandlingservice.filters;

import java.util.stream.Stream;
import service.buildingandroomhandlingservice.entities.Room;

public interface Filter {
    public Stream<Room> filter(Stream<Room> rooms, SearchQuery searchQuery);
}
