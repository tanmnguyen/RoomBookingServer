package service.buildingandroomhandlingservice.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.buildingandroomhandlingservice.entities.Equipment;
import service.buildingandroomhandlingservice.entities.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findAll();

    Optional<Room> findById(String id);

    Optional<Room> findRoomByEquipmentContaining(Equipment equipment);

    Optional<List<Room>> findByBuildingNumber(int buildingNr);
}