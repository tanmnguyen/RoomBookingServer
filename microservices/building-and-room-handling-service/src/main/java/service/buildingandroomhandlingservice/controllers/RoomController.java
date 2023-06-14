package service.buildingandroomhandlingservice.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.buildingandroomhandlingservice.authentications.AuthenticateRequest;
import service.buildingandroomhandlingservice.authentications.Validate;
import service.buildingandroomhandlingservice.entities.AuthenticationResponse;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.EquipmentCreateRequest;
import service.buildingandroomhandlingservice.entities.EquipmentStatus;
import service.buildingandroomhandlingservice.entities.RepairRequest;
import service.buildingandroomhandlingservice.entities.RepairRequestCreation;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.entities.Status;
import service.buildingandroomhandlingservice.filters.BuildingNumberFilter;
import service.buildingandroomhandlingservice.filters.CapacityFilter;
import service.buildingandroomhandlingservice.filters.EquipmentFilter;
import service.buildingandroomhandlingservice.filters.Filter;
import service.buildingandroomhandlingservice.filters.SearchQuery;
import service.buildingandroomhandlingservice.filters.StatusFilter;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;

@Log
@Controller
@RequestMapping("/room")
public class RoomController {


    private RoomRepository roomRepository;


    /**
     * controller constructor.
     *
     * @param roomRepository      - initialise room repository
     */
    @Autowired
    public RoomController(RoomRepository roomRepository) {

        this.roomRepository = roomRepository;
    }


    /**
     * Endpoint for adding room.
     *
     * @param room room.
     * @return room.
     */
    @PostMapping("/addRoom")
    @ResponseBody
    public Room addRoom(@RequestBody Room room) {
        Room newRoom = new Room(room.getRoomNumber(),
            room.getBuildingNumber(), room.getCapacity(), room.getEquipment());
        newRoom.setId(room.getRoomNumber() + "." + room.getBuildingNumber());
        roomRepository.save(newRoom);
        return newRoom;
    }

    /**
     * Endpoint for view.
     *
     * @param id id.
     * @return Response entity.
     */
    @GetMapping("/view")
    public ResponseEntity getRoom(@RequestParam("id") String id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            Room found = room.get();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Succes!" + found);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no room with given id");
        }
    }

    /**
     * View all.
     *
     * @return list of rooms.
     */
    @GetMapping("/viewAll")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * Change the status of a room.
     *
     * @param id     id.
     * @param status status.
     * @return response entity.
     */
    @PostMapping("/status")
    @ResponseBody
    public ResponseEntity changeStatus(@RequestParam("id") String id,
                                       @RequestParam("status") Status status) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            Room found = room.get();
            found.setStatus(status);
            roomRepository.save(found);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Succes!" + found);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no room with given id");
        }
    }


    /**
     * Search for Rooms based on BuildingNumber, Capacity, Equipment and Status.
     *
     * @param searchQuery a wrapper class for the above search options
     * @param request     the HttpRequest containing authentication data
     * @return a response entity containing a list of rooms
     */
    @GetMapping("/search")
    public ResponseEntity searchRooms(@RequestBody SearchQuery searchQuery,
                                      HttpServletRequest request) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        if (searchQuery == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Could not read Search Query");
        }

        List<Filter> filters = new ArrayList<>();

        filters.addAll(getFilters(searchQuery));

        List<Room> rooms = roomRepository.findAll();
        Stream<Room> res = rooms.stream();

        for (Filter filter : filters) {
            res = filter.filter(res, searchQuery);
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body("succes!" + res.collect(Collectors.toList()));
    }

    private List<Filter> getFilters(SearchQuery searchQuery) {

        List<Filter> filters = new ArrayList<>();

        if (searchQuery.hasCapacity()) {
            filters.add(new CapacityFilter());
        }
        if (searchQuery.hasBuildingNumber()) {
            filters.add(new BuildingNumberFilter());
        }
        if (searchQuery.hasStatus()) {
            filters.add(new StatusFilter());
        }
        if (searchQuery.hasEquipment()) {
            filters.add(new EquipmentFilter());
        }

        return filters;
    }
}
