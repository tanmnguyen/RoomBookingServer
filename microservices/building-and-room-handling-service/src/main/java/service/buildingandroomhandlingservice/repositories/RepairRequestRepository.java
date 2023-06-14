package service.buildingandroomhandlingservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.RepairRequest;

@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long> {
}


