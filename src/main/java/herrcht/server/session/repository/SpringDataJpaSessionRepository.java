package herrcht.server.session.repository;

import herrcht.server.session.JpaSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataJpaSessionRepository extends JpaRepository<JpaSessionEntity, UUID> {

    List<JpaSessionEntity> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM PostgresSessionEntity s WHERE s.userId = :userId")
    void deleteAllByUserId(UUID userId);

    @Query("SELECT COUNT(s) FROM PostgresSessionEntity s WHERE s.userId = :userId AND s.revoked = false AND s.expiresAt > :now")
    long countActiveSessions(UUID userId, OffsetDateTime now);
}