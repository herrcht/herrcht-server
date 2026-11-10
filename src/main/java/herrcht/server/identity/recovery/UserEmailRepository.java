package herrcht.server.identity.recovery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEmailRepository extends JpaRepository<UserEmailEntity, UUID> {
    Optional<UserEmailEntity> findByEmail(String email);
    List<UserEmailEntity> findByUserId(UUID userId);
    boolean existsByEmail(String email);
}