package service.buildingandroomhandlingservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.buildingandroomhandlingservice.entities.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findBuildingById(long buildingId);
}
