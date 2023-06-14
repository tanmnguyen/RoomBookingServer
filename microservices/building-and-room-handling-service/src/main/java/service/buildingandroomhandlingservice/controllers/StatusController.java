package service.buildingandroomhandlingservice.controllers;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.buildingandroomhandlingservice.authentications.AuthenticateRequest;
import service.buildingandroomhandlingservice.authentications.Validate;
import service.buildingandroomhandlingservice.entities.AuthenticationResponse;
import service.buildingandroomhandlingservice.entities.Building;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.entities.Status;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;


@Controller
@RequestMapping("/building")
public class StatusController {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Endpoint for changing building status.
     * This API will also change the status of all rooms connected to the building.
     *
     * @param request the token to validate.
     * @param buildingId the id of the building for the status change
     * @param status the new status of all the rooms and building
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    public ResponseEntity<?> changeStatus(HttpServletRequest request,
                                                 @RequestParam("id") long buildingId,
                                                 @RequestParam("status") String status) {
        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String role = AuthenticateRequest.getAuthenticationResponse().getRole();
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return changeStatusHelper(buildingId, status);
    }

    /**
     * Helper method for the changeStatus endpoint.
     *
     * @param buildingId the id of the building for the status change
     * @param status the new status of all the rooms and building
     */
    public ResponseEntity<?> changeStatusHelper(long buildingId, String status) {

        //validate that the new status provided is an existing status
        boolean contains = false;
        for (Status s : Status.values()) {
            contains = contains | s.toString().equals(status);
        }

        if (!contains) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        //prevent method being called on non existing building
        Optional<Building> optionalBuilding = buildingRepository.findById(buildingId);
        if (optionalBuilding.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        //now we change the building status
        Building building = optionalBuilding.get();
        building.setStatus(Status.valueOf(status));
        buildingRepository.save(building);

        //now we change status of rooms in building
        Optional<List<Room>> optionalRoomList = roomRepository.findByBuildingNumber((int) buildingId);
        if (optionalRoomList.isPresent()) {
            List<Room> rooms = optionalRoomList.get();
            for (Room r : rooms) {
                r.setStatus(Status.valueOf(status));
                roomRepository.save(r);
            }
        }
        return ResponseEntity.ok("Success");
    }

}
