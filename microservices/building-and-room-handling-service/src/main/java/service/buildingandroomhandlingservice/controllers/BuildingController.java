package service.buildingandroomhandlingservice.controllers;

import java.sql.BatchUpdateException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
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
import service.buildingandroomhandlingservice.entities.Building;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.repositories.BuildingRepository;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;


@Controller
@RequestMapping("/building")
public class BuildingController {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;


    /**
     * Endpoint for adding a building.

     * @param request the token to validate.
     * @param building the building to be added
     * @return status of the request
     */
    @PostMapping("/addBuilding")
    @ResponseBody
    public ResponseEntity<?> addBuilding(HttpServletRequest request, @RequestBody Building building) {
        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        //Check if user that made the request is an admin
        String role = AuthenticateRequest.getAuthenticationResponse().getRole();
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //check if new building doesnt override existing building
        long buildingId = building.getId();
        Optional<Building> optionalBuilding = buildingRepository.findById(buildingId);
        if (optionalBuilding.isEmpty()) {
            buildingRepository.save(building);
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Building with this exact ID already exist, use different ID or use the Change building API");
        }
    }

    /**
     * Endpoint for removing a building.
     * This method will also remove all rooms inside the building
     *
     * @param request the token to validate.
     * @param buildingId the id of the building to remove
     */
    @DeleteMapping("/removeBuilding")
    @ResponseBody
    public ResponseEntity<?> removeBuilding(HttpServletRequest request, @RequestParam("id") long buildingId) {
        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        //Check if user that made the request is an admin
        String role = AuthenticateRequest.getAuthenticationResponse().getRole();
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //remove rooms inside building
        Optional<List<Room>> optionalRoomList = roomRepository.findByBuildingNumber((int) buildingId);
        if (optionalRoomList.isPresent()) {
            List<Room> rooms = optionalRoomList.get();
            for (Room r : rooms) {
                roomRepository.delete(r);
            }
        }

        //remove building
        buildingRepository.deleteById(buildingId);
        return  ResponseEntity.ok("Success");
    }


    /**
     * Endpoint for opening and closing times of a building.
     *
     * @return the building opening and closing times.
     */
    @GetMapping("/getOpeningHours{buildingId}")
    public ResponseEntity<?> getOpeningHours(@RequestParam long buildingId) {
        Optional<Building> optional = buildingRepository.findBuildingById(buildingId);
        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("Building with id : " + buildingId + " does not exist");
        }
        Building building = optional.get();
        String result = building.getOpeningTime().toString() + ", " + building.getClosingTime().toString();
        return ResponseEntity.ok().body(result);
    }
}


