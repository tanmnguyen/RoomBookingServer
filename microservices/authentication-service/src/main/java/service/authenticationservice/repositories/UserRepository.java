package service.authenticationservice.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.authenticationservice.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByNetIdEquals(String netId);
}
