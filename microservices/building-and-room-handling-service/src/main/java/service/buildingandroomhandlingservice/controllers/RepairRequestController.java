package service.buildingandroomhandlingservice.controllers;


import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import jdk.jfr.Unsigned;
import lombok.extern.java.Log;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.buildingandroomhandlingservice.authentications.AuthenticateRequest;
import service.buildingandroomhandlingservice.authentications.Validate;
import service.buildingandroomhandlingservice.entities.AuthenticationResponse;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.RepairRequest;
import service.buildingandroomhandlingservice.entities.RepairRequestCreation;
import service.buildingandroomhandlingservice.repositories.EquipmentRepository;
import service.buildingandroomhandlingservice.repositories.RepairRequestRepository;


@Log
@Controller
@RequestMapping("/repairrequest")
public class RepairRequestController {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private RepairRequestRepository repairRequestRepository;

    /**
     * Update the availability of a piece of equipment in a specific room.
     * Any user can request a repair request.

     * @param request http request.
     * @param repairRequestCreation creation of the repair request.

     * @return http status.

     */
    @PostMapping("/createRepairRequest")
    @ResponseBody
    public ResponseEntity createRepairRequest(HttpServletRequest request,
                                              @RequestBody
                                                  RepairRequestCreation repairRequestCreation) {

        // Authenticate the request.
        ResponseEntity<Object> response = AuthenticateRequest.authenticate(request);
        if (response != null) {
            return response;
        }

        long equipmentId = repairRequestCreation.getEquipmentId();

        Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentId);

        if (equipmentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Equipment equipment = equipmentOptional.get();

        // update the status of the equipment to unavailable
        equipment.setStatus(Equipment.Availability.UNDER_MAINTENANCE);

        equipmentRepository.save(equipment);
        String description = repairRequestCreation.getDescription();

        // Create repair request.
        RepairRequest repairRequest = new RepairRequest(equipmentId, description);
        repairRequestRepository.save(repairRequest);

        return ResponseEntity.ok().build();
    }

}
