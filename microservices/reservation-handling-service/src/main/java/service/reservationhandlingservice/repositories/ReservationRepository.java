package service.reservationhandlingservice.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.reservationhandlingservice.entities.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByHostIdAndReservationId(String hostId, Long reservationId);

    Optional<List<Reservation>> findAllByHostId(String hostId);

    Optional<Reservation> findByRoomIdAndStartTimeAndEndTime(String roomId, LocalDateTime startTime,
                                                             LocalDateTime finishTime);

    Optional<List<Reservation>> findAllByRoomId(String roomId);
}
