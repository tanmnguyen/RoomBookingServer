package service.reservationhandlingservice.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.reservationhandlingservice.entities.ResearchGroup;


@Repository
public interface ResearchGroupRepository extends JpaRepository<ResearchGroup, Long> {

    Optional<List<String>> findAllBySecretaryId(String secretaryId);

}
