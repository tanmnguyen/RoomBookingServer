package service.buildingandroomhandlingservice.controllers;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.buildingandroomhandlingservice.authentications.AuthenticateRequest;
import service.buildingandroomhandlingservice.authentications.Validate;
import service.buildingandroomhandlingservice.entities.AuthenticationResponse;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.EquipmentCreateRequest;
import service.buildingandroomhandlingservice.entities.EquipmentStatus;
import service.buildingandroomhandlingservice.entities.Room;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RoomRepository;


@Log
@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;


    /**
     * Update the availability of the equipment.
     * Only the system admin can perform this operation.

     * @param request http request.
     * @param equipmentStatus containing the equipment id and status of the equipment.

     * @return response entity indicating the status of the operation.
     */
    @PostMapping("/updateEquipmentStatus")
    @ResponseBody
    public ResponseEntity updateEquipmentStatus(HttpServletRequest request,
                                                @RequestBody EquipmentStatus equipmentStatus) {

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

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentStatus.getId());

        if (equipmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Equipment equipment = equipmentOptional.get();

        try {
            equipment.setStatus(Equipment.Availability.valueOf(equipmentStatus.getAvailability()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        equipmentRepository.save(equipment);

        return ResponseEntity.ok(equipment.toString());
    }

    /**
     * Add a new equipment to a room.
     *
     * @param request http request used to validate the user. Only admin perform this request.
     * @param equipmentCreateRequest request entity to add new equipment.

     * @return unauthorized code if the user is not admin. Else return the string presentation of the requested room with the new added equipment.
     */
    @PostMapping("/addEquipment")
    public ResponseEntity<?> addEquipment(HttpServletRequest request,
                                          @RequestBody EquipmentCreateRequest equipmentCreateRequest) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String role = AuthenticateRequest.getAuthenticationResponse().getRole();

        // unauthorized.
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String type = equipmentCreateRequest.getType();
        String roomId = equipmentCreateRequest.getRoomId();

        Optional<Room> roomOptional = roomRepository.findById(roomId);

        // room not found
        if (roomOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Room room = roomOptional.get();

        // create new equipment
        Equipment equipment = new Equipment(type);
        room.addEquipment(equipment);

        equipmentRepository.save(equipment);
        roomRepository.save(room);

        // add new equipment success
        return ResponseEntity.ok(room);
    }

    /**
     * Remove an equipment provided by its id.
     *
     * @param request     http request for the validation of the user. Only admin can perform this task.
     * @param equipmentId id of the equipment that needs to be removed.
     * @return unauthorized code if the user is not the admin. Else return ok.
     */
    @DeleteMapping("removeEquipment")
    public ResponseEntity<?> removeEquipment(HttpServletRequest request,
                                             @RequestBody Long equipmentId) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        String role = AuthenticateRequest.getAuthenticationResponse().getRole();

        // unauthorized to do this.
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentId);

        // The provided id is not valid
        if (equipmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Equipment equipment = equipmentOptional.get();
        Optional<Room> roomOptional = roomRepository.findRoomByEquipmentContaining(equipment);

        // the equipment is not in any rooms.
        if (roomOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Room room = roomOptional.get();

        // remove the equipment in the room
        room.removeEquipment(equipment);

        // remove the equipment from the database
        equipmentRepository.delete(equipment);

        return ResponseEntity.ok().build();
    }
}
